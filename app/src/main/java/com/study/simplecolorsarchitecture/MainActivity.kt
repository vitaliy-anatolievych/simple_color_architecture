package com.study.simplecolorsarchitecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.study.core.contracts.NotifyAdapter
import com.study.core.viewmodels.CoreViewModel
import com.study.core.views.BaseFragment
import com.study.simplecolorsarchitecture.views.contracts.HasScreenTitle
import com.study.simplecolorsarchitecture.views.screens.currentcolor.CurrentColorFragment

/**
 * Приклад реалізації Activity
 */
class MainActivity : AppCompatActivity(), NotifyAdapter {

    /**
     * 1. Додається [CoreViewModel] та далі рееструється callback
     * на життевий цикл
     */
    private val activityViewModel by viewModels<CoreViewModel> {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // початковий єкран якщо додаток відкрито вперше
            activityViewModel.launchFragment(
                activity = this,
                screen = CurrentColorFragment.Screen(),
                addToBackStack = false
            )
        }

        // реєстрація життевого циклу фрагменту
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, false)
    }

    override fun onDestroy() {
        // відписка від життевого циклу фрагменту
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // стає можливою навігація лише якщо acivity існує
        activityViewModel.whenActivityActive.resource = this
    }

    override fun onPause() {
        super.onPause()
        // стираємо посилання на acivity
        activityViewModel.whenActivityActive.resource = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val containerId: Int
        get() = R.id.fragmentContainer

    override fun notifyScreenUpdates() {
        val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (supportFragmentManager.backStackEntryCount > 0) {
            // more than 1 screen -> show back button in the toolbar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            // fragment has custom screen title -> display it
            supportActionBar?.title = f.getScreenTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }

        val result = activityViewModel.result.value?.getValue() ?: return
        if (f is BaseFragment) {
            // has result that can be delivered to the screen's view-model
            f.viewModel.onResult(result)
        }
    }

    /**
     * У [onFragmentViewCreated] сповіщуємо які зміни треба оновити
     */
    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            notifyScreenUpdates()
        }
    }
}