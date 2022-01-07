package com.example.candra.favoritefeature2.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.candra.data.model.DigitalShowDetails

abstract class FavoriteDigitalShowViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDigitalShow: MediatorLiveData<List<DigitalShowDetails>> = MediatorLiveData()

    abstract fun getSource(context: Context, dataType: Int) : LiveData<List<DigitalShowDetails>>

    fun loadData(context: Context, dataType: Int) {
        val source = getSource(context, dataType)
        favoriteDigitalShow.removeSource(source) //to avoid leaks
        favoriteDigitalShow.addSource<List<DigitalShowDetails>>(source) {
            favoriteDigitalShow.value = it
        }
    }

    fun getFavoriteDigitalShow() = favoriteDigitalShow
}