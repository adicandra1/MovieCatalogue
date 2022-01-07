package com.example.candra.moviecatalogue.view.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * This class is used to get Release Today data from api
 *
 * used WorkManager internally, because of it's robust and easy to use API.
 *
 * used WorkManager to reduce boilerplate code.
 *
 * This receiver will call [WorkerHere] class
 */
class ReleasedTodayDataGetter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        //cancel existing work to avoid leaks
        WorkManager.getInstance(context).cancelAllWorkByTag(WorkerHere.TAG)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = OneTimeWorkRequestBuilder<WorkerHere>()
            .setConstraints(constraints)
            .addTag(WorkerHere.TAG)
            .build()

        WorkManager.getInstance(context).enqueue(work)
    }
}