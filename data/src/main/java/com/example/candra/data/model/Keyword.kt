package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class Keyword(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null
)