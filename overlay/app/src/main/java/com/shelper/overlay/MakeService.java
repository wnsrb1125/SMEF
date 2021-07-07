package com.shelper.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MakeService extends Service {

    private ArrayList<Education> educations = new ArrayList<Education>();
    private ArrayList<File> files = new ArrayList<File>();
    private ArrayList<String> filenames = new ArrayList<String>();
    private String filename;
    private int count = 0;
    LayoutInflater inflate;
    private Integer[] draw;
    private Painter painter;
    private WindowManager wm;
    private View mView;
    private View squareView;
    private View urlView;
    private View uploadView;
    private View secondView;
    private View textView;
    private View captureView;
    private MediaPlayer mediaPlayer;
    private Intent passedIntent;
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
    private String passed_uri;
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
    private FloatingActionButton fab_confirm;
    private FloatingActionButton fab_play;
    private FloatingActionButton fab_url_plus;
    private FloatingActionButton fab_seconds;
    //private FloatingActionButton fab_text;
    private TextView number_text;
    private boolean isFabOpen;
    private boolean isFabOpen2;
    private GestureDetector gestureDetector;
    private GestureDetector gestureDetector2;
    private int mWidth;
    private int mWidth2;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams params3;
    private WindowManager.LayoutParams params4;
    private WindowManager.LayoutParams params5;
    private int rec_mode = 0; // 0: 녹음전 1: 녹음 중 2: 녹음 후
    private int play_mode = 0;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout square_layout;
    private ViewTreeObserver vto;
    private ViewTreeObserver vto2;
    private HashMap<Integer, Boolean> painterMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Boolean> fileMap = new HashMap<Integer, Boolean>();
    private Point size;
    private int biggestValue = 0;
    private boolean confirmChcker = false;
    private int FLAG;
    private int userid;
    private String url = "";
    private String text = "";
    private int seconds = 5;
    private FileOutputStream out;
    private MediaProjectionManager mediaProjectionManager;




    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    fab_confirm.setVisibility(View.INVISIBLE);
                    fab_thick.setVisibility(View.INVISIBLE);
                    fab_color.setVisibility(View.INVISIBLE);
                    fab_menu3.setVisibility(View.INVISIBLE);
                    if(painter != null) {
                        wm.removeView(painter);
                        painter = null;
                        onoff = 1;
                    }
                    painter = new Painter(getApplicationContext());
                    START_X2 = event.getX();
                    START_Y2 = event.getY();

                    draw = new Integer[] {1,1,Integer.valueOf((int)START_X2),Integer.valueOf((int)START_Y2),Integer.valueOf((int)START_X2+1),Integer.valueOf((int)START_Y2+1),seconds};
                    painter.setDrawInformation(draw);
                    wm.addView(painter,params4);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float ingX = event.getX();
                    float ingY = event.getY();
                    draw[2] = (int) START_X2;
                    draw[3] = (int) START_Y2;
                    draw[4] = (int) ingX;
                    draw[5] = (int) ingY;
                    painter.setDrawInformation(draw);
                    painter.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    float finalX = event.getX();
                    float finalY = event.getY();
                    if (START_X2 > finalX || START_Y2 > finalY) {
                        confirmChcker = false;
                    } else {
                        confirmChcker = true;
                    }
                    wm.removeView(squareView);
                    if (squareView == null) {
                        squareView = inflate.inflate(R.layout.square_make, null);
                    }
                    fab_confirm.setVisibility(View.VISIBLE);
                    fab_thick.setVisibility(View.VISIBLE);
                    fab_color.setVisibility(View.VISIBLE);
                    fab_menu3.setVisibility(View.VISIBLE);
                    Education education = new Education();
                    education.setSoundPaint(draw);
                    education.setUrl(url);
                    education.setText(text);
                    Log.d("url",url);
                    if (onoff == 1) {
                        Toast.makeText(getApplicationContext(), count + "", Toast.LENGTH_SHORT).show();
                        educations.set(count, education);
                        onoff = 0;
                    } else {
                        educations.add(education);
                    }
                    wm.addView(squareView, params3);
                    squareView.setOnTouchListener(touchListener);
                    setFab_menu3();
                    showFab2();
                    if (!painterMap.containsKey(count)) {
                        painterMap.put(count, true);
                    }
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
        mediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        now = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        date = new Date(now);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params2 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);

        params3 = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                ,PixelFormat.TRANSLUCENT);

        params4 = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                ,PixelFormat.TRANSLUCENT);

        params5 = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,
                FLAG,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                ,PixelFormat.TRANSLUCENT);

        urlView = inflate.inflate(R.layout.activity_dialog_url, null);
        mView = inflate.inflate(R.layout.view_in_myservices, null);
        uploadView = inflate.inflate(R.layout.view_upload, null);
        squareView = inflate.inflate(R.layout.square_make, null);
        secondView = inflate.inflate(R.layout.view_seconds, null);
        textView = inflate.inflate(R.layout.view_texrt, null);

        fab_menu2 = mView.findViewById(R.id.fab_menu2);
        fab_before = mView.findViewById(R.id.fab_before2);
        fab_next = mView.findViewById(R.id.fab_next2);
        fab_close = mView.findViewById(R.id.fab_cancel2);
        fab_rec = mView.findViewById(R.id.fab_rec);
        fab_end = mView.findViewById(R.id.fab_end);
        fab_lock = mView.findViewById(R.id.fab_lock);
        fab_play = mView.findViewById(R.id.fab_play);
        fab_url_plus = mView.findViewById(R.id.url_plus);
        fab_seconds = mView.findViewById(R.id.fab_seconds);
        //fab_text = mView.findViewById(R.id.fab_text);
        number_text = mView.findViewById(R.id.number_text);

        fab_menu3 = squareView.findViewById(R.id.fab_menu3);
        fab_confirm = squareView.findViewById(R.id.fab_confirm);
        fab_color = squareView.findViewById(R.id.fab_color);
        fab_thick = squareView.findViewById(R.id.fab_thick);


        constraintLayout = mView.findViewById(R.id.make_layout);
        square_layout = squareView.findViewById(R.id.square_layout);
        gestureDetector = new GestureDetector(this,new SingleTapConfirm());
        gestureDetector2 = new GestureDetector(this,new SingleTapConfirm());

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

        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        params2.x = size.x;
        params2.y = size.y;
        wm.addView(mView, params2);
        showFab();
    }

    public void setFab_menu2() {
        fab_menu2 = mView.findViewById(R.id.fab_menu2);
        fab_before = mView.findViewById(R.id.fab_before2);
        fab_next = mView.findViewById(R.id.fab_next2);
        fab_close = mView.findViewById(R.id.fab_cancel2);
        fab_rec = mView.findViewById(R.id.fab_rec);
        fab_end = mView.findViewById(R.id.fab_end);
        fab_lock = mView.findViewById(R.id.fab_lock);
        fab_play = mView.findViewById(R.id.fab_play);
        fab_url_plus = mView.findViewById(R.id.url_plus);
        fab_seconds = mView.findViewById(R.id.fab_seconds);
       // fab_text = mView.findViewById(R.id.fab_text);
        number_text = mView.findViewById(R.id.number_text);

        number_text.setText(count+"");
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
                            initialX = params2.x;
                            initialY = params2.y;

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
                            float nearestXwall = params2.x >= middle ? mWidth : 0;
                            params2.x = (int) nearestXwall;
                            wm.updateViewLayout(mView, params2);

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int xDiff2 = Math.round(event.getRawX() - initialTouchX);
                            int yDiff2 = Math.round(event.getRawY() - initialTouchY);

                            params2.x = initialX + xDiff2;
                            params2.y = initialY + yDiff2;
                            wm.updateViewLayout(mView, params2);
                    }
                }
                return false;
            }
        });
        fab_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0 ) {
                    again = 0;
                    if(fileMap.containsKey(count) && painterMap.containsKey(count)) {
                        Toast.makeText(getApplicationContext(),"뒤로 가기.",Toast.LENGTH_SHORT).show();
                    } else {
                        if (fileMap.containsKey(count)) {
                            fileMap.remove(count);
                        }
                        if (painterMap.containsKey(count)) {
                            painterMap.remove(count);
                        }
                    }
                    seconds = 5;
                    url = "";
                    text = "";
                    count--;
                    fab_play.setVisibility(View.INVISIBLE);
                    Log.d("count/fn",count+"/"+filenames.size());
                    if(count < filenames.size()) {
                        again = 1;
                        fab_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        fab_play.setVisibility(View.VISIBLE);
                    }
                    if(count < educations.size()) {
                        url = educations.get(count).getUrl();
                        text = educations.get(count).getText();
                        seconds = educations.get(count).getSoundPaint()[6];
                        Log.d("seconds",String.valueOf(seconds));
                        if (painter != null) {
                            wm.removeView(mView);
                            wm.removeView(painter);
                            painter = null;
                        }
                        else {
                            wm.removeView(mView);
                            painter = null;
                        }
                        if (educations.get(count).getSoundPaint()[3] != 0 && educations.get(count).getSoundPaint()[4] != 0) {
                            painter = new Painter(getApplicationContext());
                            painter.setDrawInformation(educations.get(count).getSoundPaint());
                            wm.addView(painter, params4);
                        }
                        wm.addView(mView, params2);
                        setFab_menu2();
                    }
                }
                number_text.setText(count+"");
            }
        });
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileMap.containsKey(count)) {
                    if (!painterMap.containsKey(count)) {
                        painterMap.put(count, true);
                        Education education = new Education();
                        education.setSoundPaint(new Integer[]{1,1,0,0,0,0,seconds});
                        education.setUrl(url);
                        education.setText(text);
                        educations.add(education);
                    } else {
                        Education education = educations.get(count);
                        education.setUrl(url);
                        education.setText(text);
                        educations.set(count,education);
                    }
                    url = "";
                    text = "";
                    seconds = 5;
                    again = 0;
                    count++;
                    if (biggestValue < count) {
                        biggestValue = count;
                    }
                    fab_play.setVisibility(View.INVISIBLE);
                    //Log.d("errorfind", count + "/" + educations.size() + "");
                    Log.d("count/fn",count+"/"+filenames.size());
                    if(count < filenames.size()) {
                        again = 1;
                        fab_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        fab_play.setVisibility(View.VISIBLE);
                    }
                    if (count < educations.size()) {
                        if(painter != null) {
                            wm.removeView(painter);
                        }
                        painter = null;
                        wm.removeView(mView);
                        if(educations.get(count).getSoundPaint()[3] != 0 && educations.get(count).getSoundPaint()[4] != 0) {
                            painter = new Painter(getApplicationContext());
                            painter.setDrawInformation(educations.get(count).getSoundPaint());
                            wm.addView(painter, params4);
                        }
                        wm.addView(mView, params2);
                        url = educations.get(count).getUrl();
                        text = educations.get(count).getText();
                        seconds = educations.get(count).getSoundPaint()[6];
                        Log.d("seconds",String.valueOf(seconds));
                    } else if (painter != null) {
                        wm.removeView(painter);
                        painter = null;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"값이 없습니다.",Toast.LENGTH_SHORT).show();
                }
                number_text.setText(count+"");
            }

        });
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(passedIntent);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("myservice",1);
                startActivity(intent);
            }
        });
        fab_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rec_mode == 1) {
                    Toast.makeText(getApplicationContext(),"녹음종료",Toast.LENGTH_SHORT).show();
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        if (again == 0) {
                            filenames.add(filename);
                            again = 1;
                            Log.d("filename0",filenames.get(count));
                        }
                        else {
                            Log.d("filename1",filenames.get(count));
                            File file = new File(filenames.get(count));
                            filenames.set(count,filename);
                        }
                        if (!fileMap.containsKey(count)) {
                            fileMap.put(count,true);
                        }
                        rec_mode = 0;
                        fab_rec.setImageResource(R.drawable.ic_mic_black_24dp);
                        fab_play.setVisibility(View.VISIBLE);
                    }
                } else if(rec_mode == 0){
                    fab_rec.setImageResource(R.drawable.ic_stop_black_24dp);
                    fab_play.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"녹음시작",Toast.LENGTH_SHORT).show();
                    if (folder_onoff == 1) {
                        dir = new File(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date));
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        folder_onoff = 0;
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+ "/"+count+".mp4");
                    filename = getApplicationContext().getCacheDir()+"/"+simpleDateFormat.format(date)+ "/"+count+".mp4";

                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rec_mode = 1;
                }
            }
        });
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play_mode == 0) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        Toast.makeText(getApplicationContext(),filenames.get(count)+"",Toast.LENGTH_SHORT).show();
                        mediaPlayer.setDataSource(filenames.get(count));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        fab_play.setImageResource(R.drawable.ic_stop_black_24dp);
                        play_mode = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(play_mode == 1) {
                    mediaPlayer.stop();
                    play_mode = 0;
                    fab_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });
        fab_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.addView(uploadView,params5);
                Button confirm_button = uploadView.findViewById(R.id.end_up_button);
                Button cancel_button = uploadView.findViewById(R.id.end_cancel_button);
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fileMap.containsKey(biggestValue)) {
                            if(!painterMap.containsKey(biggestValue)) {
                                painterMap.put(biggestValue, true);
                                Education education = new Education();
                                education.setSoundPaint(new Integer[]{1,1,0,0,0,0,seconds});
                                education.setUrl(url);
                                education.setText(text);
                                educations.add(education);
                            }
                            fab_end.setVisibility(View.INVISIBLE);
                            DisplayMetrics dm = getResources().getDisplayMetrics();
                            try {
                                Ziper ziper = new Ziper();
                                ziper.zipFolder(getApplicationContext().getCacheDir() + "/" + simpleDateFormat.format(date), getApplicationContext().getCacheDir() + "/" + simpleDateFormat.format(date) + ".zip");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            AsyncUpload asyncUpload = new AsyncUpload();
                            asyncUpload.setUserid(userid);
                            asyncUpload.setName(name);
                            asyncUpload.setWidth(dm.widthPixels);
                            asyncUpload.setHeight(dm.heightPixels);
                            try {
                                asyncUpload.execute(educations.toArray(new Education[educations.size()])).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            try {
                                AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
                                asyncFileUpload.setUserid(userid);
                                String[] ex = {getApplicationContext().getCacheDir() + "/" + simpleDateFormat.format(date) + ".zip", passed_uri};
                                Boolean TF = asyncFileUpload.execute(ex).get();
                                if(TF) {
                                    stopService(passedIntent);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"완성하시오",Toast.LENGTH_SHORT).show();
                        }
                        wm.removeView(uploadView);
                    }
                });
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wm.removeView(uploadView);
                    }
                });
            }
        });
        fab_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
                mView = null;
                if (squareView == null) {
                    squareView = inflate.inflate(R.layout.square_make, null);
                }
                wm.addView(squareView,params3);
                squareView.setOnTouchListener(touchListener);
                setFab_menu3();
                showFab2();
            }
        });
        fab_url_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.addView(urlView,params5);
                Button confirm_button = urlView.findViewById(R.id.url_button);
                Button cancel_button = urlView.findViewById(R.id.url_cancel_button);
                final EditText url_text = urlView.findViewById(R.id.url_text);
                url_text.setText(url);
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        url = url_text.getText().toString();
                        wm.removeView(urlView);
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wm.removeView(urlView);
                    }
                });
            }
        });
//        fab_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                wm.addView(textView,params5);
//                Button confirm_button = textView.findViewById(R.id.text_button);
//                Button cancel_button = textView.findViewById(R.id.text_cancel_button);
//                final EditText text_text = textView.findViewById(R.id.text_text);
//                text_text.setText(text);
//                confirm_button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        text = text_text.getText().toString();
//                        wm.removeView(textView);
//                    }
//                });
//
//                cancel_button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        wm.removeView(textView);
//                    }
//                });
//            }
//        });
        fab_seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.addView(secondView,params5);
                Button confirm_button = secondView.findViewById(R.id.second_button);
                Button cancel_button = secondView.findViewById(R.id.second_cancel_button);
                final EditText second_text = secondView.findViewById(R.id.second_text);
                second_text.setText(String.valueOf(seconds));
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seconds = Integer.parseInt(String.valueOf(second_text.getText()));
                        if (painter != null) {
                            Education old = educations.get(count);
                            old.getSoundPaint()[6] = seconds;
                            educations.set(count,old);
                        }
                        wm.removeView(secondView);
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wm.removeView(secondView);
                    }
                });
            }
        });
    }
    public void setFab_menu3() {
        fab_menu3 = squareView.findViewById(R.id.fab_menu3);
        fab_confirm = squareView.findViewById(R.id.fab_confirm);
        fab_color = squareView.findViewById(R.id.fab_color);
        fab_thick = squareView.findViewById(R.id.fab_thick);

        fab_menu3.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch",v.toString());
                if (gestureDetector2.onTouchEvent(event)) {
                    if (!isFabOpen2) {
                        showFab2();
                    } else {
                        closeFab2();
                    }
                } else {
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
        fab_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmChcker) {
                    if (squareView != null) {
                        wm.removeView(squareView);
                        squareView = null;
                    }
                    mView = inflate.inflate(R.layout.view_in_myservices, null);
                    wm.addView(mView, params2);
                    setFab_menu2();
                    showFab();
                }else {
                    Toast.makeText(getApplicationContext(),"사각형을 제대로 그리세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void onCap(Bitmap bm) throws Exception {
        String imgFile = "save.jpg"; // 저장파일명
        StringBuffer imgPath = new StringBuffer("data/data/com.shelper.overlay/"); // 저장경로
        File saveFile = new File(imgPath.toString());
        try {
            if(!saveFile.isDirectory()) {
                saveFile.mkdirs();
            }
            imgPath.append(imgFile);
            out = new FileOutputStream(imgPath.toString()); // 저장경로 + 파일명
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } catch (Exception e) {
        } finally {
            if(out != null) {
                out.close();
            }
            saveFile = null;
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
        fab_url_plus.animate().translationY(-getResources().getDimension(R.dimen.standard_50));
        fab_seconds.animate().translationY(-getResources().getDimension(R.dimen.standard_95));
        //fab_text.animate().translationY(-getResources().getDimension(R.dimen.standard_140));
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
        fab_url_plus.animate().translationY(0);
        fab_seconds.animate().translationY(0);
        //fab_text.animate().translationY(0);
    }

    public void showFab2() {
        isFabOpen2=true;
        fab_confirm.animate().translationX(-getResources().getDimension(R.dimen.standard_50));
        fab_color.animate().translationX(-getResources().getDimension(R.dimen.standard_95));
        fab_thick.animate().translationX(-getResources().getDimension(R.dimen.standard_140));
    }

    public void closeFab2() {
        isFabOpen2=false;
        fab_color.animate().translationX(0);
        fab_confirm.animate().translationX(0);
        fab_thick.animate().translationX(0);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        name = intent.getStringExtra("name");
        userid = intent.getIntExtra("userid",0);
        passed_uri = intent.getStringExtra("uriuri");
        Log.d("uri2",passed_uri);
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
            if(squareView != null) {
//                Log.d("squds",squareView.toString());
//                wm.removeView(squareView);
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
