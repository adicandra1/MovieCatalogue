package com.example.candra.moviecatalogue.view.activity.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.candra.moviecatalogue.utils.isConnectedToInternet
import com.example.candra.utils.Constant.GLOBALVAR_IS_CONNECTED


class InternetConnectionChecker(private val view: ViewOnConnectedListener) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("network change", "NETWORK CHANGED")

        if (isConnectedToInternet(context)) {
            GLOBALVAR_IS_CONNECTED = true
            Log.d("network connected", "NETWORK CHANGED: CONNECTED = $GLOBALVAR_IS_CONNECTED")
            view.loadData()
        } else {
            GLOBALVAR_IS_CONNECTED = false
            Log.d("network disconnected", "NETWORK CHANGED: CONNECTED = $GLOBALVAR_IS_CONNECTED")
        }
    }

}