package com.example.luxeval.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val settingsModule = module {
    single<Settings> { Settings() } // no-arg factory handles both platforms
}