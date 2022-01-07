package com.example.candra.data.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.candra.data.model.ReleaseToday

@Dao
interface ReleaseTodayDao {

    @Query("SELECT * FROM ${ReleaseToday.TABLE_NAME} WHERE ${ReleaseToday.COLUMN_ID} LIKE :todayDate")
    fun getResponseCursor(todayDate: String) : Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(releaseToday: ReleaseToday)

    @Query("DELETE FROM ${ReleaseToday.TABLE_NAME} WHERE ${ReleaseToday.COLUMN_ID} LIKE :todayDate")
    suspend fun delete(todayDate: String)

}