package com.example.luxeval

import android.app.Application
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.di.androidModule
import com.example.luxeval.di.initKoin
import org.koin.dsl.module

class LuxeValApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        val androidDatabaseModule = module {
//            single<AppDatabase> { buildDatabase(this@LuxeValApplication) }
//        }
//        initKoin(platformModules = listOf(androidDatabaseModule))
        initKoin(
            platformModules = listOf(
                androidModule(this)
            )
        )
    }
}