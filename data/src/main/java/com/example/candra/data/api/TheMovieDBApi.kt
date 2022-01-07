package com.example.candra.data.api

import com.example.candra.data.BuildConfig
import com.example.candra.utils.Constant.BASE_URL
import com.example.candra.utils.Constant.BASE_URL_IMAGE

object TheMovieDBApi {

    /**
     * Build uri string to get movie list from api source
     * @param language: prefered language {
     *     - english_USA: en_US
     *     - Indonesian: id_ID
     * }
     * @return uri string
     */
    fun getMovies(language: String) =
        "${BASE_URL}discover/movie?api_key=${BuildConfig.TMDB_API_KEY}&language=$language"

    /**
     * Build uri string to get tvshow list from api source
     * @param language: prefered language {
     *     - english_USA: en_US
     *     - Indonesian: id_ID
     * }
     * @return uri string
     */
    fun getTvShows(language: String) =
        "${BASE_URL}discover/tv?api_key=${BuildConfig.TMDB_API_KEY}&language=$language"

    /**
     * Build uri string to get movie details from api source
     * @param id: movie id
     * @return uri string
     */
    fun getMovieDetails(id: String, language: String) =
        "${BASE_URL}movie/$id?api_key=${BuildConfig.TMDB_API_KEY}&language=$language"

    fun getTvShowDetails(id: String, language: String) =
        "${BASE_URL}tv/$id?api_key=${BuildConfig.TMDB_API_KEY}&language=$language"

    fun getCastsFromMovie(id: String) =
        "${BASE_URL}movie/$id/credits?api_key=${BuildConfig.TMDB_API_KEY}"

    fun getCastsFromTvShow(id: String) =
        "${BASE_URL}tv/$id/credits?api_key=${BuildConfig.TMDB_API_KEY}"

    fun getKeywordsFromMovie(id: String) =
        "${BASE_URL}movie/$id/keywords?api_key=${BuildConfig.TMDB_API_KEY}"

    fun getKeywordsFromTvShow(id: String) =
        "${BASE_URL}tv/$id/keywords?api_key=${BuildConfig.TMDB_API_KEY}"

    fun searchMovie(query: String) =
        "${BASE_URL}search/movie?api_key=${BuildConfig.TMDB_API_KEY}&language=en_US&query=$query"

    fun searchTvShow(query: String) =
        "${BASE_URL}search/tv?api_key=${BuildConfig.TMDB_API_KEY}&language=en_US&query=$query"

    fun getReleasedToday(todayDate: String) =
        "${BASE_URL}discover/movie?api_key=${BuildConfig.TMDB_API_KEY}&primary_release_date.gte=$todayDate&primary_release_date.lte=$todayDate"

    /**
     * Build uri string to get poster image from api source
     * @param posterSize: choose poster size, which is: "w92", "w154", "w185", "w342", "w500", "w780", or "original". w stand for "picture width", and the number is the resolution in (px)
     * @return uri string
     */
    fun getPosterImage(posterSize: String, posterImagePath: String) =
        "${BASE_URL_IMAGE}t/p/$posterSize/$posterImagePath?api_key=${BuildConfig.TMDB_API_KEY}"
}