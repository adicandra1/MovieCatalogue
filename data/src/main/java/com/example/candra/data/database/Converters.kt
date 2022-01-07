package com.example.candra.data.database

import androidx.room.TypeConverter
import com.example.candra.data.fromJson
import com.example.candra.data.model.Cast
import com.example.candra.data.model.Genre
import com.example.candra.data.model.Keyword
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun listGenreToString(data: List<Genre>?): String? = data?.let { Gson().toJson(it) }

    @TypeConverter
    fun stringToListGenre(data: String?): List<Genre>? = data?.let { Gson().fromJson(it) }

    @TypeConverter
    fun listCastToString(data: List<Cast>?): String? = data?.let { Gson().toJson(it) }

    @TypeConverter
    fun stringToListCast(data: String?): List<Cast>? = data?.let { Gson().fromJson(it) }

    @TypeConverter
    fun listKeywordToString(data: List<Keyword>?): String? = data?.let { Gson().toJson(it) }

    @TypeConverter
    fun stringToListKeyword(data: String?): List<Keyword>? = data?.let { Gson().fromJson(it) }

    @TypeConverter
    fun listStringToString(data: List<String>?): String? = data?.let { Gson().toJson(it) }

    @TypeConverter
    fun stringToListString(data: String?): List<String>? = data?.let { Gson().fromJson(it) }

}