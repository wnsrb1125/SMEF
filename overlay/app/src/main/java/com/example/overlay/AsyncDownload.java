package com.example.overlay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.ContentValues.TAG;

public class AsyncDownload extends AsyncTask<String,Void,String> {


    @Override
    protected String doInBackground(String[] params) {

        String line = "";
        String postParameters = "";
        BufferedReader bufferedReader = null;
        String zipname = params[1]+".zip";

        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Encoding","Identity");
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
            Log.d("!@#%$",nLen+"");
            byte[] bytes = new byte[nLen];

            File voice = new File("data/data/com.example.overlay/"+zipname);
            FileOutputStream fos = new FileOutputStream(voice);

            int read;
            while(true) {
                Log.d(TAG, "while ");
                read = inputStream.read(bytes);
                if (read <= 0)
                    break;
                fos.write(bytes,0,read);
            }

            inputStream.close();
            fos.close();
            httpURLConnection.disconnect();
            unpackZip("data/data/com.example.overlay/",zipname, params[1]);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    private boolean unpackZip(String path, String zipname, String foldername){
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            int count2 = 0;

            File folder = new File(path + foldername);
            if(!folder.exists()) {
                folder.mkdirs();
            }
            path = path + foldername + "/";

            while ((ze = zis.getNextEntry()) != null){
                filename = ze.getName();


                File fmd = new File(path + filename);
                if (ze.isDirectory()) {

                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + count2+".m4a");

                while ((count = zis.read(buffer)) != -1){
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
                count2++;
            }

            zis.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected static File unzipEntry(ZipInputStream zis, File targetFile) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return targetFile;
    }


}
