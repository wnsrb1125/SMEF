package com.shelper.overlay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class AsyncFavorites extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] params) {
        String postParameters = "userid=";
        postParameters += params[0];

        try {
            URL url = new URL("https://shelper3.azurewebsites.net/selectFavorites.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            //httpURLConnection.setReadTimeout(5000);
            //httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();

            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            inputStream.close();
            bufferedReader.close();
            httpURLConnection.disconnect();
            return sb.toString().trim();
        }catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);

            return null;
        }
    }

}
