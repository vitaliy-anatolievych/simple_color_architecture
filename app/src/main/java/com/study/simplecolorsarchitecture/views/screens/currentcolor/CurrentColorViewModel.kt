package com.study.simplecolorsarchitecture.views.screens.currentcolor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.views.BaseViewModel
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.model.colors.ColorsRepository
import com.study.simplecolorsarchitecture.model.colors.NamedColor
import com.study.simplecolorsarchitecture.views.screens.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val currentColor: MutableLiveData<NamedColor> =
        savedStateHandle.getLiveData(CURRENT_COLOR, colorsRepository.currentColor)

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
            currentColor.postValue(result)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    companion object {
        private const val CURRENT_COLOR = "currentColor"
    }
}