package com.example.candra.data

import android.net.Uri
import com.example.candra.data.api.ApiConnection
import com.example.candra.data.api.TheMovieDBApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.Assert.*
import java.net.URL

class ApiResponseTest {

    @Test
    fun searchMovie() {
        val data = URL(TheMovieDBApi.searchMovie("avenger")).readText()
        print(data)
        assertEquals("1", data)

    }

    @Test
    fun assertURL() {
        val data = TheMovieDBApi.searchMovie("avenger")
        assertEquals("1", data)
    }
}