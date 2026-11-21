package com.zc.vndb.feature.userlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.userlist.domain.model.InvalidListEntryException
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.usecase.GetEditableLabelsUseCase
import com.zc.vndb.feature.userlist.domain.usecase.GetMyListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.RemoveListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.SaveListEntryUseCase
import com.zc.vndb.feature.userlist.presentation.mapper.toPickerDate
import com.zc.vndb.feature.userlist.presentation.mapper.toPickerMillis
import com.zc.vndb.feature.userlist.presentation.mapper.withDraft
import com.zc.vndb.feature.userlist.presentation.model.DateField
import com.zc.vndb.feature.userlist.presentation.model.ListEditorArgs
import com.zc.vndb.feature.userlist.presentation.model.ListEditorDialog
import kotlin.time.Clock
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

/**
 * Edits the signed-in user's entry for one VN. Changes stay local until Save, which writes the
 * whole entry (adding the VN to the list when it isn't there yet); Remove asks first.
 */
@KoinViewModel
class ListEditorViewModel(
    @InjectedParam args: ListEditorArgs,
    private val getMyListEntry: GetMyListEntryUseCase,
    private val getEditableLabels: GetEditableLabelsUseCase,
    private val saveListEntry: SaveListEntryUseCase,
    private val removeListEntry: RemoveListEntryUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val novelId = args.novelId

    private val _state = MutableStateFlow(
        ListEditorState(novelTitle = UiText.StringValue(args.novelTitle)),
    )
    val state: StateFlow<ListEditorState> = _state.asStateFlow()

    private val _effect = Channel<ListEditorEffect>(Channel.BUFFERED)
    val effect: Flow<ListEditorEffect> = _effect.receiveAsFlow()

    private var draft = MyListEntry(novelId = novelId)

    /** The entry as loaded; null when the VN was not on the list. */
    private var original: MyListEntry? = null
    private var labels: List<ListLabel> = emptyList()

    init {
        load()
    }

    @Suppress("CyclomaticComplexMethod") // A flat dispatch over the editor's intents.
    fun onIntent(intent: ListEditorIntent) {
        when (intent) {
            ListEditorIntent.Retry -> load()

            is ListEditorIntent.StatusSelected -> edit {
                val others = it.labelIds - MyListEntry.STATUS_LABELS
                val isSelected = intent.labelId in it.labelIds
                it.copy(labelIds = if (isSelected) others else others + intent.labelId)
            }

            is ListEditorIntent.LabelToggled -> edit {
                val isSelected = intent.labelId in it.labelIds
                it.copy(
                    labelIds = if (isSelected) {
                        it.labelIds - intent.labelId
                    } else {
                        it.labelIds +
                            intent.labelId
                    },
                )
            }

            is ListEditorIntent.VoteChanged -> edit { it.copy(vote = intent.vote) }

            ListEditorIntent.VoteCleared -> edit { it.copy(vote = null) }

            is ListEditorIntent.DateClicked -> _state.update {
                it.copy(
                    dialog = ListEditorDialog.PickDate(
                        intent.field,
                        draft.date(intent.field)?.toPickerMillis(),
                    ),
                )
            }

            is ListEditorIntent.DatePicked -> {
                closeDialog()
                edit { it.withDate(intent.field, intent.millis.toPickerDate()) }
            }

            is ListEditorIntent.DateCleared -> edit { it.withDate(intent.field, null) }

            is ListEditorIntent.NotesChanged -> edit { it.copy(notes = intent.notes) }

            ListEditorIntent.SaveClicked -> save()

            ListEditorIntent.RemoveClicked -> _state.update {
                it.copy(
                    dialog = ListEditorDialog.ConfirmRemove,
                )
            }

            ListEditorIntent.RemoveConfirmed -> remove()

            ListEditorIntent.DialogDismissed -> closeDialog()
        }
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val entry = async { getMyListEntry(novelId) }
            val labelsResult = async { getEditableLabels() }
            val failure = entry.await().exceptionOrNull() ?: labelsResult.await().exceptionOrNull()
            if (failure != null) {
                _state.update { it.copy(isLoading = false, error = failure.toErrorUIModel()) }
                return@launch
            }
            val existing = entry.await().getOrNull()
            labels = labelsResult.await().getOrNull().orEmpty()
            original = existing
            draft = existing ?: MyListEntry(novelId = novelId)
            _state.update {
                it.copy(
                    isLoading = false,
                    isOnList = existing != null,
                ).render()
            }
        }
    }

    private fun save() {
        if (_state.value.isSaving || !_state.value.isValid) return
        _state.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            saveListEntry(draft, original)
                .onSuccess { _effect.send(ListEditorEffect.Close) }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saveError = error.toSaveError(),
                        )
                    }
                }
        }
    }

    private fun Throwable.toSaveError(): UiText = when (this) {
        is InvalidListEntryException -> UiText.StringResource(
            StringResource.list_editor_error_invalid,
        )

        else -> toErrorUIModel().message
    }

    private fun ListEditorState.render(): ListEditorState {
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        return withDraft(draft, original, labels, draft.problems(original, today))
    }

    private fun remove() {
        closeDialog()
        if (_state.value.isSaving) return
        _state.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            removeListEntry(novelId)
                .onSuccess { _effect.send(ListEditorEffect.Close) }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saveError = error.toErrorUIModel().message,
                        )
                    }
                }
        }
    }

    private fun edit(transform: (MyListEntry) -> MyListEntry) {
        draft = transform(draft)
        _state.update { it.copy(saveError = null).render() }
    }

    private fun closeDialog() {
        _state.update { it.copy(dialog = null) }
    }

    private fun MyListEntry.date(field: DateField) = when (field) {
        DateField.Started -> started
        DateField.Finished -> finished
    }

    private fun MyListEntry.withDate(field: DateField, date: LocalDate?) = when (field) {
        DateField.Started -> copy(started = date)
        DateField.Finished -> copy(finished = date)
    }
}
