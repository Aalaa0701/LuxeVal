// shared/src/androidMain/kotlin/com/example/luxeval/presentation/report/CurrentActivityHolder.kt
package com.example.luxeval.presentation.report

import android.app.Activity
import android.app.Application
import android.os.Bundle

object CurrentActivityHolder {
    var current: Activity? = null
        private set

    fun register(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) { current = activity }
            override fun onActivityPaused(activity: Activity) {
                if (current == activity) current = null
            }
            override fun onActivityCreated(a: Activity, b: Bundle?) {}
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })
    }
}