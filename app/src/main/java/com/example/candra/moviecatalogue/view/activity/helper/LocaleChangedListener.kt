package com.example.candra.moviecatalogue.view.activity.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleChangedListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        listener.loadData()
    }

    companion object {
        interface ViewListener {
            fun loadData()
        }

        //must init in Activity which listen this Broadcast
        lateinit var listener: ViewListener
    }
}