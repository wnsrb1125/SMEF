package com.shelper.overlay

import android.os.AsyncTask
import android.app.ProgressDialog
import com.shelper.overlay.Education
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class AsyncFileUpload : AsyncTask<String, Void?, Boolean>() {
    var a: String? = null
    private var userid = 0
    private var file: File? = null
    private val CacheDeleteContext: Context? = null
    var serverResponseCode = 0
    override fun doInBackground(postParameters: Array<String>): Boolean {
        var true_parameter = 1;
        if (postParameters[1].length < 3) {
            true_parameter = 0;
        }
        try {
            val filename = postParameters[0]
            val filename2 = postParameters[1]
            val file = File(filename)
            val url = URL("http://shelper3.azurewebsites.net/uploadFile.php?userid=$userid&tfpm=$true_parameter")
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "*****"
            var bytesRead: Int
            var bytesAvailable: Int
            var bufferSize: Int
            var buffer: ByteArray
            val maxBufferSize = 10 * 1024 * 1024
            val inputStream: InputStream = FileInputStream(file)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            //httpURLConnection.setReadTimeout(10000);
            //httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.useCaches = false
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data")
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive")
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
            httpURLConnection.setRequestProperty("uploaded_file", filename)
            val dos = DataOutputStream(httpURLConnection.outputStream)

            //1
            dos.writeBytes(twoHyphens + boundary + lineEnd)
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"$filename\"$lineEnd")
            dos.writeBytes(lineEnd)
            bytesAvailable = inputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)
            bytesRead = inputStream.read(buffer, 0, bufferSize)
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize)
                bytesAvailable = inputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                bytesRead = inputStream.read(buffer, 0, bufferSize)
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd)
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
            inputStream.close()
            dos.flush()
            if(true_parameter == 0) {
                serverResponseCode = httpURLConnection.responseCode
                val serverResponseMessage = httpURLConnection.responseMessage
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode)
            }

            //2
            if(true_parameter == 1) {
                val file2 = File(filename2)
                val inputStream2: InputStream = FileInputStream(file2)
                dos.writeBytes(twoHyphens + boundary + lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file2\";filename=\"$filename2\"$lineEnd")
                dos.writeBytes(lineEnd)
                bytesAvailable = inputStream2.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)
                bytesRead = inputStream2.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize)
                    bytesAvailable = inputStream2.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = inputStream2.read(buffer, 0, bufferSize)
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd)
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
                inputStream2.close()
                dos.flush()
                serverResponseCode = httpURLConnection.responseCode
                val serverResponseMessage = httpURLConnection.responseMessage
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode)
            }
            dos.close()
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "InsertData: Error ", e)
            return false
        }
        return true
    }

    private fun upload(filename: String, inputStream: InputStream) {}
    override fun onPreExecute() {
        super.onPreExecute()
    }

    fun setUserid(userid: Int) {
        this.userid = userid
    }

    fun setFile(file: File?) {
        this.file = file
    }
}