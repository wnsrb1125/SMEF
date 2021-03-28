package com.example.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MakeService extends Service {

    private int educations_length;
    private ArrayList<Education> educations = new ArrayList<Education>();
    private ArrayList<File> files = new ArrayList<File>();
    private ArrayList<String> filenames = new ArrayList<String>();
    private String filename;
    private int count = 0;
    private int zero = 0;
    LayoutInflater inflate;
    private Integer[] a = {11,65,180,75,190,200};
    private Integer[] draw;
    private Painter painter;
    private Education education;
    private WindowManager wm;
    private View mView;
    private View squareView;
    private View graffitiView;
    private MediaPlayer mediaPlayer;
    private Intent passedIntent;
    private float START_X;                    //터치 시작 점
    private float START_Y;                    //터치 시작 점
    float START_X2 = 0;
    float START_Y2 = 0;
    private int PREV_X;                            //뷰의 시작 점
    private int PREV_Y;                            //뷰의 시작 점
    private int MAX_X = -1, MAX_Y = -1;
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
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams params3;


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
                    wm.addView(painter,params3);
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
                    if (squareView == null) {
                        addSquareviewInit();
                    }

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
                    break;
            }
            return true;
        }
    };

    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(matrix);

        MAX_X = matrix.widthPixels - mView.getWidth();
        MAX_Y = matrix.heightPixels - mView.getHeight();
    }

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

        params = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                , PixelFormat.RGB_888);


        params2 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);

        params3 = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.CENTER | Gravity.TOP;

        mView = inflate.inflate(R.layout.view_in_make, null);
        squareView = inflate.inflate(R.layout.square_make, null);
        graffitiView = inflate.inflate(R.layout.graffiti_view, null);

        //mView
        final Button bt_exit =  (Button) mView.findViewById(R.id.bt_exit);
        final Button bt_before =  (Button) mView.findViewById(R.id.bt_before);
        final Button bt_next =  (Button) mView.findViewById(R.id.bt_next);
        final Button bt_lock = (Button) mView.findViewById(R.id.bt_lock);
        final Button bt_end = (Button) mView.findViewById(R.id.bt_end);
        final Button bt_rec = (Button) mView.findViewById(R.id.bt_rec);
        final Button bt_rec2 = (Button) mView.findViewById(R.id.bt_rec2);

        bt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics dm = getResources().getDisplayMetrics();
                AsyncUpload asyncUpload = new AsyncUpload();
                asyncUpload.setUserid(1);
                asyncUpload.setName(name);
                asyncUpload.setWidth(dm.widthPixels);
                asyncUpload.setHeight(dm.heightPixels);
                asyncUpload.execute(educations.toArray(new Education[educations.size()]));
                try {
                    Ziper ziper = new Ziper();
                    ziper.zipFolder(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date), getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+".zip");
                    AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
                    String[] ex = {getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+".zip" , "" };
                    asyncFileUpload.execute(ex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_rec2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_rec.getText().equals("재생")) {
                    again = 1;
                    bt_rec2.setVisibility(View.GONE);
                    bt_rec.setText("종료");
                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
                    try {
                        file = File.createTempFile(simpleDateFormat.format(date)+count,".m4a", getApplicationContext().getCacheDir());
                        filename = file.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(filename);

                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bt_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_rec.getText().equals("종료")) {
                    Toast.makeText(getApplicationContext(),"녹음종료",Toast.LENGTH_SHORT).show();
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        if (again == 0) {
                            files.add(file);
                            filenames.add(filename);
                        }
                        else {
                            files.set(count,file);
                            filenames.set(count,filename);
                            again = 0;
                        }
                        bt_rec.setText("재생");
                    }
                    bt_rec2.setVisibility(View.VISIBLE);
                } else if(bt_rec.getText().equals("녹음")){
                    bt_rec.setText("종료");
                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
                    if (folder_onoff == 1) {
                        dir = new File(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date));
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        folder_onoff = 0;
                    }
                    try {
                        file = File.createTempFile(simpleDateFormat.format(date)+count,".m4a", dir);
                        filename = file.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(filename);

                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if(bt_rec.getText().equals("재생")) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        Toast.makeText(getApplicationContext(),filenames.get(count)+"",Toast.LENGTH_SHORT).show();
                        mediaPlayer.setDataSource(filenames.get(count));
                        mediaPlayer.prepare();
                        mediaPlayer.start();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bt_rec2.setVisibility(View.VISIBLE);
                }
            }
        });

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(passedIntent);
            }
        });

        bt_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0 && count < educations.size()) {
                    count--;
                    bt_rec.setText("녹음");
                    bt_rec2.setVisibility(View.GONE);
                    if (painter != null) {
                        wm.removeView(mView);
                        wm.removeView(painter);
                        painter = new Painter(getApplicationContext());
                        painter.setDrawInformation(educations.get(count).getSoundPaint());
                        wm.addView(painter,params3);
                        wm.addView(mView,params);
                        mView.setOnTouchListener(mViewTouchListener);
                    }
                    if(files.get(count) != null) {
                        bt_rec.setText("재생");
                        bt_rec2.setVisibility(View.VISIBLE);
                    }
                    else {
                        bt_rec2.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                bt_rec.setText("녹음");
                bt_rec2.setVisibility(View.GONE);
                if (painter != null && count < educations.size()) {
                    wm.removeView(painter);
                    painter = null;
                    wm.removeView(mView);
                    painter = new Painter(getApplicationContext());
                    painter.setDrawInformation(educations.get(count).getSoundPaint());
                    wm.addView(painter, params3);
                    wm.addView(mView, params);
                    mView.setOnTouchListener(mViewTouchListener);
                    if(files.get(count) != null) {
                        bt_rec.setText("재생");
                        bt_rec2.setVisibility(View.VISIBLE);
                    }
                }
                else if(painter != null){
                    wm.removeView(painter);
                    painter = null;
                }
            }
        });

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
                if (graffitiView == null) {
                    addGraffitiviewInit();
                }
                if (squareView == null) {
                    addSquareviewInit();
                }
                wm.addView(graffitiView, params2);
                wm.addView(squareView,params3);
                squareView.setOnTouchListener(touchListener);
            }
        });

        //squareview
        final Button bt_color =  (Button) squareView.findViewById(R.id.bt_color);
        final Button bt_thick =  (Button) squareView.findViewById(R.id.bt_thick);
        final Button bt_confirm =  (Button) squareView.findViewById(R.id.bt_confirm);
        final Button bt_delete =  (Button) squareView.findViewById(R.id.bt_delete);
        final Button bt_back = (Button) squareView.findViewById(R.id.bt_back);
        final LinearLayout ll = (LinearLayout) squareView.findViewById(R.id.layout);

        bt_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_thick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(graffitiView != null) {
                    wm.removeView(graffitiView);
                    graffitiView = null;
                }
                if(squareView != null) {
                    wm.removeView(squareView);
                    squareView = null;
                }
                wm.addView(mView,params);
                mView.setOnTouchListener(mViewTouchListener);
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(painter != null) {
                    wm.removeView(painter);
                    painter = null;
                }
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                wm.addView(mView,params);
                mView.setOnTouchListener(mViewTouchListener);
            }
        });


        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        wm.addView(mView, params);
        mView.setOnTouchListener(mViewTouchListener);
        squareView.setOnTouchListener(touchListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        name = intent.getStringExtra("name");
        return super.onStartCommand(intent, flags, startId);
    }

    public void addmViewInit() {
        mView = inflate.inflate(R.layout.view_in_make, null);

        //mView
        final Button bt_exit =  (Button) mView.findViewById(R.id.bt_exit);
        final Button bt_before =  (Button) mView.findViewById(R.id.bt_before);
        final Button bt_next =  (Button) mView.findViewById(R.id.bt_next);
        final Button bt_lock = (Button) mView.findViewById(R.id.bt_lock);
        final Button bt_rec = (Button) mView.findViewById(R.id.bt_rec);


        bt_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bt_rec.getText().equals("녹음")) {
                    Toast.makeText(getApplicationContext(),"녹음종료",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(passedIntent);
            }
        });

        bt_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    try {
                        if (painter != null) {
                            wm.removeView(mView);
                            wm.removeView(painter);
                            painter = new Painter(getApplicationContext());
                            painter.setDrawInformation(educations.get(count).getSoundPaint());
                            wm.addView(painter,params3);
                            wm.addView(mView,params);
                            mView.setOnTouchListener(mViewTouchListener);
                        }

                    }catch (Exception E) {
                        Log.d("",E.toString());
                    }

                }
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (painter != null) {
                    wm.removeView(painter);
                    painter = null;
                }
                count++;

            }
        });

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
                if (graffitiView == null) {
                    addGraffitiviewInit();
                }
                if (squareView == null) {
                    addSquareviewInit();
                }
                wm.addView(graffitiView, params2);
                wm.addView(squareView,params3);
                squareView.setOnTouchListener(touchListener);
            }
        });
    }
    public void addGraffitiviewInit() {
        graffitiView = inflate.inflate(R.layout.graffiti_view, null);

    }
    public void addSquareviewInit() {

        squareView = inflate.inflate(R.layout.square_make, null);

        final Button bt_color =  (Button) squareView.findViewById(R.id.bt_color);
        final Button bt_thick =  (Button) squareView.findViewById(R.id.bt_thick);
        final Button bt_confirm =  (Button) squareView.findViewById(R.id.bt_confirm);
        final Button bt_delete =  (Button) squareView.findViewById(R.id.bt_delete);
        final Button bt_back = (Button) squareView.findViewById(R.id.bt_back);
        final LinearLayout ll = (LinearLayout) squareView.findViewById(R.id.layout);

        bt_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_thick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(graffitiView != null) {
                    wm.removeView(graffitiView);
                    graffitiView = null;
                }
                if(squareView != null) {
                    wm.removeView(squareView);
                    squareView = null;
                }
                wm.addView(mView,params);
                mView.setOnTouchListener(mViewTouchListener);
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(painter != null) {
                    wm.removeView(painter);
                    painter = null;
                }
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                wm.addView(mView,params);
                mView.setOnTouchListener(mViewTouchListener);
            }
        });
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
}
