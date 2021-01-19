package com.example.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class Painter extends View {

    private Integer[] paint;
    private LinearLayout.LayoutParams lp;

    public Painter(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStrokeWidth(20f);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);

        Rect rect = new Rect(this.paint[2],this.paint[3],this.paint[4],this.paint[5]);
        canvas.drawRect(rect,paint);

    }

    public void setDrawInformation(Integer SoundPaint[]) {
       this.paint = SoundPaint;
       //this.ww = b;
    }

    public Painter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Painter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
