package com.study.simplecolorsarchitecture.views.screens.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.utils.ErrorResult
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
import java.lang.Exception

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

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _colorsList = MediatorLiveResult<List<NamedColorListItem>>()
    val colorsList: LiveResult<List<NamedColorListItem>> = _colorsList

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = Transformations.map(colorsList) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.first { it.selected }
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
        _colorsList.addSource(_availableColors) { mergeSources() }
        _colorsList.addSource(_currentColorId) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        val currentColorId = _currentColorId.value ?: return
        val currentColor = colorsRepository.getById(currentColorId)
        colorsRepository.currentColor = currentColor
        navigator.goBack(result = currentColor)
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

        _colorsList.value = colors.map { colorsList ->
            colorsList.map { NamedColorListItem(it, currentColorId == it.id) }
        }
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(1000)
            _availableColors.postValue(SuccessResult(colorsRepository.getAvailableColors()))
        }
    }

}