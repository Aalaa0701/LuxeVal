package com.example.luxeval.presentation.util

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException

/** A message that's safe and useful to show directly in the UI. Never wraps a
 *  raw exception message — those go to logs, not to appraisers. */
data class UserError(val title: String, val message: String, val retryable: Boolean)

fun Throwable.toUserError(context: ErrorContext = ErrorContext.GENERIC): UserError = when {
    // Network reachability
    this is HttpRequestTimeoutException || this is IOException ->
        UserError(
            title = "Connection Issue",
            message = "We couldn't reach the server. Check your internet connection and try again.",
            retryable = true
        )

    // Supabase/Postgrest errors carry a `message`/`code` you can branch on
    this is RestException && this.error == "PGRST301" ->
        UserError("Session Expired", "Please sign in again to continue.", retryable = false)

    this is RestException ->
        UserError(context.genericTitle, context.genericMessage, retryable = true)

    this is HttpRequestException ->
        UserError(
            title = "Connection Issue",
            message = "We couldn't reach the server. Check your internet connection and try again.",
            retryable = true
        )

    else -> UserError(context.genericTitle, context.genericMessage, retryable = true)
}

enum class ErrorContext(val genericTitle: String, val genericMessage: String) {
    GENERIC("Something Went Wrong", "Please try again. If this keeps happening, contact support."),
    SAVE("Couldn't Save", "Your changes couldn't be saved. Please try again."),
    UPLOAD("Upload Failed", "We couldn't upload your photo. Please check your connection and try again."),
    EXPORT("Export Failed", "We couldn't generate the PDF. Please try again."),
    AUTH("Sign-In Problem", "We couldn't sign you in. Please check your email and password and try again."),
    PRICE_FETCH("Price Unavailable", "Couldn't fetch the current market price. You can enter a value manually.")
}