package com.shelper.overlay;

import android.app.ProgressDialog;
import android.content.Context;
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

public class AsyncTodo extends AsyncTask<String, Void, String> {

    String a;
    String mJsonString;
    private ArrayList<Education> mArrayList = new ArrayList<Education>();
    private int id = 0;
    private String filepath = "";
    Context context;
    ProgressDialog dialog;

    public AsyncTodo(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        dialog.dismiss();
        if (result == null){
            Log.d(TAG,"여기까진됨###널"+result.toString());
        }
        else {
            Log.d(TAG,"여기까진됨@@@딘"+result.toString());
            mJsonString = result;
        }
    }

    public ArrayList<Education> getData() {
        return this.mArrayList;
    }

    @Override
    protected String doInBackground(String[] params) {

        String postParameters = "";
        String serverURL = params[0];
        filepath = params[1];



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


            inputStream.close();
            bufferedReader.close();
            httpURLConnection.disconnect();


            return sb.toString().trim();

        }catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);

            return null;
        }
    }

//    public ArrayList<Education> showResult() {
//
//        String TAG_JSON="webnautes";
//
//        try {
//            JSONObject jsonObject = new JSONObject(mJsonString);
//            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
//
//            for(int i=0;i<jsonArray.length();i++){
//
//                JSONObject item = jsonArray.getJSONObject(i);
//                Integer[] square = new Integer[8];
//                id = item.getInt("contentsid");
//
//                square[0] = item.getInt("thick");
//                square[1] = item.getInt("color");
//                square[2] = item.getInt("location1");
//                square[3] = item.getInt("location2");
//                square[4] = item.getInt("location3");
//                square[5] = item.getInt("location4");
//                square[6] = item.getInt("width");
//                square[7] = item.getInt("height");
//
//                Log.d("%%%%%%%%%%%%", Arrays.toString(square));
//                Education education = new Education();
//
//                education.setSoundPaint(square);
//
//                mArrayList.add(education);
//                Log.d("*****************", mArrayList.toString());
//
//            }
//
//        } catch (JSONException e) {
//            Log.d(TAG, "showResult : ", e);
//        }
//        try {
//            a = asyncDownload.execute("https://shelper3.azurewebsites.net/downloadww.php?id="+id+"&filepath="+filepath,""+id).get();
//            Log.d("^^^^^^^^^^^^^", a);
//        }  catch (Exception e) {
//            e.printStackTrace();
//            Log.d("err^^^^^^^^^^^^^", a);
//        }
//         return mArrayList;
//    }
}
