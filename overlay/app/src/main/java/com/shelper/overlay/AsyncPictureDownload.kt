package com.shelper.overlay

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

 class AsyncPictureDownload :
    AsyncTask<String?, Void?, Bitmap?>() {

    protected override fun doInBackground(vararg strings: String?): Bitmap? {
        var bmp: Bitmap? = null
        try {
            val img_url = strings[0] //url of the image
            val url = URL(img_url)
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmp
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }


}