package com.example.candra.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.candra.data.model.FavoriteTvShow

@Dao
interface FavoriteTvShowDao {

    @Query("SELECT * FROM favoriteTvShow WHERE language LIKE :language")
    fun getAllTvShow(language: String): LiveData<List<FavoriteTvShow>>

    @Query("SELECT * FROM favoriteTvShow WHERE language LIKE :language")
    fun getAllTvShowCursor(language: String): Cursor

    @Query("SELECT * FROM favoriteTvShow WHERE ${FavoriteTvShow.COLUMN_ID} LIKE :id")
    fun findById(id: String): LiveData<FavoriteTvShow>

    @Query("SELECT * FROM favoriteTvShow WHERE ${FavoriteTvShow.COLUMN_ID} LIKE :id AND language LIKE :language")
    fun findById(id: String, language: String): LiveData<FavoriteTvShow>

    @Query("SELECT * FROM favoriteTvShow WHERE ${FavoriteTvShow.COLUMN_ID} LIKE :id AND language LIKE :language")
    fun findByIdCursor(id: String, language: String): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteMovie: FavoriteTvShow)

    @Query("DELETE FROM favoriteTvShow WHERE ${FavoriteTvShow.COLUMN_ID} LIKE :id")
    suspend fun delete(id: String)
}