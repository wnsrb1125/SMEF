package com.example.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class MyService extends Service{

    private int educations_length;
    private ArrayList<Education> educations = new ArrayList<Education>();
    private int count = 0;
    private int zero = 0;
    private Integer[] a = {11,65,180,75,190};
    private Painter painter;
    private Education education;
    private WindowManager wm;
    private View mView;
    private View graffitiView;
    private MediaPlayer mediaPlayer;
    private String media_path = "file:///data/data/com.example.overlay/3/";
    private int current_index = 0;
    private Intent passedIntent;
    private float START_X;                    //터치 시작 점
    private float START_Y;                    //터치 시작 점
    private int PREV_X;                            //뷰의 시작 점
    private int PREV_Y;                            //뷰의 시작 점
    private int MAX_X = -1, MAX_Y = -1;
    private int media_count = 1;
    private WindowManager.LayoutParams firstparams;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams params2;


    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(MAX_X == -1)
                        setMaxPosition();
                    START_X = event.getRawX();
                    START_Y = event.getRawY();
                    PREV_X = params.x;
                    PREV_Y = params.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int)(event.getRawX() - START_X);
                    int y = (int)(event.getRawY() - START_Y);

                    params.x = PREV_X + x;
                    params.y = PREV_Y + y;

                    //optimizePosition();
                    wm.updateViewLayout(mView, params);
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener graffitiViewTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            Toast.makeText(getApplicationContext(),event.getX()+","+event.getY(),Toast.LENGTH_SHORT).show();
            if((event.getX() > a[2] && event.getX() < a[4])&&
                    (event.getY() > a[3] && event.getY() < a[5])) {
                if (count < educations_length) {
                    count++;
                    media_count++;
                    player();
                }
            }
            return true;
        }
    };

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        firstparams = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,PixelFormat.RGB_888);


        params2 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.CENTER | Gravity.TOP;

        mView = inflate.inflate(R.layout.view_in_service, null);
        graffitiView = inflate.inflate(R.layout.graffiti_view, null);

        final Button bt_exit =  (Button) mView.findViewById(R.id.bt_exit);
        final Button bt_before =  (Button) mView.findViewById(R.id.bt_before);
        final Button bt_next =  (Button) mView.findViewById(R.id.bt_next);
        final Button bt_current =  (Button) mView.findViewById(R.id.bt_current);
        final LinearLayout ll = (LinearLayout) mView.findViewById(R.id.service_view);

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(passedIntent);
            }
        });

        bt_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == educations_length) {
                    count--;
                }
                player();
                count++;
            }
        });

        bt_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    media_count--;
                    player();
                }
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (count < educations_length - 1) {
                   count++;
                   media_count++;
                   player();
               }
           }
       });

        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
        wm.addView(graffitiView,firstparams);
        wm.addView(mView, params);
        mView.setOnTouchListener(mViewTouchListener);
        graffitiView.setOnTouchListener(graffitiViewTouchListener);
    }

    public void player() {

        if (painter != null && count != educations_length) {
            wm.removeView(painter);
        }
        if(count < educations_length ) {
            education = educations.get(count);
            a = education.getSoundPaint();
            painter = new Painter(this);
            painter.setDrawInformation(a);
            wm.addView(painter, params2);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(MyService.this, Uri.parse(media_path+count+".m4a"));
                mediaPlayer.prepare();
                mediaPlayer.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "" + count, Toast.LENGTH_SHORT).show();
        zero++;
    }

    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(matrix);

        MAX_X = matrix.widthPixels - mView.getWidth();
        MAX_Y = matrix.heightPixels - mView.getHeight();
    }

    private void optimizePosition() {
        if(params.x > MAX_X) params.x = MAX_X;
        if(params.y > MAX_Y) params.y = MAX_Y;
        if(params.x < 0) params.x = 0;
        if(params.y < 0) params.y = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        String result = intent.getStringExtra("edu");
        showResult(result);
        return super.onStartCommand(intent, flags, startId);
    }

    public ArrayList<Education> showResult(String mJsonString) {
        String TAG_JSON="webnautes";
        DisplayMetrics dm = getResources().getDisplayMetrics();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                Integer[] square = new Integer[8];

                int db_width = item.getInt("width");
                int db_height = item.getInt("height");

                int now_width = dm.widthPixels;
                int now_height = dm.heightPixels;

                float width_percent = (float)now_width / (float)db_width;
                float height_percent = (float)now_height / (float)db_height;




                int id = item.getInt("id");

                square[0] = item.getInt("thick");
                square[1] = item.getInt("color");
                square[2] = new Integer((int) ((item.getInt("location1") * width_percent)));
                square[3] = new Integer((int) ((item.getInt("location2") * height_percent)));
                square[4] = new Integer((int) ((item.getInt("location3") * width_percent)));
                square[5] = new Integer((int) ((item.getInt("location4") * height_percent)));
                square[6] = item.getInt("width");
                square[7] = item.getInt("height");

                Log.d("@################", Arrays.toString(square));
                Education education = new Education();

                education.setSoundPaint(square);

                educations.add(education);
                Log.d("*****************", String.valueOf(width_percent)+height_percent);

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
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            if(painter != null) {
                wm.removeView(painter);
                painter = null;
            }
            if(graffitiView != null) {
                wm.removeView(graffitiView);
                graffitiView = null;
            }
            wm = null;
        }
    }

}
