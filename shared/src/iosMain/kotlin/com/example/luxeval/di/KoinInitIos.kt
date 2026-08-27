package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.report.PdfExporter
import org.koin.dsl.module

fun initKoinIos() {
    val iosDatabaseModule = module {
        single<AppDatabase> { buildDatabase() }
        single { PhotoStorage() }
        single { PdfExporter() }
    }
    initKoin(platformModules = listOf(iosDatabaseModule))
}