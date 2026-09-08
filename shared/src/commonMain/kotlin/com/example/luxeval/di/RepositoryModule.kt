package com.example.luxeval.di


import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.database.AppDatabase
import com.example.luxeval.data.repository.AuthRepository
import com.example.luxeval.data.repository.BusinessProfileRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PhotoRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.data.repository.SessionRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.usecase.DeleteAppraisalUseCase
import com.example.luxeval.presentation.theme.ThemeController
import org.koin.dsl.module

val repositoryModule = module {
    single { ItemRepository(get(), get(), get()) }
    single { ClientRepository(get()) }
    single { PricingRepository(get()) }
    single { AuthRepository(get(), get()) }
    single { AppraisalRepository(get()) }
    single { AppraiserProfileStore(get()) }
    single { AppraiserProfileStore(get()) }
    single { AppSettingsStore(get()) }
    single { ThemeController(get()) }
    single { BusinessProfileRepository(get(), get(), get(), get()) }
    single { PhotoRepository(get(), get()) }
    single { SyncRepository(get(), get(), get(), get(), get(), get()) }
    single { DeleteAppraisalUseCase(get(), get(), get()) }
    single { SessionRepository(get(), get(), get(), get()) }
    single { get<AppDatabase>().syncOutboxDao() }

}