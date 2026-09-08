package com.example.luxeval.config

object LuxValConfig {
    val METAL_PRICE_API_KEY: String get() = GeneratedSecrets.METAL_PRICE_API_KEY
    val SUPABASE_URL: String get() = GeneratedSecrets.SUPABASE_URL
    val SUPABASE_API_KEY: String get() = GeneratedSecrets.SUPABASE_API_KEY
    // config/LuxValConfig.kt — add alongside the secret-backed vals
    const val PRIVACY_POLICY_URL = "https://alaa800-crypto.github.io/LuxVal-legal/"
    const val TERMS_OF_SERVICE_URL = "https://alaa800-crypto.github.io/LuxVal-legal/"

}