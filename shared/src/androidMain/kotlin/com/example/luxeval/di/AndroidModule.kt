package com.example.luxeval.di

import android.content.Context
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.data.network.ConnectivityObserver
import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.report.CurrentActivityHolder
import com.example.luxeval.presentation.report.PdfExporter
import com.example.luxeval.presentation.share.FileSharer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidModule(context: Context) = module {

    single<AppDatabase> {
        buildDatabase(context)
    }

    single<PhotoStorage> {
        PhotoStorage(context)
    }

//    single { PdfExporter(context) }
    single { FileSharer(context) }
    single { PdfExporter(context) }
    // di/AndroidModule.kt — add
    single { ConnectivityObserver(context) }

}