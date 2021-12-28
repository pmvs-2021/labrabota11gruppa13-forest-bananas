package com.zlatamigas.testbottomnavigation

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AnimeAPIController(var context: Context) {
    suspend fun getAnime(id: Int) = suspendCoroutine<Anime?>{ cont ->
        val queue = Volley.newRequestQueue(context)
        val url = "https://kitsu.io/api/edge/anime/$id" +
                "?fields[anime]=canonicalTitle,synopsis,averageRating," +
                "startDate,endDate,episodeCount,episodeLength,posterImage,ageRating,ageRatingGuide" +
                "&include=genres"
        val request = JsonObjectRequest(url, null,
            { response ->
                val data = response.getJSONObject("data")
                val attributes = data.getJSONObject("attributes")
                val genres = response.getJSONArray("included")
                val g = ArrayList<String>()
                for(i in 0 until genres.length()) {
                    val item = genres.getJSONObject(i).getJSONObject("attributes")
                    g.add(item.getString("name"))
                }
                var episodeLength: Int? = null
                var averageRating: Double? = null
                var episodeCount: Int? = null
                var startDate: Date? = null
                var endDate: Date? = null
                if (!attributes.isNull("episodeLength")) {
                    episodeLength = attributes.getInt("episodeLength")
                }
                if (!attributes.isNull("averageRating")) {
                    averageRating = attributes.getDouble("averageRating")
                }
                if (!attributes.isNull("episodeCount")) {
                    episodeCount = attributes.getInt("episodeCount")
                }
                if (!attributes.isNull("startDate")) {
                    startDate = SimpleDateFormat("yyyy-mm-dd")
                        .parse(attributes.getString("startDate"))
                }
                if (!attributes.isNull("endDate")) {
                    endDate = SimpleDateFormat("yyyy-mm-dd")
                        .parse(attributes.getString("endDate"))
                }
                val anime = Anime(
                    data.getInt("id"),
                    attributes.getString("canonicalTitle"),
                    attributes.getString("synopsis"),
                    averageRating,
                    startDate,
                    endDate,
                    episodeCount,
                    episodeLength,
                    attributes.getJSONObject("posterImage").getString("original"),
                    g,
                    "${attributes.getString("ageRating")}: " +
                            "${attributes.getString("ageRatingGuide")}"
                )
                cont.resume(anime)
            },
            { cont.resume(null) }
        )

        queue.add(request)
    }

    suspend fun getAnimes(searchString: String?,
                          sortBy: String?,
                          filterBy: ArrayList<String>?,
                          filters: ArrayList<String>?,
                          page: Int) = suspendCoroutine<ArrayList<Anime>?>{ cont ->
        val queue = Volley.newRequestQueue(context)
        var url = "https://kitsu.io/api/edge/anime" +
                "?fields[anime]=canonicalTitle,averageRating," +
                "episodeCount,episodeLength,posterImage,ageRating,ageRatingGuide" +
                "&include=genres" +
                "&page[limit]=20&page[offset]=${page * 20}"

        if (searchString != null && searchString.isNotEmpty()) {
            url += "&filter[text]=$searchString"
        }
        if (sortBy != null && sortBy.isNotEmpty()) {
            url +=  "&sort=-$sortBy"
        }
        if (filterBy != null && filterBy.isNotEmpty()
            && filters != null && filters.size == filterBy.size) {
            for (i in 0 until filterBy.size) {
                url += "filter[${filterBy[i]}]=${filters[i]}"
            }
        }

        val stringRequest = JsonObjectRequest(url, null,
            { response ->
                val animes = ArrayList<Anime>()
                val array = response.getJSONArray("data")
                for (i in 0 until array.length()) {
                    val data = array.getJSONObject(i)
                    val attributes = data.getJSONObject("attributes")
                    val genres = response.getJSONArray("included")
                    val g = ArrayList<String>()
                    for(i in 0 until genres.length()) {
                        val item = genres.getJSONObject(i).getJSONObject("attributes")
                        g.add(item.getString("name"))
                    }
                    var episodeLength: Int? = null
                    var averageRating: Double? = null
                    var episodeCount: Int? = null
                    if (!attributes.isNull("episodeLength")) {
                        episodeLength = attributes.getInt("episodeLength")
                    }
                    if (!attributes.isNull("averageRating")) {
                        averageRating = attributes.getDouble("averageRating")
                    }
                    if (!attributes.isNull("episodeCount")) {
                        episodeCount = attributes.getInt("episodeCount")
                    }
                    val anime = Anime(
                        data.getInt("id"),
                        attributes.getString("canonicalTitle"),
                        "",
                        averageRating,
                        Date(),
                        Date(),
                        episodeCount,
                        episodeLength,
                        attributes.getJSONObject("posterImage").getString("original"),
                        g,
                        "${attributes.getString("ageRating")}: " +
                                "${attributes.getString("ageRatingGuide")}"
                    )
                    animes.add(anime)
                }
                cont.resume(animes)
            },
            { cont.resume(null) })
        queue.add(stringRequest)
    }
}