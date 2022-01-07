package com.example.candra.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cast(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var castName: String? = null,

    @SerializedName("character")
    var castRole: String? = null,

    @SerializedName("profile_path")
    var castPhoto: String? = null

) : Parcelable