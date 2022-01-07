package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class KeywordResponse(
    @SerializedName("keywords", alternate = ["results"])
    val keywords: MutableList<Keyword>? = null
)