package com.zc.vndb.feature.detail.presentation

/** A part of the page that loads the first time it is needed and has its own Retry. */
interface LazySection {

    /** Loads unless the section was requested before; a failed load waits for [load]. */
    fun loadOnce()

    /** Loads from the start (a retry), unless a load is running. */
    fun load()
}
