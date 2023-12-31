package com.study.simplecolorsarchitecture.views.screens.changecolor

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.model.ErrorResult
import com.study.core.model.FinalResult
import com.study.core.model.PendingResult
import com.study.core.model.Result
import com.study.core.model.SuccessResult
import com.study.core.model.tasks.TasksFactory
import com.study.core.views.BaseViewModel
import com.study.core.views.LiveResult
import com.study.core.views.MediatorLiveResult
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.model.colors.ColorsRepository
import com.study.simplecolorsarchitecture.model.colors.NamedColor
import com.study.simplecolorsarchitecture.views.screens.utils.Transformations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ChangeColorViewModel(
    screen: ChangeColorFragment.Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    private val tasksFactory: TasksFactory,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId = savedStateHandle.getCustomStateFlow("currentColorId", screen.id)
    private val _saveInProgress = MutableStateFlow(false)

    // zip у Flow, передати можна до 5 параметрів
    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _saveInProgress,
        ::mergeSources
    )

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) {
                val currentColor = result.data.colorsList.first { it.selected }
                uiActions.getString(
                    R.string.change_color_screen_title,
                    currentColor.namedColor.name
                )
            } else {
                uiActions.getString(R.string.app_name)
            }
        }
        .asLiveData()

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.value = true
            val currentColorId = _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor).collect {
                print("$it")
            }
            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException) uiActions.toast(uiActions.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    /**
     * [MediatorLiveData] може прослуховувати інші екземпляри [LiveData] (навіть більше 1) і комбінувати їхні значення.
     *
     * Тут ми прослуховуємо список доступних кольорів ([_availableColors] live-data) + поточний ідентифікатор кольору ([_currentColorId] live-data),
     * а потім використовуємо обидва ці значення для створення списку [NamedColorListItem], який буде відображено у [RecyclerView].
     */
    private fun mergeSources(
        colors: Result<List<NamedColor>>,
        currentColorId: Long,
        saveInProgress: Boolean
    ): Result<ViewState> {
        return colors.map { colorsList ->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showProgressBar = saveInProgress
            )
        }
    }

    fun tryAgain() {
        load()
    }

    private fun load() = into(_availableColors) { colorsRepository.getAvailableColors() }

    private fun onSaved(result: FinalResult<NamedColor>) {
        _saveInProgress.value = false
        when (result) {
            is SuccessResult -> navigator.goBack(result.data)
            is ErrorResult -> uiActions.toast(uiActions.getString(R.string.error_happened))
        }
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showProgressBar: Boolean
    )
}