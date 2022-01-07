package com.example.candra.moviecatalogue.view.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.candra.data.model.DigitalShowResponse
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.view.activity.DetailActivity
import com.example.candra.utils.Constant
import com.google.gson.Gson


/**
 * This class is used to show Release Today notif, and using
 *
 * api data that has been queried in [WorkerHere] class
 */
class ReleaseTodayNotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val sharedPreferences = context.getSharedPreferences(
            AlarmScheduler.ALARM_MANAGER_SHARED_PREF,
            Context.MODE_PRIVATE
        )

        val response : String = sharedPreferences.getString(AlarmScheduler.PREF_RELEASETODAY_DATA, "")!!

        if (response.isNotEmpty()) {
            val gson = Gson()
            val limit = 3
            val data = gson.fromJson(response, DigitalShowResponse::class.java).digitalShows
            data?.size?.let {
                if (it >= limit) {
                    data.subList(limit, data.size).clear()
                }
            }

            data?.forEach { list ->
                list.id?.toInt()?.let { it1 ->
                    showNotification(context, it1, "Released Today", list.title)
                }
            } ?: showNotification(
                context,
                NOTIF_ID_IF_NO_DATA,
                "I'm Sorry",
                "No Released movie for Today"
            )
        }
    }

    @Suppress("SameParameterValue")
    private fun showNotification(
        context: Context?,
        notificationId: Int,
        titleString: String?,
        textString: String?
    ) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(Constant.ITEM_TYPE, Constant.TYPE_MOVIE)
        intent.putExtra(Constant.ITEM_ID, notificationId.toString())

        val builder = context.let {
            NotificationCompat.Builder(it, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_local_movies_white_24dp)
                .setContentTitle(titleString)
                .setContentText(textString)
                .setAutoCancel(true)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setGroup(GROUP_KEY)
        }

        notificationManager.notify(notificationId, builder?.build())

    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "channel_03"
        private const val GROUP_KEY = "releasedTodayGroup"

        private const val NOTIF_ID_IF_NO_DATA = 98
    }

}