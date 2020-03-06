package io.github.wulkanowy.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class DebugLogTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "Wulkanowy", message, t)
    }
}

private fun Bundle?.checkSavedState() = if (this == null) "(STATE IS NULL)" else ""

class ActivityLifecycleLogger : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} PAUSED") }
    }

    override fun onActivityResumed(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} RESUMED") }
    }

    override fun onActivityStarted(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} STARTED") }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} DESTROYED") }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        activity?.let { Timber.d("${it::class.java.simpleName} SAVED INSTANCE STATE") }
    }

    override fun onActivityStopped(activity: Activity?) {
        activity?.let { Timber.d("${it::class.java.simpleName} STOPPED") }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let { Timber.d("${it::class.java.simpleName} CREATED ${savedInstanceState.checkSavedState()}") }
    }
}

@Singleton
class FragmentLifecycleLogger @Inject constructor() : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        Timber.d("${f::class.java.simpleName} VIEW CREATED ${savedInstanceState.checkSavedState()}")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} STOPPED")
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Timber.d("${f::class.java.simpleName} CREATED ${savedInstanceState.checkSavedState()}")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} RESUMED")
    }

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        Timber.d("${f::class.java.simpleName} ATTACHED")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} DESTROYED")
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        Timber.d("${f::class.java.simpleName} SAVED INSTANCE STATE")
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} STARTED")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} VIEW DESTROYED")
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Timber.d("${f::class.java.simpleName} ACTIVITY CREATED ${savedInstanceState.checkSavedState()}")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} PAUSED")
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        Timber.d("${f::class.java.simpleName} DETACHED")
    }
}
