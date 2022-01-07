package com.example.candra.moviecatalogue.view.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Binder
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.candra.moviecatalogue.R
import com.example.candra.data.api.TheMovieDBApi
import com.example.candra.data.contentprovider.DatabaseContentProvider
import com.example.candra.data.model.DigitalShow
import com.example.candra.data.model.FavoriteMovie
import com.example.candra.data.model.FavoriteTvShow
import com.example.candra.utils.Constant
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW
import java.util.*

internal class StackRemoteViewsFactory(private val context: Context, private val intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {
    private val data: MutableList<DigitalShow?> = mutableListOf()
    private var itemType = -1
    private var widgetId = -1
    private var cursor: Cursor? = null

    override fun onCreate() {
        intent?.let { itemType = it.getIntExtra(Constant.ITEM_TYPE, 0) }
            ?: throw IllegalStateException("no Intent!")

    }

    override fun onDataSetChanged() {
        Thread.sleep(3000)
        intent?.let { itemType = it.getIntExtra(Constant.ITEM_TYPE, 0) }
            ?: throw IllegalStateException("no Intent!")
        val indentityToken = Binder.clearCallingIdentity()

        cursor = null

        //database query
        when (itemType) {
            TYPE_MOVIE -> cursor = context.contentResolver.query(
                DatabaseContentProvider.URI_FAV_MOVIE,
                arrayOf(
                    FavoriteMovie.COLUMN_ID,
                    FavoriteMovie.COLUMN_TITLE,
                    FavoriteMovie.COLUMN_POSTER_PATH
                ),
                Locale.getDefault().toString(),
                null,
                null
            )
            TYPE_TVSHOW -> cursor = context.contentResolver.query(
                DatabaseContentProvider.URI_FAV_TV_SHOW,
                arrayOf(
                    FavoriteTvShow.COLUMN_ID,
                    FavoriteTvShow.COLUMN_TITLE,
                    FavoriteTvShow.COLUMN_POSTER_PATH
                ),
                Locale.getDefault().toString(),
                null,
                null
            )
            else -> throw IllegalArgumentException("invalid data type: $itemType")
        }

        //reset data before adding
        data.clear()
        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    data.add(
                        DigitalShow(
                            id = it.getString(it.getColumnIndex(FavoriteMovie.COLUMN_ID)),
                            title = it.getString(it.getColumnIndex(FavoriteMovie.COLUMN_TITLE)),
                            poster = it.getString(it.getColumnIndex(FavoriteMovie.COLUMN_POSTER_PATH))
                        )
                    )
                } while (it.moveToNext())
            }
        }

        cursor?.close()

        Binder.restoreCallingIdentity(indentityToken)
    }

    override fun getViewAt(position: Int): RemoteViews? {
        intent?.let { widgetId = it.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0) }
            ?: throw IllegalStateException("no Intent!")
        intent.let { itemType = it.getIntExtra(Constant.ITEM_TYPE, 0) }

        var view: RemoteViews? = null
        if (data.size > 0) {
            view = RemoteViews(context.packageName, R.layout.widget_item)
            view.setTextViewText(R.id.titleText, data[position]?.title)
            val bitmap: FutureTarget<Bitmap> = Glide.with(context.applicationContext)
                .asBitmap()
                .load(data[position]?.poster?.let {
                    TheMovieDBApi.getPosterImage(
                        Constant.POSTER_IMAGE_SIZE,
                        it
                    )
                })
                .submit(100, 100)

            view.setImageViewBitmap(R.id.posterImage, bitmap.get())

            Glide.with(context).clear(bitmap)

            val fillIntent = Intent()
            fillIntent.putExtra(Constant.ITEM_ID, data[position]?.id)
            fillIntent.putExtra(Constant.ITEM_TYPE, itemType)

            view.setOnClickFillInIntent(R.id.posterImage, fillIntent)
        }

        return view
    }

    override fun getCount(): Int = data.size

    override fun getViewTypeCount(): Int = 1

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.id.progressBar2)
    }

    override fun getItemId(position: Int): Long =
        if (data.size > 0) data[position]?.id?.toLong()!! else 0

    override fun hasStableIds(): Boolean = true

    override fun onDestroy() {
        Log.d("onDestroyStackView", "onDestroy")
    }

}