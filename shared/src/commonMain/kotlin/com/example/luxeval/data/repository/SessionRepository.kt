package com.example.luxeval.data.repository


import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.database.AppDatabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SessionRepository(
    private val supabase: SupabaseClient,
    private val database: AppDatabase,
    private val profileStore: AppraiserProfileStore,
    private val authRepository: AuthRepository
) {
    suspend fun signOutAndClearLocalData() {
        supabase.auth.signOut()
        database.appDao().deleteAllItems()
        database.appDao().deleteAllClients()
        database.appDao().deleteAllAppraisals()
        profileStore.setName("")
        profileStore.setLicenseNumber("")
        profileStore.setBusinessPhone("")
        profileStore.setBusinessAddress("")
    }
    // SessionRepository.kt — add
    suspend fun deleteAccountAndClearLocalData() {
        authRepository.deleteAccount()       // remote user + cascaded rows + storage
        signOutAndClearLocalData()           // clears local Room tables, local prefs, signs out client-side session
    }
}