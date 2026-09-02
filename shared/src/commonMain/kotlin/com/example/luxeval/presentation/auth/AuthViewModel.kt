package com.example.luxeval.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val authState = authRepository.authState

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    fun onEmailChanged(v: String) { _formState.value = _formState.value.copy(email = v, error = null) }
    fun onPasswordChanged(v: String) { _formState.value = _formState.value.copy(password = v, error = null) }

    fun signIn(onSuccess: () -> Unit) = submit(onSuccess) { authRepository.signIn(_formState.value.email, _formState.value.password) }
    fun signUp(onSuccess: () -> Unit) = submit(onSuccess) { authRepository.signUp(_formState.value.email, _formState.value.password) }

    private fun submit(onSuccess: () -> Unit, action: suspend () -> Unit) {
        val state = _formState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _formState.value = state.copy(error = "Enter a valid email and a password with at least 6 characters")
            return
        }
        viewModelScope.launch {
            _formState.value = state.copy(isSubmitting = true, error = null)
            try {
                action()
                _formState.value = _formState.value.copy(isSubmitting = false)
                onSuccess()
            } catch (e: Exception) {
                _formState.value = _formState.value.copy(isSubmitting = false, error = e.message ?: "Something went wrong")
            }
        }
    }
}

data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
)