package com.example.overlay;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class AsyncTodo extends AsyncTask<String, Void, String> {

    String a;
    ProgressDialog progressDialog;
    String mJsonString;
    private AsyncDownload asyncDownload = new AsyncDownload();
    private ArrayList<Integer> IdArray = new ArrayList<Integer>();
    private int count = 0;
    private ArrayList<Education> mArrayList = new ArrayList<Education>();
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS ="country";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result == null){
            Log.d(TAG,"여기까진됨###널"+result.toString());
        }
        else {
            Log.d(TAG,"여기까진됨@@@딘"+result.toString());
            mJsonString = result;
            showResult();
        }
    }

    public ArrayList<Education> getData() {
        return this.mArrayList;
    }

    @Override
    protected String doInBackground(String[] params) {

        String postParameters = "";
        String serverURL = params[0];



        try {
            URL url = new URL(serverURL);
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


            bufferedReader.close();
            httpURLConnection.disconnect();


            return sb.toString().trim();

        }catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);

            return null;
        }
    }

    public ArrayList<Education> showResult() {

        String TAG_JSON="webnautes";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                Integer[] square = new Integer[5];
                int id = item.getInt(TAG_ID);

                IdArray.add(item.getInt("id"));
                square[0] = item.getInt("id");
                square[1] = item.getInt("square1");
                square[2] = item.getInt("square2");
                square[3] = item.getInt("square3");
                square[4] = item.getInt("square4");

                Log.d("%%%%%%%%%%%%", Arrays.toString(square));
                Education education = new Education();

                education.setSoundPaint(square);

                mArrayList.add(education);
                Log.d("*****************", mArrayList.toString());

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
        try {
            a = asyncDownload.execute("http://192.168.0.36/downloadww.php?idarray="+IdArray.toString()).get();
            Log.d("^^^^^^^^^^^^^", a);
        }  catch (Exception e) {
            e.printStackTrace();
            Log.d("err^^^^^^^^^^^^^", a);
        }
        return mArrayList;
    }
}
