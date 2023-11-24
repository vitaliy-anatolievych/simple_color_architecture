package com.study.core.viewmodels

import androidx.lifecycle.ViewModel
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.navigator.NavigatorManager

open class CoreViewModel(
    val uiActions: UiActions,
    val navigatorManager: NavigatorManager
): ViewModel(),
    Navigator by navigatorManager,
    UiActions by uiActions {

    override fun onCleared() {
        super.onCleared()
        navigatorManager.clear()
    }

}