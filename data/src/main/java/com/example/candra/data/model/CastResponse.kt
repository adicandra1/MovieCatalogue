package com.example.candra.data.model

import com.example.candra.data.model.Cast
import com.google.gson.annotations.SerializedName

data class CastResponse(

    @SerializedName("cast")
    val casts: MutableList<Cast>? = null
)