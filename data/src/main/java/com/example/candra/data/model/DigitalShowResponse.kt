package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class DigitalShowResponse(

    @SerializedName("results")
    val digitalShows: MutableList<DigitalShow>? = null
)