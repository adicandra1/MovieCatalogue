package com.example.candra.data.model

interface DigitalShowDetails {

    var id: String

    var title: String?

    var posterPath: String?

    var releasedDate: String?

    var genres: List<Genre>?

    var overview: String?

}