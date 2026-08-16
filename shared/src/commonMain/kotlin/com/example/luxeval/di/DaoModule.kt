package com.example.luxeval.di

import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.database.daos.AppraisalDao
import com.example.luxeval.data.database.daos.ClientDao
import com.example.luxeval.data.database.daos.ItemDao
import org.koin.core.module.Module
import org.koin.dsl.module

val daoModule = module {
    single<ItemDao> {
        get<AppDatabase>().itemDao()
    }

    single<ClientDao> {
        get<AppDatabase>().clientDao()
    }

    single<AppraisalDao> {
        get<AppDatabase>().appraisalDao()
    }
}
expect val platformModule: Module