package com.example.luxeval.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted

sealed class AuthState {
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class LoggedIn(val userId: String, val email: String?) : AuthState()
}

class AuthRepository(private val client: SupabaseClient) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val authState: StateFlow<AuthState> = client.auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Authenticated -> AuthState.LoggedIn(
                    userId = status.session.user?.id.orEmpty(),
                    email = status.session.user?.email
                )
                is SessionStatus.NotAuthenticated -> AuthState.LoggedOut
                else -> AuthState.Loading
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, AuthState.Loading)

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(Email) { this.email = email; this.password = password }
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) { this.email = email; this.password = password }
    }

    suspend fun signOut() = client.auth.signOut()

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id
}