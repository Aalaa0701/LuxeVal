package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


actual val platformModule = module {
    single<AppDatabase> {
        buildDatabase(androidContext())
    }

}