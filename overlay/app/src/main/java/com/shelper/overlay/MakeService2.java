package com.shelper.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MakeService2 extends Service {
    private ArrayList<Education> educations = new ArrayList<Education>();
    private ArrayList<File> files = new ArrayList<File>();
    private ArrayList<String> filenames = new ArrayList<String>();
    private int count = 0;
    private int zero = 0;
    LayoutInflater inflate;
    private Integer[] draw;
    private Painter painter;
    private WindowManager wm;
    private View mView;
    private View squareView;
    private View graffitiView;
    private View amuseMentView;
    private MediaPlayer mediaPlayer;
    private Intent passedIntent;
    private float START_X;                    //터치 시작 점
    private float START_Y;                    //터치 시작 점
    float START_X2 = 0;
    float START_Y2 = 0;
    private int onoff = 0;
    private int again = 0;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private MediaRecorder recorder;
    private File file;
    private int folder_onoff = 1;
    private File dir;
    private String name = "";
    private long now;
    private FloatingActionButton fab_before;
    private FloatingActionButton fab_next;
    private FloatingActionButton fab_close;
    private FloatingActionButton fab_rec;
    private FloatingActionButton fab_lock;
    private FloatingActionButton fab_end;
    private FloatingActionButton fab_menu2;
    private FloatingActionButton fab_menu3;
    private FloatingActionButton fab_thick;
    private FloatingActionButton fab_color;
    private FloatingActionButton fab_delete;
    private FloatingActionButton fab_confirm;
    private FloatingActionButton fab_play;
    private boolean isFabOpen;
    private boolean isFabOpen2;
    private GestureDetector gestureDetector;
    private GestureDetector gestureDetector2;
    private int mWidth;
    private int mWidth2;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams params3;
    private WindowManager.LayoutParams params4;
    private WindowManager.LayoutParams params5;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout square_layout;
    private ViewTreeObserver vto;
    private ViewTreeObserver vto2;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(painter != null) {
                        wm.removeView(painter);
                        painter = null;
                        onoff = 1;
                    }
                    painter = new Painter(getApplicationContext());
                    START_X2 = event.getX();
                    START_Y2 = event.getY();
                    draw = new Integer[] {1,1,Integer.valueOf((int)START_X2),Integer.valueOf((int)START_Y2),Integer.valueOf((int)START_X2+1),Integer.valueOf((int)START_Y2+1)};
                    painter.setDrawInformation(draw);
                    wm.addView(painter,params4);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float ingX = event.getX();
                    float ingY = event.getY();
                    draw = new Integer[] {1,1,Integer.valueOf((int)START_X2),Integer.valueOf((int)START_Y2),Integer.valueOf((int)ingX),Integer.valueOf((int)ingY)};
                    painter.setDrawInformation(draw);
                    painter.invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    Integer finalX = Integer.valueOf((int)event.getX());
                    Integer finalY = Integer.valueOf((int)event.getY());
                    wm.removeView(squareView);
                    Education education = new Education();
                    education.setSoundPaint(new Integer[] {1,1,Integer.valueOf((int)START_X2),Integer.valueOf((int)START_Y2),finalX,finalY});
                    if (onoff == 1) {
                        Toast.makeText(getApplicationContext(),count+"",Toast.LENGTH_SHORT).show();
                        educations.set(count,education);
                        onoff = 0;
                    }
                    else {
                        educations.add(education);
                    }
                    wm.addView(squareView,params3);
                    squareView.setOnTouchListener(touchListener);
                    //setFab_menu3();
                    break;
            }
            return true;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        now = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        date = new Date(now);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        params = new WindowManager.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                , PixelFormat.TRANSLUCENT);

        params2 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);

        params3 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params4 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params5 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.CENTER | Gravity.TOP;

        mView = inflate.inflate(R.layout.view_in_myservices, null);
        squareView = inflate.inflate(R.layout.square_make, null);
        graffitiView = inflate.inflate(R.layout.graffiti_view, null);
        amuseMentView = inflate.inflate(R.layout.amusement, null);

        fab_menu2 = mView.findViewById(R.id.fab_menu2);
        fab_before = mView.findViewById(R.id.fab_before2);
        fab_next = mView.findViewById(R.id.fab_next2);
        fab_close = mView.findViewById(R.id.fab_cancel2);
        fab_rec = mView.findViewById(R.id.fab_rec);
        fab_end = mView.findViewById(R.id.fab_end);
        fab_lock = mView.findViewById(R.id.fab_lock);
        fab_play = mView.findViewById(R.id.fab_play);

        fab_menu3 = squareView.findViewById(R.id.fab_menu3);
        fab_confirm = squareView.findViewById(R.id.fab_confirm);
        fab_color = squareView.findViewById(R.id.fab_color);
        fab_delete = squareView.findViewById(R.id.fab_delete);
        fab_thick = squareView.findViewById(R.id.fab_thick);

        //mView
        constraintLayout = mView.findViewById(R.id.make_layout);
        square_layout = squareView.findViewById(R.id.square_layout);
        gestureDetector = new GestureDetector(this,new MakeService2.SingleTapConfirm());
        gestureDetector2 = new GestureDetector(this,new MakeService2.SingleTapConfirm());
        vto = constraintLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constraintLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = constraintLayout.getMeasuredWidth();
                mWidth = size.x - width;
            }
        });
        vto2 = square_layout.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                square_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = square_layout.getMeasuredWidth();
                mWidth2 = size.x - width;
            }
        });
        setFab_menu2();
        setFab_menu3();
//        bt_end.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DisplayMetrics dm = getResources().getDisplayMetrics();
//                AsyncUpload asyncUpload = new AsyncUpload();
//                asyncUpload.setUserid(1);
//                asyncUpload.setName(name);
//                asyncUpload.setWidth(dm.widthPixels);
//                asyncUpload.setHeight(dm.heightPixels);
//                asyncUpload.execute(educations.toArray(new Education[educations.size()]));
//                try {
//                    Ziper ziper = new Ziper();
//                    ziper.zipFolder(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date), getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+".zip");
//                    AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
//                    String[] ex = {getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+".zip" , "" };
//                    asyncFileUpload.execute(ex);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        bt_rec2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bt_rec.getText().equals("재생")) {
//                    again = 1;
//                    bt_rec2.setVisibility(View.GONE);
//                    bt_rec.setText("종료");
//                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
//                    try {
//                        file = File.createTempFile(simpleDateFormat.format(date)+count,".m4a", getApplicationContext().getCacheDir());
//                        filename = file.getAbsolutePath();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    recorder = new MediaRecorder();
//                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                    recorder.setOutputFile(filename);
//
//                    try {
//                        recorder.prepare();
//                        recorder.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        bt_rec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bt_rec.getText().equals("종료")) {
//                    Toast.makeText(getApplicationContext(),"녹음종료",Toast.LENGTH_SHORT).show();
//                    if (recorder != null) {
//                        recorder.stop();
//                        recorder.release();
//                        recorder = null;
//                        if (again == 0) {
//                            files.add(file);
//                            filenames.add(filename);
//                        }
//                        else {
//                            files.set(count,file);
//                            filenames.set(count,filename);
//                            again = 0;
//                        }
//                        bt_rec.setText("재생");
//                    }
//                    bt_rec2.setVisibility(View.VISIBLE);
//                } else if(bt_rec.getText().equals("녹음")){
//                    bt_rec.setText("종료");
//                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
//                    if (folder_onoff == 1) {
//                        dir = new File(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date));
//                        if (!dir.exists()) {
//                            dir.mkdirs();
//                        }
//                        folder_onoff = 0;
//                    }
//                    try {
//                        file = File.createTempFile(simpleDateFormat.format(date)+count,".m4a", dir);
//                        filename = file.getAbsolutePath();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    recorder = new MediaRecorder();
//                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                    recorder.setOutputFile(filename);
//
//                    try {
//                        recorder.prepare();
//                        recorder.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else if(bt_rec.getText().equals("재생")) {
//                    mediaPlayer = new MediaPlayer();
//                    try {
//                        Toast.makeText(getApplicationContext(),filenames.get(count)+"",Toast.LENGTH_SHORT).show();
//                        mediaPlayer.setDataSource(filenames.get(count));
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    bt_rec2.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        bt_exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(passedIntent);
//            }
//        });
//
//        bt_before.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (count > 0 && count < educations.size()) {
//                    count--;
//                    bt_rec.setText("녹음");
//                    bt_rec2.setVisibility(View.GONE);
//                    if (painter != null) {
//                        wm.removeView(mView);
//                        wm.removeView(painter);
//                        painter = new Painter(getApplicationContext());
//                        painter.setDrawInformation(educations.get(count).getSoundPaint());
//                        wm.addView(painter,params3);
//                        wm.addView(mView,params);
//                        mView.setOnTouchListener(mViewTouchListener);
//                    }
//                    if(files.get(count) != null) {
//                        bt_rec.setText("재생");
//                        bt_rec2.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        bt_rec2.setVisibility(View.INVISIBLE);
//                    }
//                }
//            }
//        });
//
//        bt_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count++;
//                bt_rec.setText("녹음");
//                bt_rec2.setVisibility(View.GONE);
//                if (painter != null && count < educations.size()) {
//                    wm.removeView(painter);
//                    painter = null;
//                    wm.removeView(mView);
//                    painter = new Painter(getApplicationContext());
//                    painter.setDrawInformation(educations.get(count).getSoundPaint());
//                    wm.addView(painter, params3);
//                    wm.addView(mView, params);
//                    mView.setOnTouchListener(mViewTouchListener);
//                    if(files.get(count) != null) {
//                        bt_rec.setText("재생");
//                        bt_rec2.setVisibility(View.VISIBLE);
//                    }
//                }
//                else if(painter != null){
//                    wm.removeView(painter);
//                    painter = null;
//                }
//            }
//        });
//
//        bt_lock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                wm.removeView(mView);
//                if (graffitiView == null) {
//                    addGraffitiviewInit();
//                }
//                if (squareView == null) {
//                    addSquareviewInit();
//                }
//                wm.addView(graffitiView, params2);
//                wm.addView(squareView,params3);
//                squareView.setOnTouchListener(touchListener);
//            }
//        });

        //squareview
//        final Button bt_color =  (Button) squareView.findViewById(R.id.bt_color);
//        final Button bt_thick =  (Button) squareView.findViewById(R.id.bt_thick);
//        final Button bt_confirm =  (Button) squareView.findViewById(R.id.bt_confirm);
//        final Button bt_delete =  (Button) squareView.findViewById(R.id.bt_delete);
//        final Button bt_back = (Button) squareView.findViewById(R.id.bt_back);
//        final LinearLayout ll = (LinearLayout) squareView.findViewById(R.id.layout);
//
//        bt_color.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        bt_thick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        bt_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(graffitiView != null) {
//                    wm.removeView(graffitiView);
//                    graffitiView = null;
//                }
//                if(squareView != null) {
//                    wm.removeView(squareView);
//                    squareView = null;
//                }
//                wm.addView(mView,params);
//            }
//        });
//
//        bt_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(painter != null) {
//                    wm.removeView(painter);
//                    painter = null;
//                }
//            }
//        });
//
//        bt_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(painter != null) {
//                    wm.removeView(painter);
//                    painter = null;
//                }
//                if(graffitiView != null) {
//                    wm.removeView(graffitiView);
//                    graffitiView = null;
//                }
//                if(squareView != null) {
//                    wm.removeView(squareView);
//                    squareView = null;
//                }
//                wm.addView(mView,params);
//            }
//        });


        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        params2.x = size.x;
        params2.y = size.y;
        wm.addView(mView, params);
        showFab();
        squareView.setOnTouchListener(touchListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        name = intent.getStringExtra("name");
        return super.onStartCommand(intent, flags, startId);
    }
    private void clearCache() {
        final File cacheDirFile = getApplicationContext().getCacheDir();
        if (null != cacheDirFile && cacheDirFile.isDirectory()) {
            clearSubCacheFiles(cacheDirFile);
        }
    }
    private void clearSubCacheFiles(File cacheDirFile) {
        if (null == cacheDirFile || cacheDirFile.isFile()) {
            return;
        }
        for (File cacheFile : cacheDirFile.listFiles()) {
            if (cacheFile.isFile()) {
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }
            } else {
                clearSubCacheFiles(cacheFile);
            }
        }
    }

    public void showFab() {
        isFabOpen=true;
        fab_end.animate().translationX(-getResources().getDimension(R.dimen.standard_50));
        fab_rec.animate().translationX(-getResources().getDimension(R.dimen.standard_95));
        fab_close.animate().translationX(-getResources().getDimension(R.dimen.standard_140));
        fab_next.animate().translationX(-getResources().getDimension(R.dimen.standard_185));
        fab_before.animate().translationX(-getResources().getDimension(R.dimen.standard_230));
        fab_lock.animate().translationX(-getResources().getDimension(R.dimen.standard_275));
        fab_play.animate().translationX(-getResources().getDimension(R.dimen.standard_320));
    }

    public void closeFab() {
        isFabOpen=false;
        fab_close.animate().translationX(0);
        fab_before.animate().translationX(0);
        fab_next.animate().translationX(0);
        fab_rec.animate().translationX(0);
        fab_end.animate().translationX(0);
        fab_lock.animate().translationX(0);
        fab_play.animate().translationX(0);
    }

    public void showFab2() {
        isFabOpen2=true;
        fab_confirm.animate().translationX(-getResources().getDimension(R.dimen.standard_50));
        fab_delete.animate().translationX(-getResources().getDimension(R.dimen.standard_95));
        fab_color.animate().translationX(-getResources().getDimension(R.dimen.standard_140));
        fab_thick.animate().translationX(-getResources().getDimension(R.dimen.standard_185));
    }

    public void closeFab2() {
        isFabOpen2=false;
        fab_color.animate().translationX(0);
        fab_confirm.animate().translationX(0);
        fab_delete.animate().translationX(0);
        fab_thick.animate().translationX(0);
    }
    public void setFab_menu2() {
        fab_menu2.setOnTouchListener(new View.OnTouchListener() {
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
                            initialX = params.x;
                            initialY = params.y;

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
                            float nearestXwall = params.x >= middle ? mWidth : 0;
                            params.x = (int) nearestXwall;
                            wm.updateViewLayout(mView, params);

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int xDiff2 = Math.round(event.getRawX() - initialTouchX);
                            int yDiff2 = Math.round(event.getRawY() - initialTouchY);

                            params.x = initialX + xDiff2;
                            params.y = initialY + yDiff2;
                            wm.updateViewLayout(mView, params);
                    }
                }
                return false;
            }
        });
        fab_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
                if (graffitiView == null) {
                    graffitiView = inflate.inflate(R.layout.graffiti_view, null);
                }
                if (squareView == null) {
                    squareView = inflate.inflate(R.layout.square_make, null);
                }
                wm.addView(graffitiView, params2);
                wm.addView(squareView,params3);
                squareView.setOnTouchListener(touchListener);
            }
        });
    }
    public void setFab_menu3() {
        fab_menu3.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector2.onTouchEvent(event)) {
                    if (!isFabOpen2) {
                        showFab2();
                    }
                    else {
                        closeFab2();
                    }
                }else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params3.x;
                            initialY = params3.y;

                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            return true;
                        case MotionEvent.ACTION_UP:
                            float xDiff = event.getRawX() - initialTouchX;
                            float yDiff = event.getRawY() - initialTouchY;
                            if ((Math.abs(xDiff) < 5) && (Math.abs(yDiff) < 5)) {
                                //close the service and remove the fab view
                            }
                            int middle = mWidth2 / 2;
                            float nearestXwall = params3.x >= middle ? mWidth2 : 0;
                            params3.x = (int) nearestXwall;
                            wm.updateViewLayout(squareView, params3);

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int xDiff2 = Math.round(event.getRawX() - initialTouchX);
                            int yDiff2 = Math.round(event.getRawY() - initialTouchY);

                            params3.x = initialX + xDiff2;
                            params3.y = initialY + yDiff2;
                            wm.updateViewLayout(squareView, params3);
                    }
                }
                return false;
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        clearCache();
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
            if(squareView != null) {
                wm.removeView(squareView);
                squareView = null;
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
