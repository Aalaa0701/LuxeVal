package com.example.luxeval.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.OtpType
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

/** Outcome of a sign-up call — Supabase either logs the user in immediately
 *  (email confirmations disabled in your project settings) or requires them
 *  to click a link first (confirmations enabled — the default). */
sealed class SignUpResult {
    data object LoggedInImmediately : SignUpResult()
    data class NeedsEmailConfirmation(val email: String) : SignUpResult()
}

/** Classifies auth errors into buckets the UI can act on distinctly, instead
 *  of showing every failure as the same generic red text. */
sealed class AuthError(val message: String) {
    data class EmailNotConfirmed(val email: String) : AuthError("Please confirm your email before signing in.")
    data class InvalidCredentials(val raw: String) : AuthError("Incorrect email or password.")
    data class Other(val raw: String) : AuthError(raw)
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

    suspend fun signUp(email: String, password: String, fullName: String): SignUpResult {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = kotlinx.serialization.json.buildJsonObject {
                put("full_name", kotlinx.serialization.json.JsonPrimitive(fullName))
            }
        }
        return if (client.auth.currentSessionOrNull() != null) {
            SignUpResult.LoggedInImmediately
        } else {
            SignUpResult.NeedsEmailConfirmation(email)
        }
    }

    /** Throws a classified AuthError instead of the raw Supabase exception so
     *  the UI layer never has to string-match exception messages itself. */
    suspend fun signIn(email: String, password: String) {
        try {
            client.auth.signInWith(Email) { this.email = email; this.password = password }
        } catch (e: Exception) {
            throw classify(e, email)
        }
    }

    suspend fun resendConfirmationEmail(email: String) {
        client.auth.resendEmail(type = OtpType.Email.SIGNUP, email = email)
    }

    suspend fun sendPasswordReset(email: String) {
        client.auth.resetPasswordForEmail(email)
    }

    suspend fun signInWithGoogle() {
        // Requires one-time setup: a redirect URL registered in your Supabase
        // project's Auth settings, plus a matching custom URL scheme declared
        // in AndroidManifest.xml (intent-filter) and Info.plist (CFBundleURLTypes)
        // so the OAuth browser flow can hand control back to the app. Flag this
        // to me separately — it's platform config, not Kotlin code, and easy to
        // get subtly wrong on the first pass.
        client.auth.signInWith(Google)
    }

    suspend fun signOut() = client.auth.signOut()

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    private fun classify(e: Exception, email: String): AuthException {
        val msg = e.message.orEmpty()
        return when {
            msg.contains("not confirmed", ignoreCase = true) ||
                    msg.contains("email_not_confirmed", ignoreCase = true) ->
                AuthException(AuthError.EmailNotConfirmed(email))
            msg.contains("invalid", ignoreCase = true) ||
                    msg.contains("credentials", ignoreCase = true) ->
                AuthException(AuthError.InvalidCredentials(msg))
            else -> AuthException(AuthError.Other(msg.ifBlank { "Something went wrong. Please try again." }))
        }
    }
}

/** Wraps a classified AuthError so it can be thrown/caught while still
 *  carrying structured info the ViewModel can pattern-match on. */
class AuthException(val error: AuthError) : Exception(error.message)