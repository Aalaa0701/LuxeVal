package com.example.luxeval.di


import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { ItemRepository(get()) }
    single { ClientRepository(get()) }
    single { AppraisalRepository(get()) }
    single { AppraiserProfileStore(get()) }
}