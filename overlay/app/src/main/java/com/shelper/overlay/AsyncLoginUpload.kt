package com.shelper.overlay

import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AsyncLoginUpload : AsyncTask<String, Void?, Int?>() {
    private var nickname = ""
    private var kakaoid = 0
    private var idid = 0

    override fun onPreExecute() {
        super.onPreExecute()
    }

    public fun setting(kakao : Integer, nickname : String) {
        this.nickname = nickname;
        this.kakaoid = kakaoid;
    }


    protected override fun doInBackground(parameters: Array<String>): Int {
        var postParameters = "";
        postParameters += "kakaoid=" + parameters[0]
        postParameters += "&nickname=" + "'" +parameters[1] + "'"
        Log.d("poooo",postParameters);
        try {
            val url = URL("https://shelper3.azurewebsites.net/uploadLogin.php")
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

            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)

            val sb = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }

            Log.d("sdsd",sb.toString().trim())
            idid = Integer.parseInt(sb.toString().trim());
            inputStream.close()
            bufferedReader.close()
            httpURLConnection.disconnect()
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "InsertData: Error ", e)
            return 0
        }
        return idid
    }
}