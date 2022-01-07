package com.example.candra.moviecatalogue.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.example.candra.data.api.ApiRepository
import com.example.candra.data.database.FavoriteRepository
import com.example.candra.data.database.FavoriteRoomDatabase
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.data.model.FavoriteInsertData
import com.example.candra.data.saveToDatabase
import com.example.candra.moviecatalogue.view.widget.FavoriteMovieWidget
import com.example.candra.moviecatalogue.view.widget.FavoriteTvShowWidget
import com.example.candra.utils.Constant
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailItemViewModel(private val app: Application) : AndroidViewModel(app) {

    //variable declarations
    private val favoriteDigitalShowRepository: FavoriteRepository
    private val apiRepository = ApiRepository()

    //dataState
    var isInitialLoadDataDone = false

    val dataType: MutableLiveData<Int> = MutableLiveData()


    private val digitalShowDetailsFromApi =
        Transformations.switchMap(dataType) { apiRepository.getDigitalShowDetails(it) }

    private var favoriteShow: MediatorLiveData<DigitalShowDetails> = MediatorLiveData()

    init {
        val favoriteMovieDao = FavoriteRoomDatabase.getDatabase(app).favoriteMovieDao()
        val favoriteTvShowDao = FavoriteRoomDatabase.getDatabase(app).favoriteTvShowDao()
        favoriteDigitalShowRepository =
            FavoriteRepository(
                favoriteMovieDao,
                favoriteTvShowDao
            )
    }


    //to observe in Activity
    fun getDigitalShowDetailsFromApi() = digitalShowDetailsFromApi

    fun getFavoriteShow() = favoriteShow


    /*
     * DATA LOAD
     */

    fun loadApiData(dataType: Int, id: String, language: String) {
        viewModelScope.launch {
            apiRepository.loadDigitalShowDetailsFromAPI(dataType, id, language)
        }
    }

    fun loadFavoriteShow(dataType: Int, id: String, language: String) {
        val data = favoriteDigitalShowRepository.findById(dataType, id, language)
        favoriteShow.removeSource(data)
        favoriteShow.addSource<DigitalShowDetails>(data) {
            favoriteShow.value = it
        }
    }

    /**
     * SAVE both current locale data and other locale data to DB
     */
    fun addToFavorite(
        context: Context,
        favoriteInsertData: FavoriteInsertData,
        language: String,
        dataType: Int,
        id: String
    ) {
        when (language) {
            "en_US" -> {
                loadDataForOtherLocaleFromApiAndSaveTODB(context, dataType, id, "in_ID")
            }
            "in_ID" -> {
                loadDataForOtherLocaleFromApiAndSaveTODB(context, dataType, id, "en_US")
            }
            "id_ID" -> {
                loadDataForOtherLocaleFromApiAndSaveTODB(context, dataType, id, "en_US")
            }
        }
        saveToDatabase(favoriteInsertData, language, favoriteDigitalShowRepository)
        updateWidget(app, dataType)
    }

    private fun loadDataForOtherLocaleFromApiAndSaveTODB(
        context: Context,
        dataType: Int,
        id: String,
        language: String
    ) {
        GlobalScope.launch {
            apiRepository.loadDataAsync(context, dataType, id, language).await()?.let {
                it.digitalShowDetails?.let { _ ->
                    saveToDatabase(
                        it,
                        language,
                        favoriteDigitalShowRepository
                    )
                    context.sendBroadcast(Intent(Constant.BROADCAST_DATA_INSERTED_TO_DB).apply {
                        putExtra(Constant.INTENT_EXTRA_ITEM_ID, id)
                    })
                    updateWidget(app, dataType)
                }

            }
        }
    }

    fun removeFromFavorite(dataType: Int, id: String) {
        viewModelScope.launch {
            favoriteDigitalShowRepository.delete(dataType, id)
        }
        updateWidget(app, dataType)
    }

    fun isDataExistInDB(dataType: Int, id: String): LiveData<Boolean> =
        Transformations.map(
            favoriteDigitalShowRepository.findById(
                dataType,
                id
            )
        ) { it?.id == id }

    private fun updateWidget(context: Context, dataType: Int) {
        when (dataType) {
            TYPE_MOVIE -> {
                val intent = Intent(context, FavoriteMovieWidget::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, FavoriteMovieWidget::class.java))
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                app.sendBroadcast(intent)
            }
            TYPE_TVSHOW -> {
                val intent = Intent(context, FavoriteTvShowWidget::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, FavoriteTvShowWidget::class.java))
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                context.sendBroadcast(intent)
            }
        }
    }
}