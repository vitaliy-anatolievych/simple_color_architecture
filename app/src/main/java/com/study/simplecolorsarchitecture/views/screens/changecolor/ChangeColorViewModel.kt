package com.study.simplecolorsarchitecture.views.screens.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.utils.PendingResult
import com.study.core.utils.SuccessResult
import com.study.core.views.BaseViewModel
import com.study.core.views.LiveResult
import com.study.core.views.MediatorLiveResult
import com.study.core.views.MutableLiveResult
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.model.colors.ColorsRepository
import com.study.simplecolorsarchitecture.model.colors.NamedColor
import com.study.simplecolorsarchitecture.views.screens.utils.Transformations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangeColorViewModel(
    screen: ChangeColorFragment.Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId = savedStateHandle.getLiveData("currentColorId", screen.id)
    private val _saveInProgress = MutableLiveData(false)

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = Transformations.map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.app_name)
        }
    }

    init {
        viewModelScope.launch {
            delay(1000)
//            _availableColors.value = ErrorResult(Exception())
            _availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
        }
        // initializing MediatorLiveData
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        viewModelScope.launch {
            _saveInProgress.postValue(true)
            delay(1500)
            val currentColorId = _currentColorId.value ?: return@launch
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.currentColor = currentColor
            navigator.goBack(result = currentColor)
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
    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        _viewState.value = colors.map { colorsList ->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showProgressBar = saveInProgress
            )
        }
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(1000)
            _availableColors.postValue(SuccessResult(colorsRepository.getAvailableColors()))
        }
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showProgressBar: Boolean
    )
}