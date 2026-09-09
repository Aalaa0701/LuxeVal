package com.example.luxeval.presentation.util

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

/** A message that's safe and useful to show directly in the UI. Never wraps a
 *  raw exception message — those go to logs, not to appraisers. */
data class UserError(val title: String, val message: String, val retryable: Boolean)

/**
 * Suspend because it resolves localized strings via Compose Resources'
 * getString(), which is safe to call from any coroutine — including
 * viewModelScope.launch { } blocks, which is where every call site
 * (see repositories/ViewModels) already lives.
 */
suspend fun Throwable.toUserError(context: ErrorContext = ErrorContext.GENERIC): UserError = when {
    // Network reachability
    this is HttpRequestTimeoutException || this is IOException ->
        UserError(
            title = getString(Res.string.error_connection_title),
            message = getString(Res.string.error_connection_body),
            retryable = true
        )

    // Supabase/Postgrest errors carry a `message`/`code` you can branch on
    this is RestException && this.error == "PGRST301" ->
        UserError(
            title = getString(Res.string.error_session_expired_title),
            message = getString(Res.string.error_session_expired_body),
            retryable = false
        )

    this is RestException ->
        UserError(getString(context.titleRes()), getString(context.messageRes()), retryable = true)

    this is HttpRequestException ->
        UserError(
            title = getString(Res.string.error_connection_title),
            message = getString(Res.string.error_connection_body),
            retryable = true
        )

    else -> UserError(getString(context.titleRes()), getString(context.messageRes()), retryable = true)
}

enum class ErrorContext {
    GENERIC, SAVE, UPLOAD, EXPORT, AUTH, PRICE_FETCH
}

private fun ErrorContext.titleRes() = when (this) {
    ErrorContext.GENERIC -> Res.string.error_generic_title
    ErrorContext.SAVE -> Res.string.error_save_title
    ErrorContext.UPLOAD -> Res.string.error_upload_title
    ErrorContext.EXPORT -> Res.string.error_export_title
    ErrorContext.AUTH -> Res.string.error_auth_title
    ErrorContext.PRICE_FETCH -> Res.string.error_price_fetch_title
}

private fun ErrorContext.messageRes() = when (this) {
    ErrorContext.GENERIC -> Res.string.error_generic_body
    ErrorContext.SAVE -> Res.string.error_save_body
    ErrorContext.UPLOAD -> Res.string.error_upload_body
    ErrorContext.EXPORT -> Res.string.error_export_body
    ErrorContext.AUTH -> Res.string.error_auth_body
    ErrorContext.PRICE_FETCH -> Res.string.error_price_fetch_body
}