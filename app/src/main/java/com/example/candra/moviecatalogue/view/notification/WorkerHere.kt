package com.example.candra.moviecatalogue.view.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.candra.data.api.ApiRepository
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.*


/**
 * This class is used to query Release Today data from api
 *
 * the api response will saved in sharedPreference. later to be used in [ReleaseTodayNotifReceiver] class
 *
 * This worker will also schedule [ReleaseTodayNotifReceiver] after finished loading api data
 */
class WorkerHere(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = coroutineScope {

        val sharedPreferences = applicationContext.getSharedPreferences(
            AlarmScheduler.ALARM_MANAGER_SHARED_PREF,
            Context.MODE_PRIVATE
        )

        val isAlarmSetAfterPresetNotifHour =
            sharedPreferences.getBoolean(
                AlarmScheduler.PREF_IS_SET_AFTER_PRESET_HOUR_RELEASE_TODAY,
                false
            )

        /* if alarm activated (on settingActivity) after preset hour (i.e: 8 A.M.):
         * then skip this day (schedule for the next day)
         *
         */
        if (!isAlarmSetAfterPresetNotifHour) {

            val today = Date()
            val todayDate: String =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today)

            //get datafrom api
            val response: String? = ApiRepository().loadReleasedTodayAsync(todayDate).await()
            //if result null send result retry
            response?.let { } ?: Result.retry()

            //insert to sharedPreference
            sharedPreferences.edit().putString(AlarmScheduler.PREF_RELEASETODAY_DATA, response).apply()

            //schedule notification
            val timeNow = Calendar.getInstance().time
            val isNowAfterPresetNotifHour =
                AlarmScheduler.isNowAfter(AlarmScheduler.hourReleaseTodayNotif)

            /* if this time is BEFORE preset release today notif hour (i.e : 8 A.M):
             * then set notif schedule on preset hour
             * else set notif schedule now (i.e: show notif right now)
             *
             */
            if (!isNowAfterPresetNotifHour) {
                AlarmScheduler.schedule(AlarmScheduler.TYPE_RELEASETODAY_NOTIF, applicationContext)
            } else {
                val cal = Calendar.getInstance()
                cal.time = timeNow
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val minute = cal.get(Calendar.MINUTE)
                AlarmScheduler.schedule(
                    AlarmScheduler.TYPE_RELEASETODAY_NOTIF,
                    applicationContext,
                    hour = hour,
                    minute = minute
                )
            }
        } else {
            Log.e(
                "WorkerHereScheduleNotif",
                "Alarm is set after preset hour, alarm will fire on the next day"
            )
        }
        //reset pref state so 'if block' above can be executed on next day schedule
        sharedPreferences.edit()
            .putBoolean(AlarmScheduler.PREF_IS_SET_AFTER_PRESET_HOUR_RELEASE_TODAY, false)
            .apply()

        Result.success()
    }

    companion object {
        const val TAG = "workerHereTag"
    }

}