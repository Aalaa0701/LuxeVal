package com.example.luxeval.data.remote

import com.example.luxeval.config.LuxValConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = LuxValConfig.SUPABASE_URL,
            supabaseKey = LuxValConfig.SUPABASE_API_KEY    // safe to ship client-side; RLS policies do the real gating
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}