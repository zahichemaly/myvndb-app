#!/usr/bin/env python3
"""Download, crop and convert the theme wallpapers listed in wallpapers.json.

Each entry is downloaded into a temporary directory, cropped to the largest 1080:2400 window that fits
inside its clean `region` (centred on `focus`), scaled to 1080x2400 and encoded as WebP. The result goes
to app/src/main/res/drawable-nodpi/wallpaper_<name>.webp, or to build/wallpapers/ with --preview.

Run from anywhere (paths are relative to this file):
    python3 -I tools/wallpapers/fetch_wallpapers.py --preview            # contact sheet, res/ untouched
    python3 -I tools/wallpapers/fetch_wallpapers.py                      # write every wallpaper
    python3 -I tools/wallpapers/fetch_wallpapers.py --only umineko --force

Needs ImageMagick 7 (`magick`) and `cwebp` on PATH: `brew install imagemagick webp`.
Dev-only: never run by the Gradle build. See docs/plans/14-more-themes.md, section 3.
"""
import argparse
import json
import math
import re
import shutil
import subprocess
import sys
import tempfile
import urllib.parse
import urllib.request
from pathlib import Path

TOOL_DIR = Path(__file__).resolve().parent
REPO_ROOT = TOOL_DIR.parent.parent
MANIFEST = TOOL_DIR / "wallpapers.json"
DRAWABLE_DIR = REPO_ROOT / "app" / "src" / "main" / "res" / "drawable-nodpi"
PREVIEW_DIR = REPO_ROOT / "build" / "wallpapers"

# Only these hosts may serve a source image. Add a host here to use official art from another site.
ALLOWED_HOSTS = {"t.vndb.org"}
USER_AGENT = "MyVNDB-wallpaper-tool"
TIMEOUT_SECONDS = 60
MAX_DOWNLOAD_BYTES = 50 * 1024 * 1024
CHUNK_BYTES = 64 * 1024
UPSCALE_WARNING = 2.0
SHEET_COLUMNS = 7
# ImageMagick has no default font on macOS, so the contact sheet labels use the first one found here.
LABEL_FONTS = (
    "/System/Library/Fonts/Supplemental/Arial.ttf",
    "/Library/Fonts/Arial.ttf",
    "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
)

TOP_LEVEL_KEYS = {"output_size", "webp_quality", "wallpapers"}
ENTRY_KEYS = {"name", "vndb", "source", "region", "focus", "note"}
NAME_PATTERN = re.compile(r"^[a-z][a-z0-9_]*$")

# First bytes of each accepted image type, and the ImageMagick format prefix used to read it.
MAGIC_BYTES = (
    (b"\xff\xd8\xff", "jpeg"),
    (b"\x89PNG\r\n\x1a\n", "png"),
)


class ToolError(Exception):
    """A problem with the manifest, a download or an image. Stops the current entry."""


def require_binaries():
    missing = [name for name in ("magick", "cwebp") if shutil.which(name) is None]
    if missing:
        raise ToolError(f"missing {', '.join(missing)} on PATH (brew install imagemagick webp)")


def fractions(value, count, label):
    if not isinstance(value, list) or len(value) != count:
        raise ToolError(f"{label} must be a list of {count} numbers")
    if not all(isinstance(v, (int, float)) and not isinstance(v, bool) and 0 <= v <= 1 for v in value):
        raise ToolError(f"{label} values must be numbers between 0 and 1")
    return [float(v) for v in value]


def load_manifest(path):
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        raise ToolError(f"cannot read {path}: {error}") from error
    if not isinstance(data, dict) or set(data) != TOP_LEVEL_KEYS:
        raise ToolError(f"manifest must have exactly these keys: {sorted(TOP_LEVEL_KEYS)}")

    size = data["output_size"]
    if not (isinstance(size, list) and len(size) == 2 and all(isinstance(v, int) and v > 0 for v in size)):
        raise ToolError("output_size must be [width, height] in pixels")
    quality = data["webp_quality"]
    if not (isinstance(quality, int) and 1 <= quality <= 100):
        raise ToolError("webp_quality must be an integer from 1 to 100")

    entries, names = [], set()
    for index, entry in enumerate(data["wallpapers"]):
        label = f"wallpapers[{index}]"
        if not isinstance(entry, dict) or set(entry) != ENTRY_KEYS:
            raise ToolError(f"{label} must have exactly these keys: {sorted(ENTRY_KEYS)}")
        name = entry["name"]
        if not isinstance(name, str) or not NAME_PATTERN.match(name) or name in names:
            raise ToolError(f"{label}: name must be unique lowercase letters, digits and underscores")
        names.add(name)
        region = fractions(entry["region"], 4, f"{name}.region")
        if region[0] >= region[2] or region[1] >= region[3]:
            raise ToolError(f"{name}.region must be [x0, y0, x1, y1] with x0 < x1 and y0 < y1")
        focus = fractions(entry["focus"], 2, f"{name}.focus")
        source = entry["source"]
        if not isinstance(source, str):
            raise ToolError(f"{name}.source must be a URL")
        check_url(source)
        entries.append({"name": name, "source": source, "region": region, "focus": focus})
    return (size[0], size[1]), quality, entries


def check_url(url):
    parsed = urllib.parse.urlparse(url)
    if parsed.scheme != "https" or parsed.hostname not in ALLOWED_HOSTS:
        raise ToolError(f"{url}: only https URLs on {sorted(ALLOWED_HOSTS)} are allowed")


def download(url, work_dir):
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(request, timeout=TIMEOUT_SECONDS) as response:
            check_url(response.geturl())  # a redirect must stay on an allowed host
            declared = response.headers.get("Content-Length")
            if declared and declared.isdigit() and int(declared) > MAX_DOWNLOAD_BYTES:
                raise ToolError(f"{url}: {int(declared)} bytes is over the {MAX_DOWNLOAD_BYTES} byte cap")
            data = bytearray()
            while True:
                chunk = response.read(CHUNK_BYTES)
                if not chunk:
                    break
                data.extend(chunk)
                if len(data) > MAX_DOWNLOAD_BYTES:
                    raise ToolError(f"{url}: over the {MAX_DOWNLOAD_BYTES} byte cap")
    except OSError as error:  # URLError, HTTPError and timeouts are all OSErrors
        raise ToolError(f"{url}: download failed ({error})") from error
    path = work_dir / "source"
    path.write_bytes(bytes(data))
    return path, len(data)


def sniff_format(path):
    head = path.read_bytes()[:16]
    for magic, image_format in MAGIC_BYTES:
        if head.startswith(magic):
            return image_format
    if head[:4] == b"RIFF" and head[8:12] == b"WEBP":
        return "webp"
    raise ToolError("downloaded file is not a JPEG, PNG or WebP image")


def run(command):
    try:
        result = subprocess.run(command, check=True, capture_output=True, text=True)
    except subprocess.CalledProcessError as error:
        raise ToolError(f"{command[0]} failed: {error.stderr.strip() or error}") from error
    return result.stdout


def image_size(image_format, path):
    output = run(["magick", "identify", "-format", "%w %h", f"{image_format}:{path}[0]"])
    width, height = (int(v) for v in output.split())
    return width, height


def crop_box(width, height, region, focus, aspect):
    """The largest window of `aspect` (width / height) inside `region`, centred on `focus`, in pixels."""
    left, top = math.ceil(region[0] * width), math.ceil(region[1] * height)
    right, bottom = math.floor(region[2] * width), math.floor(region[3] * height)
    region_width, region_height = right - left, bottom - top
    if region_width / region_height > aspect:
        crop_height = region_height
        crop_width = math.floor(crop_height * aspect)
    else:
        crop_width = region_width
        crop_height = math.floor(crop_width / aspect)
    x = round(focus[0] * width - crop_width / 2)
    y = round(focus[1] * height - crop_height / 2)
    x = min(max(x, left), right - crop_width)
    y = min(max(y, top), bottom - crop_height)
    return crop_width, crop_height, x, y


def render(image_format, source, box, size, quality, work_dir):
    crop_width, crop_height, x, y = box
    png = work_dir / "out.png"
    webp = work_dir / "out.webp"
    run([
        "magick", f"{image_format}:{source}[0]",
        "-crop", f"{crop_width}x{crop_height}+{x}+{y}", "+repage",
        "-colorspace", "sRGB",
        "-filter", "Lanczos", "-resize", f"{size[0]}x{size[1]}!",
        "-strip", f"png:{png}",
    ])
    run(["cwebp", "-quiet", "-q", str(quality), "-m", "6", "-metadata", "none", str(png), "-o", str(webp)])
    if image_size("webp", webp) != size:
        raise ToolError(f"encoded image is not {size[0]}x{size[1]}")
    return webp


def process(entry, size, quality, destination):
    work_dir = Path(tempfile.mkdtemp(prefix="wallpaper-"))
    try:
        source, byte_count = download(entry["source"], work_dir)
        image_format = sniff_format(source)
        width, height = image_size(image_format, source)
        box = crop_box(width, height, entry["region"], entry["focus"], size[0] / size[1])
        webp = render(image_format, source, box, size, quality, work_dir)
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(webp), str(destination))
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)

    crop_width, crop_height, x, y = box
    scale = size[0] / crop_width
    warning = f"  WARNING: enlarged more than x{UPSCALE_WARNING:g}" if scale > UPSCALE_WARNING else ""
    print(
        f"{entry['name']}: {width}x{height} ({byte_count // 1024} KB) -> crop {crop_width}x{crop_height}+{x}+{y}"
        f" x{scale:.2f} -> {destination.stat().st_size // 1024} KB {destination.relative_to(REPO_ROOT)}{warning}"
    )


def contact_sheet(files, output):
    """All crops side by side, 7 per row, labeled with their names when a font is available."""
    font = next((f for f in LABEL_FONTS if Path(f).is_file()), None)
    if font:
        run(["magick", "montage", "-font", font, "-pointsize", "14", "-label", "%t"]
            + [f"webp:{f}" for f in files]
            + ["-tile", "7x", "-geometry", "216x480+6+6", "-background", "#202020", "-fill", "white",
               f"png:{output}"])
        return
    # No font: plain rows in manifest order, so no text has to be drawn.
    command = ["magick", "-background", "#202020"]
    for start in range(0, len(files), SHEET_COLUMNS):
        row = [f"webp:{f}" for f in files[start:start + SHEET_COLUMNS]]
        command += ["("] + row + ["-resize", "216x480", "-bordercolor", "#202020", "-border", "6", "+append", ")"]
    command += ["-append", f"png:{output}"]
    run(command)


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--preview", action="store_true",
                        help="write crops and a contact sheet to build/wallpapers/ instead of res/")
    parser.add_argument("--only", action="append", metavar="NAME", help="process only this entry (repeatable)")
    parser.add_argument("--force", action="store_true", help="overwrite existing wallpapers in res/")
    args = parser.parse_args()

    try:
        require_binaries()
        size, quality, entries = load_manifest(MANIFEST)
    except ToolError as error:
        sys.exit(f"error: {error}")

    if args.only:
        unknown = set(args.only) - {e["name"] for e in entries}
        if unknown:
            sys.exit(f"error: not in the manifest: {', '.join(sorted(unknown))}")
        entries = [e for e in entries if e["name"] in args.only]

    failures, written = [], []
    for entry in entries:
        if args.preview:
            destination = PREVIEW_DIR / f"{entry['name']}.webp"
        else:
            destination = DRAWABLE_DIR / f"wallpaper_{entry['name']}.webp"
            if destination.exists() and not args.force:
                failures.append(entry["name"])
                print(f"{entry['name']}: {destination.relative_to(REPO_ROOT)} exists, use --force to replace it")
                continue
        try:
            process(entry, size, quality, destination)
            written.append(destination)
        except ToolError as error:
            failures.append(entry["name"])
            print(f"{entry['name']}: error: {error}")

    if args.preview and written:
        sheet = PREVIEW_DIR / "preview.png"
        try:
            contact_sheet(written, sheet)
            print(f"contact sheet: {sheet.relative_to(REPO_ROOT)}")
        except ToolError as error:
            print(f"contact sheet: error: {error} (the crops are still in {PREVIEW_DIR.relative_to(REPO_ROOT)})")
    if failures:
        sys.exit(f"failed: {', '.join(failures)}")


if __name__ == "__main__":
    main()
