package com.example.candra.moviecatalogue.view.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {

    //alarmType
    const val TYPE_DAILYREMINDER_NOTIF = "typeDailyReminderNotif"
    const val TYPE_RELEASETODAY_DATAGETTER = "typeReleaseTodayDataGetter"
    const val TYPE_RELEASETODAY_NOTIF = "typeReleaseTodayNotif"

    const val ID_DAILYREMINDER_PENDINGINTENT = 101
    const val ID_RELEASETODAYDATAGETTER_PENDINGINTENT = 102
    private const val ID_RELEASETODAYNOTIF_PENDINGINTENT = 103

    private const val REGISTERED_DAY_RELEASE_NOTIF_SCHEDULE = "registeredDayScheduleReleaseNotif"
    const val REGISTERED_DAY_DAILY_REMINDER_NOTIF_SCHEDULE =
        "registeredDayScheduleDailyReminderNotif"
    const val REGISTERED_DAY_RELEASE_DATA_GETTER_SCHEDULE =
        "registeredDayReleaseDataGetterSchedule"

    const val ALARM_MANAGER_SHARED_PREF = "AlarmManager"

    const val IS_DAILY_REMINDER_DISABLED_BY_USER = "isDailyReminderDisabledByUser"
    const val IS_RELEASE_TODAY_REMINDER_DISABLED_BY_USER = "isReleaseTodayReminderDisabledByUser"

    const val PREF_IS_SET_AFTER_PRESET_HOUR_RELEASE_TODAY =
        "prefIsSetAfterPresetHourReleaseTodayReminder"
    const val PREF_IS_SET_AFTER_PRESET_HOUR_DAILY_REMINDER = "prefIsSetAfterPresetHourDailyReminder"

    const val PREF_RELEASETODAY_DATA = "prefReleaseTodayData"

    private const val hourDailyReminder = 7
    const val hourReleaseTodayNotif = 8
    private const val hourReleaseDataGetter = 7


    /**
     * This function will schedule 3 types of 'alarm'
     * 1. Daily Reminder Notification = used setInexactRepeating, will run at 7 A.M. @see hourDailyReminder variable above
     *    if this alarm is set (on settingActivity) after preset hour (i.e 7 A.M), notif will be shown on the next day
     *
     * 2. ReleaseToday DataGetter = used setInexactRepeating, will run at 7 A.M. same as Daily Reminder
     *    This alarm will get api data for Release Today Notification, called early (at 7 A.M) to show timely Release Today Notif (at 8 A.M.)
     *
     * 3. Release Today Notification = used setExactAndAllowWhenIdle, will run at 8 A.M. with ideal condition
     *    (i.e: phone is connected to internet, api data has been loaded)
     *    if phone disconnected from internet, ReleaseToday DataGetter will wait phone until connected, and deliver the data,
     *    if data delivered before preset hour (i.e 8 A.M.) then notif will sheduled at 8 A.M.
     *    if data delivered after preset hour (i.e 8 A.M.) then notif will sheduled at this time
     */
    @SuppressLint("ApplySharedPref")
    fun schedule(
        alarmType: String,
        context: Context,
        hour: Int? = null,
        minute: Int = 0,
        second: Int = 0,
        presetHourNotif: Int? = null,
        presetMinute: Int? = null
    ) {
        val sharedPreferences =
            context.getSharedPreferences(ALARM_MANAGER_SHARED_PREF, Context.MODE_PRIVATE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var clazz: Class<*> = AlarmScheduler::class.java //MUST BE CHANGED
        var pendingIntentId = 0 //MUST BE CHANGED
        var sharedPrefsKeyRegisteredDay = "" //MUST BE CHANGED
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)

        when (alarmType) {
            TYPE_DAILYREMINDER_NOTIF -> {
                clazz = DailyReminderNotifReceiver::class.java
                pendingIntentId = ID_DAILYREMINDER_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_DAILY_REMINDER_NOTIF_SCHEDULE
                calendar.set(Calendar.HOUR_OF_DAY, hour ?: hourDailyReminder)

                val isSetAfterPresetHour =
                    isNowAfter(presetHourNotif ?: hourDailyReminder, presetMinute ?: 1)
                sharedPreferences.edit()
                    .putBoolean(PREF_IS_SET_AFTER_PRESET_HOUR_DAILY_REMINDER, isSetAfterPresetHour)
                    ?.apply()
            }
            TYPE_RELEASETODAY_DATAGETTER -> {
                clazz = ReleasedTodayDataGetter::class.java
                pendingIntentId = ID_RELEASETODAYDATAGETTER_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_RELEASE_DATA_GETTER_SCHEDULE

                //if alarm set after datagetterpresethour (7AM) but before preset hour of ReleaseTodayNotif + 1min (8.01 A.M.)
                if (isNowAfter(hourReleaseDataGetter) && !isNowAfter(hourReleaseTodayNotif, 1)) {
                    //call data getter now (and schedule release today notif)
                    context.sendBroadcast(Intent(context, ReleasedTodayDataGetter::class.java))
                } //otherwise, just ignore and schedule datagetter as usual (at 7AM)

                calendar.set(Calendar.HOUR_OF_DAY, hour ?: hourReleaseDataGetter)

                val isSetAfterPresetHour =
                    isNowAfter(presetHourNotif ?: hourReleaseTodayNotif, presetMinute ?: 1)
                sharedPreferences.edit()
                    .putBoolean(PREF_IS_SET_AFTER_PRESET_HOUR_RELEASE_TODAY, isSetAfterPresetHour)
                    ?.apply()
            }
            TYPE_RELEASETODAY_NOTIF -> {
                clazz = ReleaseTodayNotifReceiver::class.java
                pendingIntentId = ID_RELEASETODAYNOTIF_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_RELEASE_NOTIF_SCHEDULE
                calendar.set(Calendar.HOUR_OF_DAY, hour ?: hourReleaseTodayNotif)

            }
        }

        val intent = Intent(context, clazz)

        val alarmHasBeenSet = isAlarmHasBeenSet(
            context,
            pendingIntentId,
            clazz,
            sharedPrefsKeyRegisteredDay
        )

        val pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, 0)

        if (!alarmHasBeenSet) {

            when (alarmType) {
                TYPE_DAILYREMINDER_NOTIF -> {
                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                    Log.d("dailyReminderNotif", "SCHEDULED")
                }
                TYPE_RELEASETODAY_DATAGETTER -> {
                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                    Log.d("releaseTodayDataGetter", "SCHEDULED")
                }
                TYPE_RELEASETODAY_NOTIF -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d("releaseTodayNotif", "SCHEDULED")
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d("releaseTodayNotif", "SCHEDULED")
                    }
                }
            }

            sharedPreferences.edit()
                .putString(sharedPrefsKeyRegisteredDay, getTodayDate())
                .commit()

        } else Log.e(
            "error$alarmType",
            "Alarm($alarmType) already been scheduled! Make sure to unschedule first"
        )

    }

    fun cancel(
        alarmType: String,
        context: Context
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent() //MUST BE CHANGED
        var pendingIntentId = 0 //MUST BE CHANGED
        var sharedPrefsKeyRegisteredDay = "" //MUST BE CHANGED

        when (alarmType) {
            TYPE_DAILYREMINDER_NOTIF -> {
                intent = Intent(context, DailyReminderNotifReceiver::class.java)
                pendingIntentId = ID_DAILYREMINDER_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_DAILY_REMINDER_NOTIF_SCHEDULE
            }
            TYPE_RELEASETODAY_DATAGETTER -> {
                intent = Intent(context, ReleasedTodayDataGetter::class.java)
                pendingIntentId = ID_RELEASETODAYDATAGETTER_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_RELEASE_DATA_GETTER_SCHEDULE
            }
            TYPE_RELEASETODAY_NOTIF -> {
                intent = Intent(context, ReleaseTodayNotifReceiver::class.java)
                pendingIntentId = ID_RELEASETODAYNOTIF_PENDINGINTENT
                sharedPrefsKeyRegisteredDay = REGISTERED_DAY_RELEASE_NOTIF_SCHEDULE
            }
        }

        val pendingIntent =
            PendingIntent.getBroadcast(context, pendingIntentId, intent, 0)

        alarmManager.cancel(pendingIntent)

        if (alarmType == TYPE_RELEASETODAY_DATAGETTER) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WorkerHere.TAG)
            cancel(TYPE_RELEASETODAY_NOTIF, context)
        }

        resetRegisteredDay(context, sharedPrefsKeyRegisteredDay)
    }

    fun <T> isAlarmHasBeenSet(
        context: Context,
        pendingIntentId: Int,
        clazz: Class<T>,
        sharedPrefsKeyRegisteredDay: String
    ): Boolean {
        //check if has been set for today
        val sharedPreferences =
            context.getSharedPreferences(ALARM_MANAGER_SHARED_PREF, Context.MODE_PRIVATE)
        val intent = Intent(context, clazz)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pendingIntentId,
            intent,
            PendingIntent.FLAG_NO_CREATE
        )

        val registeredDay = sharedPreferences.getString(sharedPrefsKeyRegisteredDay, "")

        return getTodayDate() == registeredDay && pendingIntent != null

    }

    @SuppressLint("ApplySharedPref")
    private fun resetRegisteredDay(context: Context, sharedPrefsKeyRegisteredDay: String) {
        val sharedPreferences =
            context.getSharedPreferences(ALARM_MANAGER_SHARED_PREF, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(sharedPrefsKeyRegisteredDay, "")
            .commit()

        val newValue = sharedPreferences.getString(sharedPrefsKeyRegisteredDay, "a")
        Log.d("resetRegisteredDay", "$sharedPrefsKeyRegisteredDay : $newValue")
    }

    private fun getTodayDate(): String {
        val time = Date()
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
    }

    fun isNowAfter(presetHour: Int, presetMinute: Int = 0): Boolean {
        val now = Date()

        val calPreset = Calendar.getInstance()
        calPreset.set(Calendar.HOUR_OF_DAY, presetHour)
        calPreset.set(Calendar.MINUTE, presetMinute)
        calPreset.set(Calendar.SECOND, 0)

        val timePreset = calPreset.time

        return now.after(timePreset)
    }


}