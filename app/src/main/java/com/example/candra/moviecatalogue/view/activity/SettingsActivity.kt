package com.example.candra.moviecatalogue.view.activity


import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.view.notification.AlarmScheduler
import com.example.candra.moviecatalogue.view.notification.DailyReminderNotifReceiver
import com.example.candra.moviecatalogue.view.notification.ReleasedTodayDataGetter

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val sharedPrefs = context?.getSharedPreferences(
                AlarmScheduler.ALARM_MANAGER_SHARED_PREF,
                Context.MODE_PRIVATE
            )

            val dailyReminderSwitchKey = context?.resources!!.getString(R.string.daily_reminder_switch_key)
            val dailySwitch = preferenceManager.findPreference<SwitchPreferenceCompat>(dailyReminderSwitchKey)

            dailySwitch?.isChecked = AlarmScheduler.isAlarmHasBeenSet(
                context!!,
                AlarmScheduler.ID_DAILYREMINDER_PENDINGINTENT,
                DailyReminderNotifReceiver::class.java,
                AlarmScheduler.REGISTERED_DAY_DAILY_REMINDER_NOTIF_SCHEDULE
            )

            dailySwitch?.setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as Boolean
                if (value == false) {
                    context?.applicationContext?.let {
                        AlarmScheduler.cancel(AlarmScheduler.TYPE_DAILYREMINDER_NOTIF, it)
                        sharedPrefs?.edit()
                            ?.putBoolean(AlarmScheduler.IS_DAILY_REMINDER_DISABLED_BY_USER, true)
                            ?.apply()

                    }
                } else {
                    context?.applicationContext?.let {
                        AlarmScheduler.schedule(AlarmScheduler.TYPE_DAILYREMINDER_NOTIF, it)

                        sharedPrefs?.edit()
                            ?.putBoolean(AlarmScheduler.IS_DAILY_REMINDER_DISABLED_BY_USER, false)
                            ?.apply()
                    }
                }
                return@setOnPreferenceChangeListener true
            }

        val releaseTodaySwitchKey = context?.resources!!.getString(R.string.release_today_reminder_switch_key)
            val releaseTodaySwitch =
                preferenceManager.findPreference<SwitchPreferenceCompat>(releaseTodaySwitchKey)

            releaseTodaySwitch?.isChecked = AlarmScheduler.isAlarmHasBeenSet(
                context!!,
                AlarmScheduler.ID_RELEASETODAYDATAGETTER_PENDINGINTENT,
                ReleasedTodayDataGetter::class.java,
                AlarmScheduler.REGISTERED_DAY_RELEASE_DATA_GETTER_SCHEDULE
            )

            releaseTodaySwitch?.setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as Boolean
                if (value == false) {
                    context?.applicationContext?.let {
                        AlarmScheduler.cancel(AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER, it)
                        sharedPrefs?.edit()?.putBoolean(
                            AlarmScheduler.IS_RELEASE_TODAY_REMINDER_DISABLED_BY_USER,
                            true
                        )?.apply()
                    }
                } else {
                    context?.applicationContext?.let {
                        AlarmScheduler.schedule(AlarmScheduler.TYPE_RELEASETODAY_DATAGETTER, it)

                        sharedPrefs?.edit()?.putBoolean(
                            AlarmScheduler.IS_RELEASE_TODAY_REMINDER_DISABLED_BY_USER,
                            false
                        )?.apply()
                    }
                }
                return@setOnPreferenceChangeListener true
            }
        }


    }
}