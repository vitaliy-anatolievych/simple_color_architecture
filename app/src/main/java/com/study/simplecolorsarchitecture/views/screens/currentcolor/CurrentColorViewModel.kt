package com.study.simplecolorsarchitecture.views.screens.currentcolor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.utils.ErrorResult
import com.study.core.utils.PendingResult
import com.study.core.utils.SuccessResult
import com.study.core.utils.takeSuccess
import com.study.core.views.BaseViewModel
import com.study.core.views.MutableLiveResult
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.model.colors.ColorsRepository
import com.study.simplecolorsarchitecture.model.colors.NamedColor
import com.study.simplecolorsarchitecture.views.screens.changecolor.ChangeColorFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _currentColor: MutableLiveData<NamedColor> =
        savedStateHandle.getLiveData(CURRENT_COLOR, colorsRepository.currentColor)

    val currentColor: MutableLiveResult<NamedColor> = MutableLiveResult(PendingResult())

    init {
        viewModelScope.launch {
            delay(2000)
            currentColor.postValue(ErrorResult(Exception()))
        }
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
            currentColor.postValue(SuccessResult(result))
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        // todo: mocking long-running reloading for view
        viewModelScope.launch {
            currentColor.postValue(PendingResult())
            delay(2000)
            currentColor.value.takeSuccess().runCatching {
                currentColor.postValue(SuccessResult(this ?: colorsRepository.currentColor))
            }.onFailure {
                currentColor.postValue(ErrorResult(Exception()))
            }
        }
    }

    companion object {
        private const val CURRENT_COLOR = "currentColor"
    }
}