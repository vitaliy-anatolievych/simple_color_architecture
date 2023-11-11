package com.study.core.viewmodels

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.study.core.R
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.utils.Event
import com.study.core.utils.ResourceActions
import com.study.core.views.ARG_SCREEN
import com.study.core.views.BaseScreen
import com.study.core.views.LiveEvent
import com.study.core.views.MutableLiveEvent

open class CoreViewModel(
    application: Application
): AndroidViewModel(application), Navigator, UiActions {

    val whenActivityActive = ResourceActions<AppCompatActivity>()

    private val _result = MutableLiveEvent<Any>()
    val result: LiveEvent<Any> = _result

    override fun launch(screen: BaseScreen) = whenActivityActive {
        launchFragment(it, screen)
    }

    override fun goBack(result: Any?) = whenActivityActive {
        if (result != null) {
            _result.value = Event(result)
        }
        it.onBackPressed()
    }

    override fun toast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        TODO()
    }

    fun launchFragment(activity: AppCompatActivity, screen: BaseScreen, addToBackStack: Boolean = true) {
        // as screen classes are inside fragments -> we can create fragment directly from screen
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        // set screen object as fragment's argument
        fragment.arguments = bundleOf(ARG_SCREEN to screen)

        val transaction = activity.supportFragmentManager.beginTransaction()
        val root = activity.findViewById<View>(android.R.id.content)

        if (addToBackStack) transaction.addToBackStack(null)
        transaction
            .setCustomAnimations(
                R.anim.enter,
                R.anim.exit,
                R.anim.pop_enter,
                R.anim.pop_exit
            )

            .replace(root.id, fragment)
            .commit()
    }
}