package com.example.candra.moviecatalogue.view.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RegisterAlarmWhenReboot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("onReceiveMovieREboot", "REACHED")
        val sharedPrefs = context.getSharedPreferences(
            AlarmScheduler.ALARM_MANAGER_SHARED_PREF,
            Context.MODE_PRIVATE
        )

        val isDailyReminderDisabledByUser =
            sharedPrefs.getBoolean(AlarmScheduler.IS_DAILY_REMINDER_DISABLED_BY_USER, true)
        val isReleaseTodayReminderDisabledByUser = sharedPrefs.getBoolean(
            AlarmScheduler.IS_RELEASE_TODAY_REMINDER_DISABLED_BY_USER,
            true
        )
        Log.e(
            "onREceiveMovieReboot",
            "isDAilyREminderDisabledByUser : $isDailyReminderDisabledByUser"
        )

        if (!isDailyReminderDisabledByUser) {
            AlarmScheduler.schedule(AlarmScheduler.TYPE_DAILYREMINDER_NOTIF, context)
            Log.e("onReceiveMovieReboot", "dailyReminderResetReboot")
        }
        if (!isReleaseTodayReminderDisabledByUser) {
            AlarmScheduler.schedule(AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER, context)
            Log.e("onReceiveMovieReboot", "releaseDataGetterScheduleResetReboot")
        }

    }

}