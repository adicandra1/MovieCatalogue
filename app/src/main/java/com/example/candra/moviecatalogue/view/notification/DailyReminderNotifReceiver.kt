package com.example.candra.moviecatalogue.view.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.view.activity.MainActivity

class DailyReminderNotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences(
            AlarmScheduler.ALARM_MANAGER_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        val isSetAfterPresetHour = sharedPreferences.getBoolean(
            AlarmScheduler.PREF_IS_SET_AFTER_PRESET_HOUR_DAILY_REMINDER,
            false
        )
        if (!isSetAfterPresetHour) {
            showNotification(context, NOTIF_ID_DAILY_REMINDER, R.string.app_name, R.string.miss_you)
        } else {
            Log.e(
                "NOTIFDAILYREMINDER",
                "Alarm Scheduled after preset hour, this day will be skipped and scheduled at the next day"
            )
        }
        //reset pref state so 'if block' above can be executed on next day schedule
        sharedPreferences.edit()
            .putBoolean(AlarmScheduler.PREF_IS_SET_AFTER_PRESET_HOUR_DAILY_REMINDER, false).apply()
    }


    @Suppress("SameParameterValue")
    private fun showNotification(
        context: Context?,
        notificationId: Int,
        titleResId: Int,
        textResId: Int
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

        val builder = context.let {
            NotificationCompat.Builder(it, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_local_movies_white_24dp)
                .setContentTitle(context.getString(titleResId))
                .setContentText(context.getString(textResId))
                .setAutoCancel(true)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
        }

        notificationManager.notify(notificationId, builder?.build())

    }

    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "channel_02"
        private const val NOTIF_ID_DAILY_REMINDER = 212
    }

}