package com.example.candra.data.model

import android.database.Cursor
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.candra.data.fromJson
import com.google.gson.Gson

@Entity(tableName = FavoriteMovie.TABLE_NAME, primaryKeys = [FavoriteMovie.COLUMN_ID, "language"])
data class FavoriteMovie(

    @NonNull @ColumnInfo(name = COLUMN_ID) override var id: String,

    @NonNull @ColumnInfo(name = COLUMN_LANGUAGE) var language: String,

    @ColumnInfo(name = COLUMN_TITLE) override var title: String? = null,

    @ColumnInfo(name = COLUMN_POSTER_PATH) override var posterPath: String? = null,

    @ColumnInfo(name = COLUMN_OVERVIEW) override var overview: String? = null,

    @ColumnInfo(name = COLUMN_RUNTIME) var runtime: String? = null,

    @ColumnInfo(name = COLUMN_RELEASED_DATE) override var releasedDate: String? = null,

    @ColumnInfo(name = COLUMN_GENRES) override var genres: List<Genre>? = null,

    @ColumnInfo(name = COLUMN_BUDGET) var budget: String? = null,

    @ColumnInfo(name = COLUMN_REVENUE) var revenue: String? = null,

    @ColumnInfo(name = COLUMN_CASTS) var casts: List<Cast>? = null,

    @ColumnInfo(name = COLUMN_KEYWORDS) var keywords: List<Keyword>? = null
) : DigitalShowDetails {
    companion object {
        private val gson = Gson()

        const val TABLE_NAME = "favoriteMovie"

        const val COLUMN_ID = "_ID"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_TITLE = "title"
        const val COLUMN_POSTER_PATH = "posterPath"
        const val COLUMN_OVERVIEW = "overview"
        const val COLUMN_RUNTIME = "runtime"
        const val COLUMN_RELEASED_DATE = "releasedDate"
        const val COLUMN_GENRES = "genres"
        const val COLUMN_BUDGET = "budget"
        const val COLUMN_REVENUE = "revenue"
        const val COLUMN_CASTS = "casts"
        const val COLUMN_KEYWORDS = "keywords"

        fun fromCursor(cursor: Cursor) : FavoriteMovie {
            return FavoriteMovie(
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE)),
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                posterPath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                overview = cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
                runtime = cursor.getString(cursor.getColumnIndex(COLUMN_RUNTIME)),
                releasedDate = cursor.getString(cursor.getColumnIndex(COLUMN_RELEASED_DATE)),
                genres = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES))),
                budget = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET)),
                revenue = cursor.getString(cursor.getColumnIndex(COLUMN_REVENUE)),
                casts = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_CASTS))),
                keywords = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_KEYWORDS)))
            )
        }

        fun fromCursortoList(cursor: Cursor) : List<FavoriteMovie>? {
            val list = mutableListOf<FavoriteMovie>()
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        FavoriteMovie(
                            id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                            language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE)),
                            title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                            posterPath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                            overview = cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW))
                        )
                    )
                } while (cursor.moveToNext())
            }
            return list
        }
    }
}