package com.zlatamigas.testbottomnavigation

import java.util.*
import kotlin.collections.ArrayList

class Anime(val id: Int,
            val title: String,
            val synopsis: String,
            val rating: Double?,
            val startDate: Date?,
            val endDate: Date?,
            val episodeCount: Int?,
            val episodeLength: Int?,
            val posterImage: String,
            val genres: ArrayList<String>,
            val ageRating: String ) {}