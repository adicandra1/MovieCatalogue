package com.example.candra.data.model


import android.database.Cursor
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.candra.data.fromJson
import com.google.gson.Gson

@Entity(tableName = FavoriteTvShow.TABLE_NAME, primaryKeys = [FavoriteTvShow.COLUMN_ID, "language"])
data class FavoriteTvShow(

    @NonNull @ColumnInfo(name = COLUMN_ID) override var id: String,

    @NonNull @ColumnInfo(name = COLUMN_LANGUAGE) var language: String,

    @ColumnInfo(name = COLUMN_TITLE) override var title: String? = null,

    @ColumnInfo(name = COLUMN_POSTER_PATH) override var posterPath: String? = null,

    @ColumnInfo(name = COLUMN_OVERVIEW) override var overview: String? = null,

    @ColumnInfo(name = COLUMN_RUNTIME) var runtime: List<String>? = null,

    @ColumnInfo(name = COLUMN_RELEASED_DATE) override var releasedDate: String? = null,

    @ColumnInfo(name = COLUMN_GENRES) override var genres: List<Genre>? = null,

    @ColumnInfo(name = COLUMN_TYPE) var type: String? = null,

    @ColumnInfo(name = COLUMN_STATUS) var status: String? = null,

    @ColumnInfo(name = COLUMN_ORIGINAL_LANGUANGE) var originalLanguage: String? = null,

    @ColumnInfo(name = COLUMN_CASTS) var casts: List<Cast>? = null,

    @ColumnInfo(name = COLUMN_KEYWORDS) var keywords: List<Keyword>? = null

) : DigitalShowDetails {
    companion object {
        val gson = Gson()

        const val TABLE_NAME = "favoriteTvShow"

        const val COLUMN_ID = "_ID"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_TITLE = "title"
        const val COLUMN_POSTER_PATH = "posterPath"
        const val COLUMN_OVERVIEW = "overview"
        const val COLUMN_RUNTIME = "runtime"
        const val COLUMN_RELEASED_DATE = "releasedDate"
        const val COLUMN_GENRES = "genres"
        const val COLUMN_TYPE = "type"
        const val COLUMN_STATUS = "status"
        const val COLUMN_ORIGINAL_LANGUANGE = "originalLanguage"
        const val COLUMN_CASTS = "casts"
        const val COLUMN_KEYWORDS = "keywords"

        fun fromCursor(cursor: Cursor) : FavoriteTvShow {
            return FavoriteTvShow(
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE)),
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                posterPath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                overview = cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
                runtime = gson.fromJson<List<String>>(cursor.getString(cursor.getColumnIndex(COLUMN_RUNTIME))),
                releasedDate = cursor.getString(cursor.getColumnIndex(COLUMN_RELEASED_DATE)),
                genres = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES))),
                type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)),
                status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)),
                originalLanguage = cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANGUANGE)),
                casts = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_CASTS))),
                keywords = gson.fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_KEYWORDS)))
            )
        }

        fun fromCursortoList(cursor: Cursor) : List<FavoriteTvShow>? {
            val list = mutableListOf<FavoriteTvShow>()
            if (cursor.moveToFirst()) {
                do {
                    list.add(FavoriteTvShow(
                        id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                        language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE)),
                        title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        posterPath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                        overview = cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW))
                    ))
                } while (cursor.moveToNext())
            }
            return list
        }
    }
}