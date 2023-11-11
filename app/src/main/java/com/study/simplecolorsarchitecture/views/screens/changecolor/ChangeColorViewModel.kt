package com.study.simplecolorsarchitecture.views.screens.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.views.BaseViewModel
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.model.colors.ColorsRepository
import com.study.simplecolorsarchitecture.model.colors.NamedColor

class ChangeColorViewModel(
    screen: ChangeColorFragment.Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveData<List<NamedColor>>()
    private val _currentColorId = savedStateHandle.getLiveData("currentColorId", screen.id)

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _colorsList = MediatorLiveData<List<NamedColorListItem>>()
    val colorsList: LiveData<List<NamedColorListItem>> = _colorsList

    // side destination, also the same result can be achieved by using Transformations.map() function.
    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        _availableColors.value = colorsRepository.getAvailableColors()
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
        val currentColor = colors.first { it.id == currentColorId }
        _colorsList.value = colors.map { NamedColorListItem(it, currentColorId == it.id) }
        _screenTitle.value = uiActions.getString(R.string.change_color_screen_title, currentColor.name)
    }

}