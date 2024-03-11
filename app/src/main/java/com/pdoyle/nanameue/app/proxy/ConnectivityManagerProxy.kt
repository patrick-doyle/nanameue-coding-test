package com.pdoyle.nanameue.app.proxy

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Wrapper around android ConnectivityManager to improve testing
 */
class ConnectivityManagerProxy private constructor(
    private val connectivityManager: ConnectivityManager){

    companion object {
        fun create(context: Context): ConnectivityManagerProxy {
            return ConnectivityManagerProxy(
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            )
        }
    }

    fun hasConnection(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}