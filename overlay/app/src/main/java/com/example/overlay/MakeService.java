package com.example.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MakeService extends Service {

    private int educations_length;
    private ArrayList<Education> educations = new ArrayList<Education>();
    private int count = 0;
    private int zero = 0;
    private Integer[] a = {11,65,180,75,190,200};
    private Painter painter;
    private Education education;
    private WindowManager wm;
    private View mView;
    private View squareView;
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
            int x = (int)event.getX();
            int y = (int)event.getY();

            if(painter != null) {
                wm.removeView(painter);
                painter = null;
            }

            painter = new Painter(getApplicationContext());
            painter.setDrawInformation(new Integer[] {1,1,10,10,x,y});


            wm.removeView(squareView);
            wm.addView(painter,params3);
            wm.addView(squareView,params3);

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
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200,
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

        //squareView
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
            }
        });


        //mView
        final Button bt_exit =  (Button) mView.findViewById(R.id.bt_exit);
        final Button bt_before =  (Button) mView.findViewById(R.id.bt_before);
        final Button bt_next =  (Button) mView.findViewById(R.id.bt_next);
        final Button bt_current =  (Button) mView.findViewById(R.id.bt_current);
        final Button bt_lock = (Button) mView.findViewById(R.id.bt_lock);
        final ToggleButton bt_rec = (ToggleButton) mView.findViewById(R.id.bt_rec);


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

        bt_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == educations_length) {
                    count--;
                }
                count++;
            }
        });

        bt_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    media_count--;
                }
            }
        });
        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
                wm.addView(graffitiView, params2);
                wm.addView(squareView,params3);
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < educations_length) {
                    count++;
                    media_count++;
                }
            }
        });

        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
        wm.addView(mView, params);
        mView.setOnTouchListener(mViewTouchListener);
        squareView.setOnTouchListener(touchListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        passedIntent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }

            wm = null;
        }
    }
}
