package com.example.luxeval.di

import android.content.Context
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.buildDatabase
import com.example.luxeval.presentation.photo.PhotoStorage
import org.koin.dsl.module

fun androidModule(context: Context) = module {

    single<AppDatabase> {
        buildDatabase(context)
    }

    single<PhotoStorage> {
        PhotoStorage(context)
    }
}