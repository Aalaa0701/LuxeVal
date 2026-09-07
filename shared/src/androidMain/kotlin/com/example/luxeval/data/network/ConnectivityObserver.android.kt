// shared/src/androidMain/kotlin/com/example/luxeval/data/network/ConnectivityObserver.android.kt
package com.example.luxeval.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

actual class ConnectivityObserver(private val context: Context) {
    // ConnectivityObserver.android.kt

    actual fun observe(): Flow<ConnectionStatus> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Emit the current state immediately so collectors don't wait for the next transition.
        trySend(currentStatus(connectivityManager))

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectionStatus.AVAILABLE)
            }
            override fun onLost(network: Network) {
                trySend(currentStatus(connectivityManager)) // re-check — other networks may still be up
            }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(currentStatus(connectivityManager))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
    private fun currentStatus(cm: ConnectivityManager): ConnectionStatus {
        return try {
            val network = cm.activeNetwork ?: return ConnectionStatus.UNAVAILABLE
            val capabilities = cm.getNetworkCapabilities(network) ?: return ConnectionStatus.UNAVAILABLE
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (hasInternet) ConnectionStatus.AVAILABLE else ConnectionStatus.UNAVAILABLE
        } catch (e: SecurityException) {
            ConnectionStatus.UNAVAILABLE   // fail closed — outbox just won't auto-retry until app restart / pull-to-refresh
        }
    }
}