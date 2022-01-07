package com.example.candra.moviecatalogue.view.activity.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.candra.data.saveToDatabase
import com.example.candra.moviecatalogue.R
import com.example.candra.data.api.ApiRepository
import com.example.candra.data.database.FavoriteRepository
import com.example.candra.data.database.FavoriteRoomDatabase
import com.example.candra.utils.Constant.BROADCAST_DATA_CAST_LOADED
import com.example.candra.utils.Constant.BROADCAST_DATA_DIGITAL_SHOW_LOADED
import com.example.candra.utils.Constant.BROADCAST_DATA_FAILED_TO_LOAD
import com.example.candra.utils.Constant.BROADCAST_DATA_INSERTED_TO_DB
import com.example.candra.utils.Constant.BROADCAST_DATA_KEYWORD_LOADED
import com.example.candra.utils.Constant.BROADCAST_LOAD_DATA_FINISHED
import com.example.candra.utils.Constant.BROADCAST_LOAD_DATA_STARTED
import com.example.candra.utils.Constant.BROADCAST_RETRY_LOAD_DATA
import com.example.candra.utils.Constant.INTENT_EXTRA_DATA_TYPE
import com.example.candra.utils.Constant.INTENT_EXTRA_ITEM_ID
import com.example.candra.utils.Constant.INTENT_EXTRA_LANGUAGE
import com.example.candra.utils.Constant.NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoadDataApiToDBListener : BroadcastReceiver() {
    private val apiRepository = ApiRepository()
    private val maxProgress = 4
    private var currentProgress = 0

    private fun dismissNotification(context: Context?, notificationId: Int) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun showNotification(
        context: Context?,
        notificationId: Int,
        titleResId: Int,
        textResId: Int,
        action: PendingIntent? = null,
        actionIcon: Int = 0,
        actionTextResId: Int = 0,
        maxProgress: Int? = null,
        currentProgress: Int? = null
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
        }

        action?.let {
            builder?.addAction(actionIcon, context.getString(actionTextResId), it)
        }

        if (maxProgress != null && currentProgress != null) {
            builder?.setProgress(maxProgress, currentProgress, false)
        }

        notificationManager.notify(notificationId, builder?.build())

    }

    private fun notifyWhenFinished(context: Context, intent: Intent) {
        val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
        context.sendBroadcast(Intent(BROADCAST_LOAD_DATA_FINISHED).apply {
            putExtra(INTENT_EXTRA_ITEM_ID, itemId)
        })
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("LoadApiReceiver", "onReceive")

        val favoriteMovieDao =
            context?.let { FavoriteRoomDatabase.getDatabase(it).favoriteMovieDao() }
        val favoriteTvShowDao =
            context?.let { FavoriteRoomDatabase.getDatabase(it).favoriteTvShowDao() }

        val favoriteRepository = FavoriteRepository(
            favoriteMovieDao!!,
            favoriteTvShowDao!!
        )

        val intentAction = intent?.action
        val title = R.string.title_notification_load_data

        when (intentAction) {
            BROADCAST_LOAD_DATA_STARTED -> {
                currentProgress = 0
                Log.d("LOADDATASTARTEDPROGRESS", currentProgress.toString())
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_fetching_data_from_API, null, 0, 0,
                    maxProgress, currentProgress
                )
            }
            BROADCAST_DATA_DIGITAL_SHOW_LOADED -> {
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                currentProgress = 1
                Log.d("LOAD_DATA_DIGITAL_SHOW", currentProgress.toString())
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_loaded_digital_show, null, 0, 0,
                    maxProgress, currentProgress
                )
                if (currentProgress == maxProgress) {
                    notifyWhenFinished(context, intent)
                }
            }
            BROADCAST_DATA_CAST_LOADED -> {
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                currentProgress = 2
                Log.d("LOAD_DATA_CAST", currentProgress.toString())
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_loaded_cast, null, 0, 0,
                    maxProgress, currentProgress
                )
                if (currentProgress == maxProgress) {
                    notifyWhenFinished(context, intent)
                }
            }
            BROADCAST_DATA_KEYWORD_LOADED -> {
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                currentProgress = 3
                Log.d("LOAD_DATA_KEYWORD", currentProgress.toString())
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_loaded_keywords, null, 0, 0,
                    maxProgress, currentProgress
                )
                if (currentProgress == maxProgress) {
                    notifyWhenFinished(context, intent)
                }
            }
            BROADCAST_DATA_INSERTED_TO_DB -> {
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                currentProgress = 4
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_saved_to_DB, null, 0, 0,
                    maxProgress, currentProgress
                )
                if (currentProgress == maxProgress) {
                    notifyWhenFinished(context, intent)
                }
            }
            BROADCAST_DATA_FAILED_TO_LOAD -> {
                currentProgress = 0
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                val itemType = intent.getIntExtra(INTENT_EXTRA_DATA_TYPE, 0)
                val language = intent.getStringExtra(INTENT_EXTRA_LANGUAGE)
                val retryIntent = Intent(context, LoadDataApiToDBListener::class.java).apply {
                    action = BROADCAST_RETRY_LOAD_DATA
                    putExtra(INTENT_EXTRA_ITEM_ID, itemId)
                    putExtra(INTENT_EXTRA_DATA_TYPE, itemType)
                    putExtra(INTENT_EXTRA_LANGUAGE, language)

                }

                val retryPendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, 0)
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_failed_fetch_API_data,
                    retryPendingIntent,
                    R.drawable.ic_refresh_24dp,
                    R.string.retry
                )
            }
            BROADCAST_RETRY_LOAD_DATA -> {
                currentProgress = 0
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                val itemType = intent.getIntExtra(INTENT_EXTRA_DATA_TYPE, 0)
                val language = intent.getStringExtra(INTENT_EXTRA_LANGUAGE)
                GlobalScope.launch {
                    apiRepository.loadDataAsync(context, itemType, itemId, language).await()?.let {
                        it.digitalShowDetails?.let { _ ->
                            saveToDatabase(it, language, favoriteRepository)
                            context.sendBroadcast(Intent(BROADCAST_DATA_INSERTED_TO_DB).apply {
                                putExtra(INTENT_EXTRA_ITEM_ID, itemId)
                            })

                        }

                    }
                }
            }

            BROADCAST_LOAD_DATA_FINISHED -> {
                val itemId = intent.getStringExtra(INTENT_EXTRA_ITEM_ID)
                showNotification(
                    context, itemId.toInt(),
                    title, R.string.status_load_data_finish
                )
                GlobalScope.launch {
                    Thread.sleep(5000)
                    dismissNotification(context, itemId.toInt())
                }
            }
        }
    }

}