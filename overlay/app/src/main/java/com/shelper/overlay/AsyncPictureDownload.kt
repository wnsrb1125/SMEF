package com.shelper.overlay

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AsyncPictureDownload(context: Context): AsyncTask<String,Void,String>()  {

    var thisContext = context
    lateinit var progress : ProgressDialog

    override fun onPreExecute() {
        progress = ProgressDialog(thisContext)
        progress.show()
    }

    override fun onPostExecute(result: String?) {
        progress.dismiss()
    }

    override fun doInBackground(vararg params: String?): String {
        val line = ""
        val postParameters = ""
        val bufferedReader: BufferedReader? = null
        val downpic = params[1].toString() + ".jpg"
        var uri_string = "nope"


        try {
            val url = URL(params[0])
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Accept-Encoding", "Identity")
            httpURLConnection.connect()

//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            outputStream.write(postParameters.getBytes("UTF-8"));
//            outputStream.flush();
//            outputStream.close();
            val responseStatusCode = httpURLConnection.responseCode
            Log.d(ContentValues.TAG, "response code - $responseStatusCode")
            val inputStream: InputStream
            inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                httpURLConnection.inputStream
            } else {
                httpURLConnection.errorStream
            }
            val nLen = httpURLConnection.contentLength
            val bytes = ByteArray(nLen)
            if(nLen > 1000) {
                Log.d("king2","data/data/com.shelper.overlay/$downpic")
                val picture = File("data/data/com.shelper.overlay/$downpic")
                val fos = FileOutputStream(picture)
                var read: Int
                while (true) {
                    Log.d(ContentValues.TAG, "while ")
                    read = inputStream.read(bytes)
                    if (read <= 0) break
                    fos.write(bytes, 0, read)
                }
                uri_string = "file:///data/data/com.shelper.overlay/$downpic"
                fos.close()

            }
            inputStream.close()
            httpURLConnection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri_string
    }


}