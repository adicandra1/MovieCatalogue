package com.example.candra.data.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.candra.data.database.FavoriteMovieDao
import com.example.candra.data.database.FavoriteRoomDatabase
import com.example.candra.data.database.FavoriteTvShowDao
import com.example.candra.data.database.ReleaseTodayDao
import com.example.candra.data.model.FavoriteMovie
import com.example.candra.data.model.FavoriteTvShow
import com.example.candra.data.model.ReleaseToday
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DatabaseContentProvider : ContentProvider() {

    private lateinit var favMovieDao: FavoriteMovieDao
    private lateinit var favTvShowDao: FavoriteTvShowDao
    private lateinit var releaseTodayDao: ReleaseTodayDao

    override fun onCreate(): Boolean {

        context?.let { favMovieDao = FavoriteRoomDatabase.getDatabase(it).favoriteMovieDao() }
        context?.let { favTvShowDao = FavoriteRoomDatabase.getDatabase(it).favoriteTvShowDao() }
        context?.let { releaseTodayDao = FavoriteRoomDatabase.getDatabase(it).releaseTodayDao() }

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (MATCHER.match(uri)) {
            CODE_MOVIES -> selection?.let {
                favMovieDao.getAllMoviesCursor(language = it)
            } ?: throw IllegalArgumentException("please specify selection argument!")
            CODE_MOVIE -> selection?.let {
                favMovieDao.findByIdCursor(id = uri.lastPathSegment!!, language = it)
            } ?: throw IllegalArgumentException("please specify selection argument!")
            CODE_TVSHOWS -> selection?.let {
                favTvShowDao.getAllTvShowCursor(language = it)
            } ?: throw IllegalArgumentException("please specify selection argument!")
            CODE_TVSHOW -> selection?.let {
                favTvShowDao.findByIdCursor(id = uri.lastPathSegment!!, language = it)
            } ?: throw IllegalArgumentException("please specify selection argument!")
            CODE_RELEASE_TODAY -> releaseTodayDao.getResponseCursor(todayDate = uri.lastPathSegment!!)
            else -> throw IllegalArgumentException("Invalid URI : $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        if (MATCHER.match(uri) == CODE_INSERT_RELEASE_DATA) {
            val insertData = ReleaseToday(
                values.getAsString(ReleaseToday.COLUMN_ID),
                values.getAsString(ReleaseToday.COLUMN_RESPONSE)
            )
            GlobalScope.launch {
                releaseTodayDao.insert(insertData)
            }
        } else throw IllegalArgumentException("Invalid URI : $uri")
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (MATCHER.match(uri)) {
            CODE_MOVIES -> throw IllegalArgumentException("Please specify the id : URI/TABLENAME/ID")
            CODE_TVSHOWS -> throw IllegalArgumentException("Please specify the id : URI/TABLENAME/ID")
            CODE_MOVIE -> uri.lastPathSegment?.let { GlobalScope.launch { favMovieDao.delete(it) } }
            CODE_TVSHOW -> uri.lastPathSegment?.let { GlobalScope.launch { favTvShowDao.delete(it) } }
            CODE_RELEASE_TODAY -> uri.lastPathSegment?.let {
                GlobalScope.launch {
                    releaseTodayDao.delete(
                        it
                    )
                }
            }
            else -> throw IllegalArgumentException("Invalid URI : $uri")
        }
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        const val AUTHORITY = "com.example.candra.data.contentprovider"

        val URI_FAV_MOVIE: Uri = Uri.parse("content://$AUTHORITY/${FavoriteMovie.TABLE_NAME}")

        val URI_FAV_TV_SHOW: Uri = Uri.parse("content://$AUTHORITY/${FavoriteTvShow.TABLE_NAME}")

        val URI_RELEASE_TODAY: Uri = Uri.parse("content://$AUTHORITY/${ReleaseToday.TABLE_NAME}")

        private const val CODE_MOVIES = 1

        private const val CODE_MOVIE = 2

        private const val CODE_TVSHOWS = 3

        private const val CODE_TVSHOW = 4

        private const val CODE_RELEASE_TODAY = 5

        private const val CODE_INSERT_RELEASE_DATA = 6

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            /*
             * The calls to addURI() go here, for all of the content URI patterns that the provider
             * should recognize.
             */

            addURI(AUTHORITY, FavoriteMovie.TABLE_NAME, CODE_MOVIES)
            addURI(AUTHORITY, "${FavoriteMovie.TABLE_NAME}/#", CODE_MOVIE)


            addURI(AUTHORITY, FavoriteTvShow.TABLE_NAME, CODE_TVSHOWS)
            addURI(AUTHORITY, "${FavoriteTvShow.TABLE_NAME}/#", CODE_TVSHOW)

            addURI(AUTHORITY, "${ReleaseToday.TABLE_NAME}/*", CODE_RELEASE_TODAY)

            addURI(AUTHORITY, ReleaseToday.TABLE_NAME, CODE_INSERT_RELEASE_DATA)

        }
    }

}