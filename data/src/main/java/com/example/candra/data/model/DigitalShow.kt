package com.example.candra.data.model

import com.google.gson.annotations.SerializedName


data class DigitalShow(

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("title", alternate = ["name"])
    var title: String? = null,

    @SerializedName("poster_path")
    var poster: String? = null,

    @SerializedName("overview")
    var overview: String? = null
)