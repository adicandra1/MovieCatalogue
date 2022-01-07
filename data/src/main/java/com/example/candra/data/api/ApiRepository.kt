package com.example.candra.data.api

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.candra.data.model.*
import com.example.candra.utils.Constant
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

sealed class MergedData
data class DataMovieDetails(val movieDetails: MovieDetails?) : MergedData()
data class DataTvShowDetails(val tvShowDetails: TvShowDetails?) : MergedData()
data class DataCasts(val casts: MutableList<Cast>?) : MergedData()
data class DataKeywords(val keywords: MutableList<Keyword>?) : MergedData()

class ApiRepository {

    private val mediatorLiveData = MediatorLiveData<MergedData>()

    private val gson = Gson()

    private val movieDetails: MutableLiveData<MovieDetails> = MutableLiveData()
    private val tvShowDetails: MutableLiveData<TvShowDetails> = MutableLiveData()
    private val casts: MutableLiveData<MutableList<Cast>> = MutableLiveData()
    private val keywords: MutableLiveData<MutableList<Keyword>> = MutableLiveData()
    private val searchResult = MutableLiveData<MutableList<DigitalShow>>()

    suspend fun loadDigitalShowDetailsFromAPI(dataType: Int, id: String, language: String) {
        when (dataType) {
            TYPE_MOVIE -> loadMovieDetails(id, language)
            TYPE_TVSHOW -> loadTvShowDetails(id, language)
            else -> throw IllegalArgumentException("invalid data Type")
        }
        //limit cast item to 5
        5.loadCasts(dataType, id)
        loadKeywords(dataType, id)
    }

    fun loadDataAsync(
        context: Context,
        dataType: Int,
        id: String,
        language: String
    ): Deferred<FavoriteInsertData?> = GlobalScope.async {
        context.sendBroadcast(Intent(Constant.BROADCAST_LOAD_DATA_STARTED).apply {
            putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
        })
        val data = FavoriteInsertData()
        when (dataType) {
            TYPE_MOVIE -> loadMovieDetailsAsync(id, language).await()?.let {
                @Suppress("SENSELESS_COMPARISON")
                if (it != null) {
                    data.digitalShowDetails = it
                    context.sendBroadcast(Intent(Constant.BROADCAST_DATA_DIGITAL_SHOW_LOADED).apply {
                        putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                    })
                } else {
                    context.sendBroadcast(Intent(Constant.BROADCAST_DATA_FAILED_TO_LOAD).apply {
                        putExtra(Constant.INTENT_EXTRA_DATA_TYPE, dataType)
                        putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                        putExtra(Constant.INTENT_EXTRA_LANGUAGE, language)
                    })
                }
            }
            TYPE_TVSHOW -> loadTvShowDetailsAsync(id, language).await()?.let {
                @Suppress("SENSELESS_COMPARISON")
                if (it != null) {
                    data.digitalShowDetails = it
                    context.sendBroadcast(Intent(Constant.BROADCAST_DATA_DIGITAL_SHOW_LOADED).apply {
                        putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                    })
                } else {
                    context.sendBroadcast(Intent(Constant.BROADCAST_DATA_FAILED_TO_LOAD).apply {
                        putExtra(Constant.INTENT_EXTRA_DATA_TYPE, dataType)
                        putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                        putExtra(Constant.INTENT_EXTRA_LANGUAGE, language)
                    })
                }
            }
        }

        loadCastsAsync(dataType, id, 5).await()?.casts.let {
            if (it != null) {
                data.casts = it
                context.sendBroadcast(Intent(Constant.BROADCAST_DATA_CAST_LOADED).apply {
                    putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                })
            } else {
                context.sendBroadcast(Intent(Constant.BROADCAST_DATA_FAILED_TO_LOAD).apply {
                    putExtra(Constant.INTENT_EXTRA_DATA_TYPE, dataType)
                    putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                    putExtra(Constant.INTENT_EXTRA_LANGUAGE, language)
                })
            }
        }
        loadKeywordsAsync(dataType, id).await()?.keywords.let {
            if (it != null) {
                data.keywords = it
                context.sendBroadcast(Intent(Constant.BROADCAST_DATA_KEYWORD_LOADED).apply {
                    putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                })
            } else {
                context.sendBroadcast(Intent(Constant.BROADCAST_DATA_FAILED_TO_LOAD).apply {
                    putExtra(Constant.INTENT_EXTRA_DATA_TYPE, dataType)
                    putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                    putExtra(Constant.INTENT_EXTRA_LANGUAGE, language)
                })
            }
        }
        return@async data
    }

    suspend fun loadSearchResult(dataType: Int, query: String) {
        loadSearchResultAsync(dataType, query).await()?.let { searchResult.postValue(it.digitalShows) }
            ?: searchResult.postValue(null)
    }

    fun getDigitalShowDetails(dataType: Int): LiveData<MergedData> {
        mediatorLiveData.removeSource(movieDetails)
        mediatorLiveData.removeSource(tvShowDetails)
        mediatorLiveData.removeSource(casts)
        mediatorLiveData.removeSource(keywords)

        when (dataType) {
            TYPE_MOVIE -> mediatorLiveData.addSource(movieDetails) {
                mediatorLiveData.value = DataMovieDetails(it)
            }
            TYPE_TVSHOW -> mediatorLiveData.addSource(tvShowDetails) {
                mediatorLiveData.value = DataTvShowDetails(it)
            }
        }
        mediatorLiveData.addSource(casts) {
            mediatorLiveData.value = DataCasts(it)
        }
        mediatorLiveData.addSource(keywords) {
            mediatorLiveData.value = DataKeywords(it)
        }

        return mediatorLiveData
    }

    fun getSearchResultLiveData() : LiveData<MutableList<DigitalShow>> = searchResult

    private fun loadMovieDetailsAsync(id: String, language: String): Deferred<MovieDetails?> {
        return GlobalScope.async {
            gson.fromJson(
                ApiConnection.doRequestAsync(
                    TheMovieDBApi.getMovieDetails(
                        id,
                        language
                    )
                ).await(),
                MovieDetails::class.java
            )
        }
    }

    private fun loadTvShowDetailsAsync(id: String, language: String) = GlobalScope.async {
        gson.fromJson(
            ApiConnection.doRequestAsync(
                TheMovieDBApi.getTvShowDetails(
                    id,
                    language
                )
            ).await(),
            TvShowDetails::class.java
        )
    }

    private fun loadCastsAsync(dataType: Int, id: String, limit: Int) = GlobalScope.async {
        val uri = when (dataType) {
            TYPE_MOVIE -> TheMovieDBApi.getCastsFromMovie(id)
            TYPE_TVSHOW -> TheMovieDBApi.getCastsFromTvShow(id)
            else -> throw IllegalArgumentException("invalid data Type")
        }
        val castFromJSON = gson.fromJson(
            ApiConnection.doRequestAsync(uri).await(),
            CastResponse::class.java
        )
        castFromJSON?.casts?.size?.let {
            if (limit != 0 && it >= limit) {
                Log.e("CAST size", castFromJSON.casts.size.toString())
                castFromJSON.casts.subList(limit, castFromJSON.casts.size).clear()
            }
        }

        return@async castFromJSON
    }

    private fun loadKeywordsAsync(dataType: Int, id: String) = GlobalScope.async {
        val uri = when (dataType) {
            TYPE_MOVIE -> TheMovieDBApi.getKeywordsFromMovie(id)
            TYPE_TVSHOW -> TheMovieDBApi.getKeywordsFromTvShow(id)
            else -> throw IllegalArgumentException("invalid data Type")
        }
        return@async gson.fromJson(
            ApiConnection.doRequestAsync(uri).await(),
            KeywordResponse::class.java
        )
    }

    private fun loadSearchResultAsync(dataType: Int, query: String) = GlobalScope.async {
        val uri = when (dataType) {
            TYPE_MOVIE -> TheMovieDBApi.searchMovie(query)
            TYPE_TVSHOW -> TheMovieDBApi.searchTvShow(query)
            else -> throw IllegalArgumentException("invalid data Type")
        }
        return@async gson.fromJson(
            ApiConnection.doRequestAsync(uri).await(),
            DigitalShowResponse::class.java
        )
    }

    fun loadReleasedTodayAsync(todayDate: String) = GlobalScope.async {
        ApiConnection.doRequestAsync(TheMovieDBApi.getReleasedToday(todayDate)).await()
    }


    private suspend fun loadMovieDetails(id: String, language: String) {
        loadMovieDetailsAsync(id, language).await()?.let { movieDetails.postValue(it) }
            ?: movieDetails.postValue(null)

    }

    private suspend fun loadTvShowDetails(id: String, language: String) {
        loadTvShowDetailsAsync(id, language).await()?.let { tvShowDetails.postValue(it) }
            ?: tvShowDetails.postValue(null)
    }


    private suspend fun Int.loadCasts(dataType: Int, id: String) {
        loadCastsAsync(dataType, id, this@loadCasts).await()?.let { casts.postValue(it.casts) }
            ?: casts.postValue(null)
    }


    private suspend fun loadKeywords(dataType: Int, id: String) {
        loadKeywordsAsync(dataType, id).await()?.let { keywords.postValue(it.keywords) }
            ?: keywords.postValue(null)

    }

}