package com.shelper.overlay;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static Activity firstActivity;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private String result;
    private String result2;
    private String search_word;
    private int id = 21;
    private String name = "";
    private final int REQ_PERMISSION = 1001;
    private int permission1;
    private int permission2;
    private String resultt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KakaoSdk.init(this, "ta404b66a2cf6ce41ce86c61ee1b5b545");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())).putExtra("edu", result);
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQ_PERMISSION);
        }

        setContentView(R.layout.activity_main);
        firstActivity = MainActivity.this;

        Button bt_make = (Button) findViewById(R.id.make);
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                startActivityForResult(intent, 123);
            }
        });


        final EditText editText = findViewById(R.id.editText2);
        final Button search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncSearch asyncSearch = new AsyncSearch();
                search_word = editText.getText().toString();
                try {
                    result2 = asyncSearch.execute(search_word).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search_result", result2);
                startActivity(intent);
            }
        });
        handleDeepLink();
    }

    private void startServ() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra("edu", result);
        intent.putExtra("id", id);
        startService(intent);
    }

    private void startMakeServ() {
        Intent intent = new Intent(MainActivity.this, MakeService.class);
        intent.putExtra("name", name);
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
        switch (requestCode) {
            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                if (!Settings.canDrawOverlays(this)) {
                    // TODO 동의를 얻지 못했을 경우의 처리
                } else {
                    startService(new Intent(MainActivity.this, MyService.class));
                }
                break;
            case 123:
                name = data.getStringExtra("name");
                startMakeServ();
                break;
            case REQ_PERMISSION:
                if (permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "권한 동의 거부시 사용이 불가합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void kakaoLogin(Context context) {
        UserApiClient.getInstance().isKakaoTalkLoginAvailable(context);

    }
    void startMain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MainActivity.class));
        } else {
            startService(new Intent(this, MainActivity.class));
        }
    }

    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData == null) {
                            Log.d("GAJA", "No have dynamic link");
                            return;
                        }
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        Log.d("GAJA", "deepLink: " + deepLink.getQueryParameter("tt"));

                        String segment = deepLink.getLastPathSegment();
                        Log.d("segment", segment);
                        AsyncTodo task = new AsyncTodo();
                        try {
                            resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id=" + deepLink.getQueryParameter("tt"), "sound").get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(MainActivity.this, MyService.class);
                        intent.putExtra("edu", resultt);
                        intent.putExtra("id", id);
                        startService(intent);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "getDynamicLink:onFailure", e);
                    }
                });
    }

}
