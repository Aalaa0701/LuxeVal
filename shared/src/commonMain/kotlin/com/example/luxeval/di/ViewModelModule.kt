package com.example.luxeval.di

import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailViewModel
import com.example.luxeval.presentation.appraisalsList.AppraisalsListViewModel
import com.example.luxeval.presentation.dashboard.DashboardViewModel
import com.example.luxeval.presentation.itemEditor.bracelet.BraceletEditorViewModel
import com.example.luxeval.presentation.itemEditor.brooch.BroochEditorViewModel
import com.example.luxeval.presentation.itemEditor.earrings.EarringsEditorViewModel
import com.example.luxeval.presentation.itemEditor.loose_gemstone.LooseGemstoneEditorViewModel
import com.example.luxeval.presentation.itemEditor.necklace.NecklaceEditorViewModel
import com.example.luxeval.presentation.itemEditor.pearl.PearlEditorViewModel
import com.example.luxeval.presentation.itemEditor.pendant.PendantEditorViewModel
import com.example.luxeval.presentation.itemEditor.ring.RingEditorViewModel
import com.example.luxeval.presentation.itemEditor.watch.WatchEditorViewModel
import com.example.luxeval.presentation.report.ReportPreviewViewModel
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
    viewModel { params ->
        RingEditorViewModel(
            appraisalId = params.get(),
            itemId = params.get(),
            itemRepository = get(),
            photoStorage = get()
        )
    }
    viewModel { params ->
        NecklaceEditorViewModel(
            appraisalId = params.get(),
            itemId = params.get(),
            itemRepository = get(),
            photoStorage = get()

        )
    }

    viewModel { params ->
        WatchEditorViewModel(
            appraisalId = params.get(),
            itemId = params.get(),
            itemRepository = get(),
            photoStorage = get()

        )
    }
    viewModel { params ->
        PearlEditorViewModel(
            appraisalId = params.get(),
            itemId = params.get(),
            itemRepository = get(),
            photoStorage = get()

        )
    }


    viewModel { params -> PendantEditorViewModel(params.get(), params.get(), get(), get()) }
    viewModel { params -> BraceletEditorViewModel(params.get(), params.get(), get(), get()) }
    viewModel { params -> EarringsEditorViewModel(params.get(), params.get(), get(), get()) }
    viewModel { params -> BroochEditorViewModel(params.get(), params.get(), get(), get()) }
    viewModel { params -> LooseGemstoneEditorViewModel(params.get(), params.get(), get(), get()) }

    viewModel { params ->
        ReportPreviewViewModel(
            appraisalId = params.get(),
            appraisalRepository = get(),
            clientRepository = get(),
            itemRepository = get(),
            profileStore = get(),
            settingsStore = get(),
            pdfExporter = get(),
            fileSharer = get()
        )
    }
    viewModel { AppraisalsListViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}