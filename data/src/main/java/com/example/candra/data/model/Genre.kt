package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class Genre(

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String? = null

)