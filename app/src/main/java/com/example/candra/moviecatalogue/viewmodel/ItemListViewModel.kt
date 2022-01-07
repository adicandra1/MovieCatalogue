package com.example.candra.moviecatalogue.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.candra.data.api.ApiConnection
import com.example.candra.data.api.TheMovieDBApi
import com.example.candra.data.model.DigitalShow
import com.example.candra.data.model.DigitalShowResponse
import com.example.candra.utils.Constant.GLOBALVAR_IS_CONNECTED
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ItemListViewModel : ViewModel() {
    private val gson: Gson = Gson()
    private var digitalShows: MutableLiveData<MutableList<DigitalShow>> = MutableLiveData()


    fun loadDigitalShows(isMovie: Boolean, language: String) {
        val uri = if (isMovie) TheMovieDBApi.getMovies(language) else TheMovieDBApi.getTvShows(
            language
        )
        if (GLOBALVAR_IS_CONNECTED) {
            viewModelScope.launch {
                val digitalShowsfromJSON = gson.fromJson(
                    ApiConnection.doRequestAsync(uri).await(),
                    DigitalShowResponse::class.java
                )
                digitalShows.postValue(digitalShowsfromJSON?.digitalShows)
            }
        }

    }

    fun getDigitalShows(): MutableLiveData<MutableList<DigitalShow>> {
        return digitalShows
    }
}