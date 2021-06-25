package com.shelper.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class MyService extends Service implements SensorEventListener {

    private long shakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.5F;
    private int educations_length;
    private ArrayList<Education> educations = new ArrayList<Education>();
    private int count = 0;
    private String filepath = "sound";
    private Integer[] a = {11,65,180,75,190};
    private String t = "";
    private int next_button_push = 0;
    private int play_button_push = 0;
    private int before_button_push = 0;
    private int again_button_push = 0;
    private Painter painter;
    private Education education;
    private WindowManager wm;
    private View mView;
    private View tView;
    private String media_path = "file:///data/data/com.shelper.overlay/";
    private Intent passedIntent;
    private int passedId;
    private int contents_id;
    private int mWidth;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams paramsk;
    private GestureDetector gestureDetector;
    private FloatingActionButton fab_before;
    private FloatingActionButton fab_next;
    private FloatingActionButton fab_again;
    private FloatingActionButton fab_close;
    private FloatingActionButton fab_play;
    private FloatingActionButton fab_url;
    private FloatingActionButton fab_text;
    private FloatingActionButton fab_menu;
    private MediaPlayer MotoMediaPlayer = null;
    private boolean isFabOpen;
    private int FLAG;
    private boolean ofswitch = true;
    private String edu_result;
    private int start_time;
    private String text;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    LayoutInflater inflate2;
    private boolean visibility_switch = true;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start_time = (int) (System.currentTimeMillis() / 1000);
        inflate2  = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        paramsk = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,PixelFormat.TRANSLUCENT);

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,PixelFormat.TRANSLUCENT);

        params2 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.CENTER | Gravity.TOP;

        mView = inflate.inflate(R.layout.view_in_service, null);
        fab_menu = mView.findViewById(R.id.fab_menu);
        fab_before = mView.findViewById(R.id.fab_before);
        fab_next = mView.findViewById(R.id.fab_next);
        fab_again = mView.findViewById(R.id.fab_current);
        fab_close = mView.findViewById(R.id.fab_cancel);
        fab_play = mView.findViewById(R.id.fab_playing);
        fab_url = mView.findViewById(R.id.fab_url);
        fab_text = mView.findViewById(R.id.fab_texts);
        final ConstraintLayout constraintLayout = mView.findViewById(R.id.service_view);
        gestureDetector = new GestureDetector(this,new SingleTapConfirm());
        ViewTreeObserver vto = constraintLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constraintLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = constraintLayout.getMeasuredWidth();
                mWidth = size.x - width;
            }
        });
        fab_menu.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                   if (!isFabOpen) {
                       showFab();
                   }
                   else {
                       closeFab();
                   }
                }else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = paramsk.x;
                            initialY = paramsk.y;

                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            return true;
                        case MotionEvent.ACTION_UP:
                            float xDiff = event.getRawX() - initialTouchX;
                            float yDiff = event.getRawY() - initialTouchY;
                            if ((Math.abs(xDiff) < 5) && (Math.abs(yDiff) < 5)) {
                                //close the service and remove the fab view
                            }
                            int middle = mWidth / 2;
                            float nearestXwall = paramsk.x >= middle ? mWidth : 0;
                            paramsk.x = (int) nearestXwall;
                            wm.updateViewLayout(mView, paramsk);

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int xDiff2 = Math.round(event.getRawX() - initialTouchX);
                            int yDiff2 = Math.round(event.getRawY() - initialTouchY);

                            paramsk.x = initialX + xDiff2;
                            paramsk.y = initialY + yDiff2;
                            wm.updateViewLayout(mView, paramsk);
                    }
                }
                    return false;
            }
        });
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int playing_time = (int) (System.currentTimeMillis() / 1000 - start_time);
                AsynclogContents asynclogContents = new AsynclogContents();
                try {
                    asynclogContents.execute(contents_id,playing_time,play_button_push,next_button_push,before_button_push,again_button_push).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ofswitch = false;
                if (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
                    MotoMediaPlayer.stop();
                }
                stopService(passedIntent);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("myservice",1);
                startActivity(intent);
            }
        });

        fab_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                again_button_push++;
                if (tView != null) {
                    wm.removeView(tView);
                    tView = null;
                }
                player();
            }
        });

        fab_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tView != null) {
                    wm.removeView(tView);
                    tView = null;
                } else {
                    tView = inflate2.inflate(R.layout.view_textservice, null);
                    wm.addView(tView,params);
                    TextView textView = tView.findViewById(R.id.textt);
                    TextView button = tView.findViewById(R.id.textt_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wm.removeView(tView);
                            tView = null;
                        }
                    });
                    Log.d("text",text);
                    textView.setText(text);
                }
            }
        });

        fab_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before_button_push++;
                if (count > 0) {
                    fab_text.setVisibility(View.INVISIBLE);
                    if (tView != null) {
                        wm.removeView(tView);
                        tView = null;
                    }
                    if (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
                        MotoMediaPlayer.stop();
                    }
                    count--;
                    fab_url.setVisibility(View.INVISIBLE);
                    player();
                } else {
                    Toast.makeText(getApplicationContext(), "첫번째 화면입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_button_push++;
                if (count < educations_length - 1) {
                    fab_text.setVisibility(View.INVISIBLE);
                    if (tView != null) {
                        wm.removeView(tView);
                        tView = null;
                    }
                    if (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
                        MotoMediaPlayer.stop();
                    }
                    count++;
                    fab_url.setVisibility(View.INVISIBLE);
                    player();
                } else {
                    Toast.makeText(getApplicationContext(), "마지막 화면입니다.",Toast.LENGTH_SHORT).show();
                    ofswitch = false;
                    if (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
                        MotoMediaPlayer.stop();
                    }
                    stopService(passedIntent);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("myservice",1);
                    startActivity(intent);
                }
            }
        });

        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_button_push++;
                if (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
                    MotoMediaPlayer.stop();
                }
               if (ofswitch) {
                   ofswitch = false;
                   fab_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
               }
               else {
                   ofswitch = true;
                   fab_play.setImageResource(R.drawable.ic_baseline_pause_24);
                   player();
               }
            }
        });

        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        paramsk.x = size.x;
        paramsk.y = size.y;
        wm.addView(mView, paramsk);
        showFab();
    }


//    @Override
//    public void onStart(Intent intent, int startId) {
//        player();
//    }

    public void autoplaying() {

    }
    public void showFab() {
        isFabOpen=true;
        fab_close.animate().translationX(-getResources().getDimension(R.dimen.standard_55));
        fab_next.animate().translationX(-getResources().getDimension(R.dimen.standard_105));
        fab_again.animate().translationX(-getResources().getDimension(R.dimen.standard_155));
        fab_before.animate().translationX(-getResources().getDimension(R.dimen.standard_205));
        fab_play.animate().translationX(-getResources().getDimension(R.dimen.standard_255));
        fab_url.animate().translationY(-getResources().getDimension(R.dimen.standard_305));
        fab_text.animate().translationX(-getResources().getDimension(R.dimen.standard_305));

    }

    public void closeFab() {
        isFabOpen=false;
        fab_again.animate().translationX(0);
        fab_close.animate().translationX(0);
        fab_before.animate().translationX(0);
        fab_next.animate().translationX(0);
        fab_play.animate().translationX(0);
        fab_text.animate().translationX(0);
        fab_url.animate().translationY(0);
    }
    public void player() {
        if (painter != null && count != educations_length) {
            Log.d("whathappened",count+"/"+painter.toString());
            wm.removeView(painter);
            painter = null;
        }
        if(count < educations_length ) {
            education = educations.get(count);
            if(education.getText().length() > 0) {
                fab_text.setVisibility(View.VISIBLE);
                if (tView == null) {
                    tView = inflate2.inflate(R.layout.view_textservice, null);
                }
                wm.addView(tView, params);
                TextView textView = tView.findViewById(R.id.textt);
                TextView button = tView.findViewById(R.id.textt_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wm.removeView(tView);
                        tView = null;
                    }
                });
                textView.setText(education.getText());
                text = education.getText();
            }
            if (education.getUrl().length() > 3) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("https://" + education.getUrl());
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {

                }

                fab_url.setVisibility(View.VISIBLE);
            }
            a = education.getSoundPaint();
            if(education.getSoundPaint()[3] != 0 && education.getSoundPaint()[4] != 0) {
                painter = new Painter(this);
                painter.setDrawInformation(a);
                wm.addView(painter, params2);
            }
            if (MotoMediaPlayer == null ) {
                MotoMediaPlayer = new MediaPlayer();
            }
            else if(MotoMediaPlayer.isPlaying()) {
                MotoMediaPlayer.stop();
                MotoMediaPlayer = new MediaPlayer();
            }
            else {
                MotoMediaPlayer = new MediaPlayer();
            }
            try {
                MotoMediaPlayer.setDataSource(MyService.this, Uri.parse(media_path+contents_id+"/"+count+".m4a"));
                MotoMediaPlayer.prepare();
                MotoMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
////                while (MotoMediaPlayer != null && MotoMediaPlayer.isPlaying()) {
////                }
//                if (ofswitch) {
//                        fab_next.performClick();
//                    } else {
//                        return;
//                    }
//            }
//        }, 10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ofswitch) {
            fab_next.performClick();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        passedIntent = intent;
        if (intent.hasExtra("contents_id")) {
            contents_id = intent.getIntExtra("contents_id",0);
        }
        if (intent.hasExtra("id")) {
            passedId = intent.getIntExtra("id",0);
        }
        edu_result = intent.getStringExtra("edu");
        showResult(edu_result);
        player();
        return super.onStartCommand(intent, flags, startId);
    }

    public ArrayList<Education> showResult(String mJsonString) {
        Log.d("씨발",mJsonString);
        String TAG_JSON="webnautes";
        DisplayMetrics dm = getResources().getDisplayMetrics();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                Integer[] square = new Integer[10];
                String url = "";
                String text = "";

                int db_width = item.getInt("width");
                int db_height = item.getInt("height");

                int now_width = dm.widthPixels;
                int now_height = dm.heightPixels;

                float width_percent = (float)now_width / (float)db_width;
                float height_percent = (float)now_height / (float)db_height;




                int id = item.getInt("contentsid");
                contents_id = id;

                square[0] = item.getInt("thick");
                square[1] = item.getInt("color");
                square[2] = new Integer((int) ((item.getInt("location1") * width_percent)));
                square[3] = new Integer((int) ((item.getInt("location2") * height_percent)));
                square[4] = new Integer((int) ((item.getInt("location3") * width_percent)));
                square[5] = new Integer((int) ((item.getInt("location4") * height_percent)));
                square[6] = item.getInt("seconds");
                square[7] = item.getInt("width");
                square[8] = item.getInt("height");
                url = item.getString("url");
                text = item.getString("text");

                Log.d("@################", id+"");
                Education education = new Education();
                education.setSoundPaint(square);
                education.setUrl(url);
                education.setText(text);

                educations.add(education);
                Log.d("*****************", String.valueOf(width_percent)+height_percent);

                try {
                    AsyncDownload asyncDownload = new AsyncDownload();
                    t = asyncDownload.execute("https://shelper3.azurewebsites.net/downloadww.php?id="+id+"&filepath="+filepath,""+id).get();
                    Log.d("download",t);
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.d("err^^^^^^^^^^^^^", t);
                }

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        educations_length = educations.size();
        return educations;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            if(painter != null) {
                wm.removeView(painter);
                painter = null;
            }
            wm = null;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float axisX = event.values[0];
        float axisY = event.values[0];
        float axisZ = event.values[0];

        float gravityX = axisX / SensorManager.GRAVITY_EARTH;
        float gravityY = axisY / SensorManager.GRAVITY_EARTH;
        float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

        Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
        double squaredD = Math.sqrt(f.doubleValue());
        float gForce = (float) squaredD;
        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            long currentTime = System.currentTimeMillis();
            if(shakeTime + SHAKE_SKIP_TIME > currentTime) {
                return;
            }
            shakeTime = currentTime;
            if (visibility_switch) {
                mView.setVisibility(View.GONE);
                visibility_switch = false;
            } else {
                mView.setVisibility(View.VISIBLE);
                visibility_switch = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

}

