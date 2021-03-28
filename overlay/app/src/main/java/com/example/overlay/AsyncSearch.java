package com.example.overlay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AsyncSearch extends AsyncTask<String, Void, String> {

    String result = "";
    ArrayList<ListViewList> arrayList = new ArrayList<ListViewList>();
    String contents_name = "";
    int contents_userid = 0;
    int contents_id = 0;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null){
        }
        else {
            result = s;
        }
    }

    @Override
    protected String doInBackground(String[] params) {
        String postParameters = "search=";
        postParameters += params[0];

        try {
            URL url = new URL("https://shelper3.azurewebsites.net/search.php");
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
