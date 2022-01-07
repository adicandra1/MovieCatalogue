package com.example.candra.data

import android.util.Log
import com.example.candra.data.database.FavoriteRepository
import com.example.candra.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun <reified T> Gson.fromJson(json: String): T =
    this.fromJson<T>(json, object : com.google.gson.reflect.TypeToken<T>() {}.type)

fun saveToDatabase(
    favoriteInsertData: FavoriteInsertData,
    language: String,
    favoriteDigitalShowRepository: FavoriteRepository
) {
    favoriteInsertData.digitalShowDetails?.let {
        Log.d("addToFav", "REACHED: ${favoriteInsertData.digitalShowDetails?.id}")
        val data: DigitalShowDetails
        when (favoriteInsertData.digitalShowDetails) {
            is MovieDetails -> {
                val movieDetailsData = favoriteInsertData.digitalShowDetails as MovieDetails
                data = FavoriteMovie(
                    movieDetailsData.id,
                    language,
                    movieDetailsData.title,
                    movieDetailsData.posterPath,
                    movieDetailsData.overview,
                    movieDetailsData.runtime,
                    movieDetailsData.releasedDate,
                    movieDetailsData.genres,
                    movieDetailsData.budget,
                    movieDetailsData.revenue,
                    favoriteInsertData.casts,
                    favoriteInsertData.keywords
                )
                GlobalScope.launch {
                    favoriteDigitalShowRepository.insert(data)
                }
            }
            is TvShowDetails -> {
                val tvShowDetailsData = favoriteInsertData.digitalShowDetails as TvShowDetails
                data = FavoriteTvShow(
                    tvShowDetailsData.id,
                    language,
                    tvShowDetailsData.title,
                    tvShowDetailsData.posterPath,
                    tvShowDetailsData.overview,
                    tvShowDetailsData.runtime,
                    tvShowDetailsData.releasedDate,
                    tvShowDetailsData.genres,
                    tvShowDetailsData.type,
                    tvShowDetailsData.status,
                    tvShowDetailsData.originalLanguage,
                    favoriteInsertData.casts,
                    favoriteInsertData.keywords
                )
                GlobalScope.launch {
                    favoriteDigitalShowRepository.insert(data)
                }
            }
            else -> throw IllegalArgumentException("invalid dataType")
        }
    } ?: throw Exception("data null")

}