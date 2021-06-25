package com.shelper.overlay

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class AsyncDeleteRecents : AsyncTask<String, Void, Void>() {

    var a: String? = null

    override fun doInBackground(vararg params: String?): Void? {
        var postParameters = "userid="+ params[0]

        try {
            val url = URL("https://shelper3.azurewebsites.net/deleteRecent.php")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            //httpURLConnection.setReadTimeout(5000);
            //httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            httpURLConnection.connect()
            val outputStream = httpURLConnection.outputStream
            outputStream.write(postParameters.toByteArray(charset("UTF-8")))
            outputStream.flush()
            outputStream.close()
            val responseStatusCode = httpURLConnection.responseCode
            Log.d(ContentValues.TAG, "response code - $responseStatusCode")
            val inputStream: InputStream
            inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                httpURLConnection.inputStream
            } else {
                httpURLConnection.errorStream
            }
            inputStream.close()
            httpURLConnection.disconnect()
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "InsertData: Error ", e)
        }
        return null
    }

}