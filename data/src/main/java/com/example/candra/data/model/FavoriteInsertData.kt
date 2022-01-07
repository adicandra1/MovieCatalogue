package com.example.candra.data.model

data class FavoriteInsertData(
    var digitalShowDetails: DigitalShowDetails? = null,

    var casts: MutableList<Cast>? = null,

    var keywords: MutableList<Keyword>? = null
)