package com.example.candra.moviecatalogue.view.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.candra.moviecatalogue.R
import kotlinx.android.synthetic.main.activity_notification_test.*

class NotificationTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_test)

        hourNumberPicker.maxValue = 23
        hourNumberPicker.minValue = 0
        minuteNumberPicker.maxValue = 59
        minuteNumberPicker.minValue = 0
        hourPresetNumberPicker.maxValue = 23
        hourPresetNumberPicker.minValue = 0
        minutePresetNumberPicker.maxValue = 59
        minutePresetNumberPicker.minValue = 0


        registerDaily.setOnClickListener {
            AlarmScheduler.schedule(
                AlarmScheduler.TYPE_DAILYREMINDER_NOTIF,
                applicationContext,
                hourNumberPicker.value,
                minuteNumberPicker.value,
                presetHourNotif = hourPresetNumberPicker.value,
                presetMinute = minutePresetNumberPicker.value
            )
        }
        unregisterDaily.setOnClickListener {
            AlarmScheduler.cancel(AlarmScheduler.TYPE_DAILYREMINDER_NOTIF, applicationContext)
        }

        registerRelease.setOnClickListener {
            AlarmScheduler.schedule(
                AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER,
                applicationContext,
                hourNumberPicker.value,
                minuteNumberPicker.value,
                presetHourNotif = hourPresetNumberPicker.value,
                presetMinute = minutePresetNumberPicker.value
            )
        }

        unregsiterRelease.setOnClickListener {
            AlarmScheduler.cancel(AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER, applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmScheduler.cancel(AlarmScheduler.TYPE_DAILYREMINDER_NOTIF, applicationContext)
        AlarmScheduler.cancel(AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER, applicationContext)
    }
}
