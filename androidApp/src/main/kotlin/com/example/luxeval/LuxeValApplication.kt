package com.example.luxeval

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.di.androidModule
import com.example.luxeval.di.initKoin
import org.koin.dsl.module

// LuxeValApplication.kt
class LuxeValApplication : Application() {
    var currentActivity: Activity? = null
        private set

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) { currentActivity = activity }
            override fun onActivityPaused(activity: Activity) {
                if (currentActivity == activity) currentActivity = null
            }
            override fun onActivityCreated(a: Activity, b: Bundle?) {}
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })

        initKoin(platformModules = listOf(androidModule(this)))
    }
}