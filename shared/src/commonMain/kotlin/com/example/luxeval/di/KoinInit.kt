package com.example.luxeval.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Single entry point for both platforms. Each platform supplies its own
 * database module (the only piece that needs a platform Context/paths);
 * everything else is shared.
 */
fun initKoin(platformModules: List<Module> = emptyList()) {
    startKoin {
        modules(platformModules + listOf(daoModule, repositoryModule, settingsModule, viewModelModule))
    }
}