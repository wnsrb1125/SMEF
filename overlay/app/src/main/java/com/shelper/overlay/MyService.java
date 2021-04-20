package com.shelper.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
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
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private String filepath = "sound";
    private Integer[] a = {11,65,180,75,190};
    private String t = "";
    private Painter painter;
    private Education education;
    private WindowManager wm;
    private View mView;
    private MediaPlayer mediaPlayer;
    private String media_path = "file:///data/data/com.shelper.overlay/";
    private int current_index = 0;
    private Intent passedIntent;
    private int passedId;
    private int mWidth;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams paramsk;
    private GestureDetector gestureDetector;
    private FloatingActionButton fab_before;
    private FloatingActionButton fab_next;
    private FloatingActionButton fab_again;
    private FloatingActionButton fab_close;
    private MediaPlayer MotoMediaPlayer = null;
    private boolean isFabOpen;
    private int FLAG;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        paramsk = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,PixelFormat.TRANSLUCENT);

        params2 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.CENTER | Gravity.TOP;

        mView = inflate.inflate(R.layout.view_in_service, null);
        FloatingActionButton floatingActionButton = mView.findViewById(R.id.fab_menu);
        fab_before = mView.findViewById(R.id.fab_before);
        fab_next = mView.findViewById(R.id.fab_next);
        fab_again = mView.findViewById(R.id.fab_current);
        fab_close = mView.findViewById(R.id.fab_cancel);
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
        floatingActionButton.setOnTouchListener(new View.OnTouchListener() {
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
                stopService(passedIntent);

            }
        });


        fab_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player();
            }
        });

        fab_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    player();
                }
            }
        });

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < educations_length - 1) {
                    count++;
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

    public void showFab() {
        isFabOpen=true;
        fab_close.animate().translationX(-getResources().getDimension(R.dimen.standard_55));
        fab_before.animate().translationX(-getResources().getDimension(R.dimen.standard_205));
        fab_next.animate().translationX(-getResources().getDimension(R.dimen.standard_105));
        fab_again.animate().translationX(-getResources().getDimension(R.dimen.standard_155));
    }

    public void closeFab() {
        isFabOpen=false;
        fab_again.animate().translationX(0);
        fab_close.animate().translationX(0);
        fab_before.animate().translationX(0);
        fab_next.animate().translationX(0);
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
                MotoMediaPlayer.setDataSource(MyService.this, Uri.parse(media_path+passedId+"/"+count+".m4a"));
                MotoMediaPlayer.prepare();
                MotoMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        String result = intent.getStringExtra("edu");
        passedId = intent.getIntExtra("id",0);
        showResult(result);
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
                Integer[] square = new Integer[8];

                int db_width = item.getInt("width");
                int db_height = item.getInt("height");

                int now_width = dm.widthPixels;
                int now_height = dm.heightPixels;

                float width_percent = (float)now_width / (float)db_width;
                float height_percent = (float)now_height / (float)db_height;




                int id = item.getInt("contentsid");

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

                try {

                }catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    AsyncDownload asyncDownload = new AsyncDownload();
                    t = asyncDownload.execute("https://shelper3.azurewebsites.net/downloadww.php?id="+id+"&filepath="+filepath,""+id).get();
                    Log.d("^^^^^^^^^^^^^", t);
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

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}

