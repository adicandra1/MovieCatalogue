package com.example.candra.data.model

import android.database.Cursor
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = ReleaseToday.TABLE_NAME)
data class ReleaseToday(
    @NonNull @ColumnInfo(name = COLUMN_ID) @PrimaryKey var id: String,

    @ColumnInfo(name = COLUMN_RESPONSE) var response: String?
) {
    companion object {
        private val gson = Gson()

        const val TABLE_NAME = "releaseToday"

        const val COLUMN_ID = "_ID"
        const val COLUMN_RESPONSE = "response"

        fun fromCursorToListDigitalShow(cursor: Cursor): MutableList<DigitalShow>? {
            return if (cursor.moveToFirst()) {
                val response = cursor.getString(cursor.getColumnIndex(COLUMN_RESPONSE))
                gson.fromJson(response, DigitalShowResponse::class.java).digitalShows
            } else {
                null
            }
        }


    }
}

