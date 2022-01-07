package com.example.candra.data.model

import com.google.gson.annotations.SerializedName

data class TvShowDetails(

    @SerializedName("id")
    override var id: String,

    @SerializedName("original_name")
    override var title: String? = null,

    @SerializedName("poster_path")
    override var posterPath: String? = null,

    @SerializedName("overview")
    override var overview: String? = null,

    @SerializedName("first_air_date")
    override var releasedDate: String?,

    @SerializedName("episode_run_time")
    var runtime: List<String>? = null,

    @SerializedName("genres")
    override var genres: List<Genre>? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("original_language")
    var originalLanguage: String? = null

) : DigitalShowDetails