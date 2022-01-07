package com.example.candra.moviecatalogue.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri

import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.view.activity.DetailActivity
import com.example.candra.utils.Constant
import com.example.candra.utils.Constant.TYPE_TVSHOW

/**
 * Implementation of App Widget functionality.
 */
class FavoriteTvShowWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra(Constant.ITEM_TYPE, TYPE_TVSHOW)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val widgetText = context.getString(R.string.tv_show)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.favorite_widget)
            views.setTextViewText(R.id.widgetTitle, widgetText)
            views.setRemoteAdapter(R.id.stackView, intent)
            views.setEmptyView(R.id.stackView, R.id.emptyDataText)


            val intentTemplate = Intent(context, FavoriteMovieWidget::class.java)
            intentTemplate.action = "favoriteWidget.PassIntent"
            intentTemplate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intentTemplate.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intentTemplate,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setPendingIntentTemplate(R.id.stackView, pendingIntent)


            //notifyRemoteAdapterDataHasChanged
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackView)


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == "favoriteWidget.PassIntent") {
            val id = intent.getStringExtra(Constant.ITEM_ID)
            val itemType = intent.getIntExtra(Constant.ITEM_TYPE, 0)

            val detailActivityOpen = Intent(context, DetailActivity::class.java)
            detailActivityOpen.putExtra(Constant.ITEM_ID, id)
            detailActivityOpen.putExtra(Constant.ITEM_TYPE, itemType)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                detailActivityOpen,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            pendingIntent.send()

        }
    }
}

