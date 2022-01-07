package com.example.candra.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.candra.data.model.FavoriteMovie

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favoriteMovie WHERE language LIKE :language")
    fun getAllMovies(language: String): LiveData<List<FavoriteMovie>>

    @Query("SELECT * FROM favoriteMovie WHERE language LIKE :language")
    fun getAllMoviesCursor(language: String): Cursor

    @Query("SELECT * FROM favoriteMovie WHERE ${FavoriteMovie.COLUMN_ID} LIKE :id")
    fun findById(id: String): LiveData<FavoriteMovie>

    @Query("SELECT * FROM favoriteMovie WHERE ${FavoriteMovie.COLUMN_ID} LIKE :id AND language LIKE :language")
    fun findById(id: String, language: String): LiveData<FavoriteMovie>

    @Query("SELECT * FROM favoriteMovie WHERE ${FavoriteMovie.COLUMN_ID} LIKE :id AND language LIKE :language")
    fun findByIdCursor(id: String, language: String): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteMovie: FavoriteMovie)

    @Query("DELETE FROM favoriteMovie WHERE ${FavoriteMovie.COLUMN_ID} LIKE :id")
    suspend fun delete(id: String)
}