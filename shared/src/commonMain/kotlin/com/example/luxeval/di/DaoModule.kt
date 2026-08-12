package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import org.koin.dsl.module

val daoModule = module {
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().clientDao() }
    single { get<AppDatabase>().appraisalDao() }
}