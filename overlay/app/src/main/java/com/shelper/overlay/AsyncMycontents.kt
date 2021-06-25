package com.shelper.overlay

import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AsyncMycontents : AsyncTask<String, Void, String>() {
    override fun doInBackground(params: Array<String>): String? {
        var postParameters = "id="
        postParameters += params[0]
        return try {
            val url = URL("https://shelper3.azurewebsites.net/myContents.php")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            //httpURLConnection.setReadTimeout(5000);
            //httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.requestMethod = "POST"
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
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            inputStream.close()
            bufferedReader.close()
            httpURLConnection.disconnect()
            sb.toString().trim { it <= ' ' }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "InsertData: Error ", e)
            null
        }
    }
}