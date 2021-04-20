package com.shelper.overlay;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AsyncFileUpload extends AsyncTask<String,Void,Boolean> {

    String a;
    ProgressDialog progressDialog;
    String mJsonString;
    private ArrayList<Integer> IdArray = new ArrayList<Integer>();
    private int count = 0;
    private ArrayList<Education> mArrayList = new ArrayList<Education>();
    private int id = 0;
    private String filepath = "";
    private int userid = 1;
    private File file;
    private Context CacheDeleteContext;
    int serverResponseCode = 0;


    @Override
    protected Boolean doInBackground(String[] postParameters) {
        try {
            String filename = postParameters[0];
            File file = new File(filename);
            URL url = new URL("http://shelper3.azurewebsites.net/uploadFile.php?userid="+userid);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 10 * 1024 * 1024;
            InputStream inputStream = new FileInputStream(file);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            //httpURLConnection.setReadTimeout(10000);
            //httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpURLConnection.setRequestProperty("Connection","Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("uploaded_file", filename);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + filename + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = inputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = inputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = inputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            inputStream.close();
            dos.flush();
            dos.close();

            serverResponseCode = httpURLConnection.getResponseCode();

            String serverResponseMessage = httpURLConnection.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);


        }catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);

            return false;
        }

        return true;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
    public void setFile(File file) {
        this.file = file;
    }


}
