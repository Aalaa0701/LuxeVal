package com.example.luxeval.di

import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailViewModel
import com.example.luxeval.presentation.appraisalsList.AppraisalsListViewModel
import com.example.luxeval.presentation.dashboard.DashboardViewModel
import com.example.luxeval.presentation.search.SearchViewModel
import com.example.luxeval.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { params ->
        AppraisalDetailViewModel(
            appraisalId = params.get(),
            appraisalRepository = get(),
            clientRepository = get(),
            itemRepository = get()
        )
    }
    viewModel {
        DashboardViewModel(
            appraisalRepository = get(),
            clientRepository = get(),
            itemRepository = get()
        )
    }
    viewModel { AppraisalsListViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}