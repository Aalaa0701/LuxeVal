package com.example.luxeval.data.network

import kotlinx.coroutines.flow.Flow

enum class ConnectionStatus { AVAILABLE, UNAVAILABLE }

/** Emits the current connectivity state, and again every time it changes.
 *  Platforms implement this with their native connectivity APIs. */
expect class ConnectivityObserver {
    fun observe(): Flow<ConnectionStatus>
}