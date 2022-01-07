package com.example.candra.data.api

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object ApiConnection {

    fun doRequestAsync(url: String): Deferred<String> = GlobalScope.async {
        getData(url)
    }

    /**
     * Using this function because on api 19 can't load https connection
     * the error = SLL error : connection aborted by peer
     * cause = on android api 19, the default TLS is TLSv1.0, while in TMDB server, TLSv1.0 is not supported
     *        reference = https://stackoverflow.com/questions/42468807/javax-net-ssl-sslexception-ssl-handshake-aborted-on-android-old-devices
     *
     * solution = create custom ssl factory to get TLSv1.1 and TLSv1.2
     **/
    private fun getData(
        url: String,
        socketFactory: TLSSocketFactoryCompat = TLSSocketFactoryCompat()
    ): String {
        var conn: HttpsURLConnection? = null
        var response = ""
        try {
            conn = URL(url).openConnection() as HttpsURLConnection
            conn.sslSocketFactory = socketFactory
            conn.requestMethod = "GET"
            val responseCode = conn.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = readStream(conn.inputStream)
            }

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return response
    }

    private fun readStream(inputStream: InputStream): String {
        var reader: BufferedReader? = null
        val response = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(inputStream))
            val line: String = reader.readLine()
            response.append(line)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return response.toString()
    }
}