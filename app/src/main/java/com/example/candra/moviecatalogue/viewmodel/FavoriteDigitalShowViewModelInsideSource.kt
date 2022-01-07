package com.example.candra.moviecatalogue.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.candra.data.database.FavoriteRepository
import com.example.candra.data.database.FavoriteRoomDatabase
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.favoritefeature2.viewmodel.FavoriteDigitalShowViewModel

class FavoriteDigitalShowViewModelInsideSource(application: Application) : FavoriteDigitalShowViewModel(application) {

    private val favoriteDigitalShowRepository: FavoriteRepository

    init {
        val favoriteMovieDao = FavoriteRoomDatabase.getDatabase(application).favoriteMovieDao()
        val favoriteTvShowDao = FavoriteRoomDatabase.getDatabase(application).favoriteTvShowDao()
        favoriteDigitalShowRepository =
            FavoriteRepository(
                favoriteMovieDao,
                favoriteTvShowDao
            )
    }

    override fun getSource(context: Context, dataType: Int): LiveData<List<DigitalShowDetails>> {
        return favoriteDigitalShowRepository.loadFavoriteDigitalShow(dataType)
    }

}