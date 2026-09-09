package com.example.luxeval.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.repository.AuthError
import com.example.luxeval.data.repository.AuthException
import com.example.luxeval.data.repository.AuthRepository
import com.example.luxeval.data.repository.SignUpResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.auth_error_enter_credentials
import luxeval.shared.generated.resources.auth_error_fill_all_fields
import luxeval.shared.generated.resources.auth_error_generic
import luxeval.shared.generated.resources.auth_error_google_failed
import luxeval.shared.generated.resources.auth_error_incorrect_credentials
import luxeval.shared.generated.resources.auth_error_must_agree_terms
import luxeval.shared.generated.resources.auth_error_password_too_short
import luxeval.shared.generated.resources.auth_error_passwords_dont_match
import luxeval.shared.generated.resources.auth_error_resend_failed
import luxeval.shared.generated.resources.auth_error_reset_failed
import luxeval.shared.generated.resources.auth_error_signin_failed
import luxeval.shared.generated.resources.auth_error_signup_failed
import org.jetbrains.compose.resources.getString

enum class AuthTab { LOGIN, SIGN_UP }

data class AuthFormState(
    val tab: AuthTab = AuthTab.LOGIN,
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rememberMe: Boolean = false,
    val agreedToTerms: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val fieldError: String? = null
) {
    val canSubmitLogin: Boolean get() = email.isNotBlank() && password.isNotBlank()
    val canSubmitSignUp: Boolean get() =
        fullName.isNotBlank() && email.isNotBlank() &&
                password.length >= 8 && password == confirmPassword && agreedToTerms
}

/** One-off UI events — each is consumed exactly once by the dialog host,
 *  never re-shown just because the screen recomposed. */
sealed class AuthUiEvent {
    data class SignUpNeedsConfirmation(val email: String) : AuthUiEvent()
    data object SignUpAutoLoggedIn : AuthUiEvent()
    data class EmailNotConfirmed(val email: String) : AuthUiEvent()
    data class Error(val message: String) : AuthUiEvent()
    data object PasswordResetSent : AuthUiEvent()
    data object ResendConfirmationSent : AuthUiEvent()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val settingsStore: AppSettingsStore
) : ViewModel() {

    private val _formState = MutableStateFlow(
        AuthFormState(email = settingsStore.getRememberedEmail().orEmpty())
    )
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthUiEvent> = _events.asSharedFlow()

    fun onTabChanged(tab: AuthTab) {
        _formState.value = _formState.value.copy(tab = tab, fieldError = null)
    }

    fun onFullNameChanged(v: String) = update { it.copy(fullName = v, fieldError = null) }
    fun onEmailChanged(v: String) = update { it.copy(email = v, fieldError = null) }
    fun onPasswordChanged(v: String) = update { it.copy(password = v, fieldError = null) }
    fun onConfirmPasswordChanged(v: String) = update { it.copy(confirmPassword = v, fieldError = null) }
    fun onRememberMeChanged(v: Boolean) = update { it.copy(rememberMe = v) }
    fun onAgreedToTermsChanged(v: Boolean) = update { it.copy(agreedToTerms = v) }
    fun togglePasswordVisibility() = update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun toggleConfirmPasswordVisibility() = update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

    private inline fun update(transform: (AuthFormState) -> AuthFormState) {
        _formState.value = transform(_formState.value)
    }

    fun submit() {
        val state = _formState.value
        when (state.tab) {
            AuthTab.LOGIN -> signIn()
            AuthTab.SIGN_UP -> signUp()
        }
    }

    private fun signIn() {
        val state = _formState.value
        if (!state.canSubmitLogin) {
            viewModelScope.launch {
                val msg = getString(Res.string.auth_error_enter_credentials) // add this key
                update { it.copy(fieldError = msg) }
            }
            return
        }
        viewModelScope.launch {
            update { it.copy(isSubmitting = true, fieldError = null) }
            try {
                authRepository.signIn(state.email.trim(), state.password)
                if (state.rememberMe) settingsStore.setRememberedEmail(state.email.trim())
                else settingsStore.setRememberedEmail(null)
                update { it.copy(isSubmitting = false) }
            } catch (e: AuthException) {
                update { it.copy(isSubmitting = false) }
                when (val err = e.error) {
                    is AuthError.EmailNotConfirmed -> _events.emit(AuthUiEvent.EmailNotConfirmed(err.email))
                    is AuthError.InvalidCredentials ->
                        _events.emit(AuthUiEvent.Error(getString(Res.string.auth_error_incorrect_credentials)))
                    is AuthError.Other ->
                        _events.emit(AuthUiEvent.Error(err.message.ifBlank { getString(Res.string.auth_error_generic) }))
                }
            } catch (e: Exception) {
                update { it.copy(isSubmitting = false) }
                _events.emit(AuthUiEvent.Error(getString(Res.string.auth_error_signin_failed)))
            }
        }
    }

    private fun signUp() {
        val state = _formState.value
        if (!state.canSubmitSignUp) {
            viewModelScope.launch {
                val msg = when {
                    state.password.length < 8 -> getString(Res.string.auth_error_password_too_short)
                    state.password != state.confirmPassword -> getString(Res.string.auth_error_passwords_dont_match)
                    !state.agreedToTerms -> getString(Res.string.auth_error_must_agree_terms)
                    else -> getString(Res.string.auth_error_fill_all_fields)
                }
                update { it.copy(fieldError = msg) }
            }
            return
        }
        viewModelScope.launch {
            update { it.copy(isSubmitting = true, fieldError = null) }
            try {
                val result = authRepository.signUp(state.email.trim(), state.password, state.fullName.trim())
                update { it.copy(isSubmitting = false) }
                when (result) {
                    is SignUpResult.LoggedInImmediately -> _events.emit(AuthUiEvent.SignUpAutoLoggedIn)
                    is SignUpResult.NeedsEmailConfirmation ->
                        _events.emit(AuthUiEvent.SignUpNeedsConfirmation(result.email))
                }
            } catch (e: Exception) {
                update { it.copy(isSubmitting = false) }
                _events.emit(
                    AuthUiEvent.Error(e.message ?: getString(Res.string.auth_error_signup_failed))
                )
            }
        }
    }

    fun resendConfirmation(email: String) {
        viewModelScope.launch {
            runCatching { authRepository.resendConfirmationEmail(email) }
                .onSuccess { _events.emit(AuthUiEvent.ResendConfirmationSent) }
                .onFailure { _events.emit(AuthUiEvent.Error(it.message ?: getString(Res.string.auth_error_resend_failed))) }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            runCatching { authRepository.sendPasswordReset(email) }
                .onSuccess { _events.emit(AuthUiEvent.PasswordResetSent) }
                .onFailure { _events.emit(AuthUiEvent.Error(it.message ?: getString(Res.string.auth_error_reset_failed))) }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            runCatching { authRepository.signInWithGoogle() }
                .onFailure { _events.emit(AuthUiEvent.Error(it.message ?: getString(Res.string.auth_error_google_failed))) }
        }
    }
}