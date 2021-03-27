package com.example.overlay;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private static String ID_ADDRESS = "";
    private ArrayList<Education> educations = new ArrayList<Education>();
    private String result;
    private int id = 21;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())).putExtra("edu", result);
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }

        setContentView(R.layout.activity_main);

        Button bt_start = (Button) findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                //startMain();
                //startService(new Intent(MainActivity.this, AlwaysOnTopService.class));
            }
        });

        Button bt_make = (Button) findViewById(R.id.make);
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PopupActivity.class);
                startActivityForResult(intent,123);
            }
        });

        Button bt_db = (Button)findViewById(R.id.dbbutton);
        bt_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTodo task = new AsyncTodo();
                try {
                    result = task.execute("https://shelper3.azurewebsites.net/getjson.php?id="+id,"sound").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startServ();
            }
        });


        Button bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, MyService.class));
            }
        });

        Button search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),TestaActivity.class);
                startActivity(intent);

            }
        });
    }


    private void startServ() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra("edu",result);
        intent.putExtra("id",id);
        startService(intent);
    }

    private void startMakeServ() {
        Intent intent = new Intent(MainActivity.this, MakeService.class);
        intent.putExtra("name",name);
        startService(intent);
    }


    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        } else {
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
            } else {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        }
        else if (requestCode == 123) {
            name = data.getStringExtra("name");
            startMakeServ();
        }
    }
    void startMain(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MainActivity.class));
        } else {
            startService(new Intent(this, MainActivity.class));
        }
    }
}
