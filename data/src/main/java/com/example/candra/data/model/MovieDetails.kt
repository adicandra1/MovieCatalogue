package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetails(

    @SerializedName("id")
    override var id: String,

    @SerializedName("original_title")
    override var title: String? = null,

    @SerializedName("poster_path")
    override var posterPath: String? = null,

    @SerializedName("release_date")
    override var releasedDate: String? = null,

    @SerializedName("runtime")
    var runtime: String? = null,

    @SerializedName("genres")
    override var genres: List<Genre>? = null,

    @SerializedName("overview")
    override var overview: String? = null,

    @SerializedName("budget")
    var budget: String? = null,

    @SerializedName("revenue")
    var revenue: String? = null

) : DigitalShowDetails