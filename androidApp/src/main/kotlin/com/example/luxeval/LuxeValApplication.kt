package com.example.luxeval

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.di.androidModule
import com.example.luxeval.di.initKoin
import com.example.luxeval.i18n.LanguageBootstrapper
import com.example.luxeval.presentation.report.CurrentActivityHolder
import org.koin.dsl.module

// LuxeValApplication.kt
class LuxeValApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CurrentActivityHolder.register(this)

        initKoin(platformModules = listOf(androidModule(this)))
        org.koin.core.context.GlobalContext.get().get<LanguageBootstrapper>().bootstrap()
    }
}