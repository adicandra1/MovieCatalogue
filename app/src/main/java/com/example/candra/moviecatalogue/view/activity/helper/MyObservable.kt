package com.example.candra.moviecatalogue.view.activity.helper

interface MyObservable {
    fun addObserver(observer: AllLoadedDataSuccessfullyObserver)

    fun notifyAllDataLoaded()
}