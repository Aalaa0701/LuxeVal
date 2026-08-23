package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.presentation.photo.PhotoStorage
import org.koin.dsl.module

fun initKoinIos() {
    val iosDatabaseModule = module {
        single<AppDatabase> { buildDatabase() }
        single { PhotoStorage() }
    }
    initKoin(platformModules = listOf(iosDatabaseModule))
}