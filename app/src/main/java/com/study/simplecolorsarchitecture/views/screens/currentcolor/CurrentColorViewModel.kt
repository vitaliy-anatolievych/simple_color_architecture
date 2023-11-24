package com.study.simplecolorsarchitecture.views.screens.currentcolor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.model.ErrorResult
import com.study.core.model.PendingResult
import com.study.core.model.SuccessResult
import com.study.core.model.takeSuccess
import com.study.core.views.BaseViewModel
import com.study.core.views.LiveResult
import com.study.core.views.MediatorLiveResult
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

    // input sources
    private val _currentColorState: MutableLiveResult<NamedColor> =
        MutableLiveResult(PendingResult())
    private val _currentColor: MutableLiveData<NamedColor> =
        savedStateHandle.getLiveData(CURRENT_COLOR, colorsRepository.currentColor)


    private val _colorMediator = MediatorLiveResult<NamedColor>()
    val currentColor: LiveResult<NamedColor> = _colorMediator


    init {
        _currentColorState.value =
            SuccessResult(_currentColor.value ?: colorsRepository.currentColor)

        _colorMediator.addSource(_currentColor) { mergeSources() }
        _colorMediator.addSource(_currentColorState) { mergeSources() }
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
            _currentColorState.postValue(SuccessResult(result))
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    private fun mergeSources() {
        val currentColor = _currentColor.value ?: return
        val currentColorState = _currentColorState.value ?: return

        _colorMediator.value = currentColorState.map { color ->
            _currentColor.postValue(color)
            currentColor
        }
    }

    fun tryAgain() {
        // todo: mocking long-running reloading for view
        viewModelScope.launch {
            _currentColorState.postValue(PendingResult())
            delay(2000)
            currentColor.value.takeSuccess().runCatching {
                _currentColorState.postValue(SuccessResult(this ?: colorsRepository.currentColor))
            }.onFailure {
                _currentColorState.postValue(ErrorResult(Exception()))
            }
        }
    }

    companion object {
        private const val CURRENT_COLOR = "currentColor"
    }
}