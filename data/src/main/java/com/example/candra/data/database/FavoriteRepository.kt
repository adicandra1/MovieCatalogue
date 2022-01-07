package com.example.candra.data.database


import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.data.model.FavoriteMovie
import com.example.candra.data.model.FavoriteTvShow
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW
import java.util.*

class FavoriteRepository(
    private val favoriteMovieDao: FavoriteMovieDao,
    private val favoriteTvShowDao: FavoriteTvShowDao
) {

    private fun findByIdFromDatabase(
        dataType: Int,
        id: String,
        language: String = Locale.getDefault().toString()
    ): LiveData<DigitalShowDetails> {
        return when (dataType) {
            TYPE_MOVIE -> Transformations.map(favoriteMovieDao.findById(id, language)) { it }
            TYPE_TVSHOW -> Transformations.map(favoriteTvShowDao.findById(id, language)) { it }
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    fun findById(
        dataType: Int,
        id: String,
        language: String = Locale.getDefault().toString()
    ): LiveData<DigitalShowDetails> {
        return findByIdFromDatabase(dataType, id, language)
    }

    private fun loadFavoriteDigitalShowFromDatabase(
        dataType: Int,
        language: String = Locale.getDefault().toString()
    ): LiveData<List<DigitalShowDetails>> {
        return when (dataType) {
            TYPE_MOVIE -> Transformations.map(favoriteMovieDao.getAllMovies(language)) { it }
            TYPE_TVSHOW -> Transformations.map(favoriteTvShowDao.getAllTvShow(language)) { it }
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    fun loadFavoriteDigitalShow(
        dataType: Int,
        language: String = Locale.getDefault().toString()
    ): LiveData<List<DigitalShowDetails>> {
        return loadFavoriteDigitalShowFromDatabase(dataType, language)
    }

    suspend fun <T> insert(data: T) {
        when (data) {
            is FavoriteMovie -> {
                favoriteMovieDao.insert(data)
            }
            is FavoriteTvShow -> favoriteTvShowDao.insert(data)
        }
    }

    suspend fun delete(dataType: Int, id: String) {
        when (dataType) {
            TYPE_MOVIE -> favoriteMovieDao.delete(id)
            TYPE_TVSHOW -> favoriteTvShowDao.delete(id)
        }
    }
}