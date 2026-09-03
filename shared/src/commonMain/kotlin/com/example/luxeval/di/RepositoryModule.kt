package com.example.luxeval.di


import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.repository.AuthRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.presentation.theme.ThemeController
import org.koin.dsl.module

val repositoryModule = module {
    single { ItemRepository(get(), get()) }
    single { ClientRepository(get()) }
    single { PricingRepository(get()) }
    single { AuthRepository(get()) }
    single { AppraisalRepository(get()) }
    single { AppraiserProfileStore(get()) }
    single { AppraiserProfileStore(get()) }
    single { AppSettingsStore(get()) }
    single { ThemeController(get()) }
    single { SyncRepository(get(), get(), get(), get()) }
}