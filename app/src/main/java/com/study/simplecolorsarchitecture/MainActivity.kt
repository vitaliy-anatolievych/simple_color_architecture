package com.study.simplecolorsarchitecture

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.study.core.contracts.FragmentsHolder
import com.study.core.navigator.NavigatorManager
import com.study.core.navigator.StackFragmentNavigator
import com.study.core.uiactions.AndroidUIActions
import com.study.core.utils.Animations
import com.study.core.utils.viewModelCreator
import com.study.core.viewmodels.CoreViewModel
import com.study.simplecolorsarchitecture.views.contracts.HasScreenTitle
import com.study.simplecolorsarchitecture.views.screens.currentcolor.CurrentColorFragment

/**
 * Приклад реалізації Activity
 */
class MainActivity : AppCompatActivity(), FragmentsHolder {

    /**
     * 2. Оголосити сам навігатор, типу [StackFragmentNavigator]
     */
    private lateinit var navigator: StackFragmentNavigator

    /**
     * 1. Створити [CoreViewModel] так передати UIActions та
     * NavigatorManager
     */
    private val activityViewModel by viewModelCreator<CoreViewModel> {
        CoreViewModel(
            uiActions = AndroidUIActions(applicationContext),
            navigatorManager = NavigatorManager(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3. Створити StackFragmentNavigator в onCreate
        navigator = StackFragmentNavigator(
            activity = this,
            navigatorManager = activityViewModel.navigatorManager,
            savedInstanceState = savedInstanceState,
            containerId = R.id.fragmentContainer,
            animations = Animations(
                enterAnim = R.anim.enter,
                exitAnim = R.anim.exit,
                popEnterAnim = R.anim.pop_enter,
                popExitAnim = R.anim.pop_exit
            ),
            initialScreenCreator = { CurrentColorFragment.Screen() }
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * 4. У notifyScreenUpdates прокинути navigator.notifyScreenUpdates()
     */
    override fun notifyScreenUpdates() {
        navigator.notifyScreenUpdates()
        val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            // fragment has custom screen title -> display it
            supportActionBar?.title = f.getScreenTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }
    }

    /**
     * 5. Повернути єкземпляр класу [CoreViewModel] до Core модулю
     */
    override fun getActivityScopeViewModel(): CoreViewModel {
        return activityViewModel
    }

}