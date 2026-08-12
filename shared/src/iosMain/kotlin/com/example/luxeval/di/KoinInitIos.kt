package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import org.koin.dsl.module

fun initKoinIos() {
    val iosDatabaseModule = module {
        single<AppDatabase> { buildDatabase() }
    }
    initKoin(platformModules = listOf(iosDatabaseModule))
}