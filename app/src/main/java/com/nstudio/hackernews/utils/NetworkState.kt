package com.nstudio.hackernews.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkState {
    companion object{
        fun isOnline(context:Context): Boolean {
            val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
    }
}