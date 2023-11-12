package com.study.core.navigator

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.study.core.contracts.Navigator
import com.study.core.utils.Animations
import com.study.core.utils.Event
import com.study.core.views.ARG_SCREEN
import com.study.core.views.BaseFragment
import com.study.core.views.BaseScreen

/**
 * Реалізує сам навігатор, аскільки працює на стороні Activity,
 * то витоків пам'яті не буде.
 */
@RequiresApi(Build.VERSION_CODES.Q)
class StackFragmentNavigator(
    private val activity: AppCompatActivity,
    private val savedInstanceState: Bundle?,
    private val navigatorManager: NavigatorManager,
    @IdRes private val containerId: Int,
    private val animations: Animations? = null,
    private val initialScreenCreator: () -> BaseScreen
) : Navigator {

    private var result: Event<Any>? = null


    init {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {

            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when(event) {
                    Lifecycle.Event.ON_CREATE -> {

                        if (savedInstanceState == null) {
                            // define the initial screen that should be launched when app starts.
                            launchFragment(
                                screen = initialScreenCreator(),
                                addToBackStack = false
                            )
                        }
                        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                            fragmentCallbacks,
                            false
                        )
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        navigatorManager.setTarget(this@StackFragmentNavigator)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        navigatorManager.setTarget(null)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                            fragmentCallbacks
                        )
                    }
                    else -> {}
                }
            }

        })
    }

    override fun launch(screen: BaseScreen) {
        launchFragment(screen)
    }

    override fun goBack(result: Any?) {
        if (result != null) {
            this.result = Event(result)
        }
        activity.onBackPressed()
    }

    fun launchFragment(screen: BaseScreen, addToBackStack: Boolean = true) {
        // as screen classes are inside fragments -> we can create fragment directly from screen
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        // set screen object as fragment's argument
        fragment.arguments = bundleOf(ARG_SCREEN to screen)

        val transaction = activity.supportFragmentManager.beginTransaction()

        if (addToBackStack) transaction.addToBackStack(null)
        animations?.let { animations ->
            transaction.setCustomAnimations(
                animations.enterAnim,
                animations.exitAnim,
                animations.popEnterAnim,
                animations.popExitAnim,
            )
        }
        transaction
            .replace(containerId, fragment)
            .commit()
    }

    fun notifyScreenUpdates() {
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            // more than 1 screen -> show back button in the toolbar
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun publishResults(fragment: Fragment) {
        val result = result?.getValue() ?: return
        if (fragment is BaseFragment) {
            // has result that can be delivered to the screen's view-model
            fragment.viewModel.onResult(result)
        }
    }

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            notifyScreenUpdates()
            publishResults(f)
        }
    }
}