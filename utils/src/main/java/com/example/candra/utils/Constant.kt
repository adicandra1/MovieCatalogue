package com.example.candra.utils

object Constant {
    //api
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_URL_IMAGE = "https://image.tmdb.org/"

    //datatype
    const val TYPE_MOVIE = 1
    const val TYPE_TVSHOW = 2
    const val TYPE_CAST = 3
    const val TYPE_DIGITALSHOW = 4
    const val TYPE_FAVORITESHOW = 5

    const val NOTIFICATION_CHANNEL_ID = "channel_01"

    //image size
    const val POSTER_IMAGE_SIZE = "w500"
    const val CAST_IMAGE_SIZE = "w185"

    //intent constant
    const val ITEM_ID = "com.example.candra.moviecatalogue.ItemListFragment.itemId"
    const val ITEM_TYPE = "com.example.candra.moviecatalogue.ItemListFragment.itemtType"
    const val INTENT_EXTRA_ITEM_ID: String = "intentExtraItemID"
    const val INTENT_EXTRA_LANGUAGE: String = "intentExtraLanguage"
    const val INTENT_EXTRA_DATA_TYPE: String = "intentExtraDataType"

    //Intent Filter
    const val CONNECTION_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"

    //loadDataType
    const val LOAD_DATA_INITIAL_LOAD = 11
    const val LOAD_DATA_REFRESH = 12
    const val LOAD_DATA_REFRESH_DATABASE = 13

    //loadedDataObserver
    const val LOADED_DATA_MOVIE_TV_SHOW = 123
    const val LOADED_DATA_CAST = 124
    const val LOADED_DATA_KEYWORD = 125

    //custom broadcast
    const val BROADCAST_LOAD_DATA_STARTED = "broadcast.LOAD_DATA_STARTED"
    const val BROADCAST_LOAD_DATA_FINISHED = "broadcast.LOAD_DATA_FINISHED"
    const val BROADCAST_DATA_INSERTED_TO_DB = "broadcast.DATA_INSERTED_TO_DB"
    const val BROADCAST_DATA_DIGITAL_SHOW_LOADED = "broadcast.DATA_DIGITAL_SHOW_LOADED"
    const val BROADCAST_DATA_CAST_LOADED = "broadcast.DATA_CAST_LOADED"
    const val BROADCAST_DATA_KEYWORD_LOADED = "broadcast.DATA_KEYWORD_LOADED"
    const val BROADCAST_DATA_FAILED_TO_LOAD = "broadcast.DATA_FAILED_TO_LOAD"
    const val BROADCAST_RETRY_LOAD_DATA = "broadcast.RETRY_LOAD_DATA"

    const val MOVIE_FRAGMENT = "movieFragment"
    const val TVSHOW_FRAGMENT = "TvShowFragment"
    const val FAVORITE_TAB_FRAGMENT = "favoriteTabFragment"

    //globalVar
    var GLOBALVAR_IS_CONNECTED = true
    var IS_INITIAL_DATA_LOAD_DONE = false
}
