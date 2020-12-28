package com.example.overlay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class AsyncDownload extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String[] params) {

        String line = "";
        String postParameters = "";
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            outputStream.write(postParameters.getBytes("UTF-8"));
//            outputStream.flush();
//            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;

            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();

            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            int nLen = httpURLConnection.getContentLength();
            byte[] bytes = new byte[nLen];

            File voice = new File("data/data/com.example.overlay/o.m4a");
            FileOutputStream fos = new FileOutputStream(voice);

            int read;
            while(true) {
                read = inputStream.read(bytes);
                if (read <= 0)
                    break;
                fos.write(bytes,0,read);
            }


            inputStream.close();
            fos.close();
            httpURLConnection.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }
}
