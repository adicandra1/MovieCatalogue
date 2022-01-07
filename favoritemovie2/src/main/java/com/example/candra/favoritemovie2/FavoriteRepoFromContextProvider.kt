package com.example.candra.favoritemovie2

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.candra.data.contentprovider.DatabaseContentProvider
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.data.model.FavoriteMovie
import com.example.candra.data.model.FavoriteTvShow
import com.example.candra.utils.Constant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


/**
 * This class contains functions to query data from content provider
 * these functions formerly placed inside [com.example.candra.data.database.FavoriteRepository] class
 * moved here to avoid confusions and to avoid coupled code
 */
class FavoriteRepoFromContextProvider {

    private fun getListFavoriteMovieFromContentProvider(
        context: Context?,
        uri: Uri,
        language: String
    ): LiveData<List<FavoriteMovie>> {
        val returnData = MutableLiveData<List<FavoriteMovie>>()
        GlobalScope.launch {
            val cursor = context?.contentResolver?.query(
                uri,
                arrayOf(
                    FavoriteMovie.COLUMN_ID,
                    FavoriteMovie.COLUMN_LANGUAGE,
                    FavoriteMovie.COLUMN_TITLE,
                    FavoriteMovie.COLUMN_POSTER_PATH,
                    FavoriteMovie.COLUMN_OVERVIEW
                ),
                language,
                null,
                null
            )
            cursor?.let {
                FavoriteMovie.fromCursortoList(it)?.let { list -> returnData.postValue(list) } ?: returnData.postValue(null)
            }
            cursor?.close()
        }
        return returnData
    }

    private fun getListFavoriteTvShowFromContentProvider(
        context: Context?,
        uri: Uri,
        language: String
    ): LiveData<List<FavoriteTvShow>> {
        val returnData = MutableLiveData<List<FavoriteTvShow>>()
        GlobalScope.launch {
            val cursor = context?.contentResolver?.query(
                uri,
                arrayOf(
                    FavoriteTvShow.COLUMN_ID,
                    FavoriteTvShow.COLUMN_LANGUAGE,
                    FavoriteTvShow.COLUMN_TITLE,
                    FavoriteTvShow.COLUMN_POSTER_PATH,
                    FavoriteTvShow.COLUMN_OVERVIEW
                ),
                language,
                null,
                null
            )
            cursor?.let {
                FavoriteTvShow.fromCursortoList(it)?.let { list -> returnData.postValue(list) } ?: returnData.postValue(null)
            }
            cursor?.close()
        }
        return returnData
    }

    fun loadFavoriteDigitalShowFromContentProvider(
        context: Context?,
        dataType: Int,
        language: String = Locale.getDefault().toString()
    ) : LiveData<List<DigitalShowDetails>> {
        return when (dataType) {
            Constant.TYPE_MOVIE -> Transformations.map(getListFavoriteMovieFromContentProvider(context,
                DatabaseContentProvider.URI_FAV_MOVIE, language)) { it }
            Constant.TYPE_TVSHOW -> Transformations.map(getListFavoriteTvShowFromContentProvider(context,
                DatabaseContentProvider.URI_FAV_TV_SHOW, language)) { it }
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

}