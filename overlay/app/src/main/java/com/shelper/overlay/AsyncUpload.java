package com.shelper.overlay;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AsyncUpload extends AsyncTask<Education, Void, Void> {

    String a;
    ProgressDialog progressDialog;
    String mJsonString;
    private ArrayList<Integer> IdArray = new ArrayList<Integer>();
    private int count = 0;
    private ArrayList<Education> mArrayList = new ArrayList<Education>();
    private int id = 0;
    private String filepath = "";
    private String name = "";
    private int userid = 0;
    private int widthPixel = 0;
    private int heightPixel = 0;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setWidth(int widthPixel) {
        this.widthPixel = widthPixel;
    }

    public void setHeight(int heightPixel) {
        this.heightPixel = heightPixel;
    }

    public ArrayList<Education> getData() {
        return this.mArrayList;
    }

    @Override
    protected Void doInBackground(Education[] educations) {

        Gson gson = new Gson();
        String postParameters = "squares=";
        postParameters += gson.toJson(educations);
        postParameters += "&userid=" + userid;
        postParameters += "&width=" + widthPixel;
        postParameters += "&height=" + heightPixel;
        postParameters += "&content_name='" + name+"'";
        Log.d("lllog", postParameters);
        try {
            URL url = new URL("https://shelper3.azurewebsites.net/upload.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            //httpURLConnection.setReadTimeout(5000);
            //httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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

            inputStream.close();
            httpURLConnection.disconnect();
            
        }catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);

            return null;
        }
        return null;
    }

}
