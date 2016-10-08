package com.morrigan.m.goal;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 刻度尺View
 * Created by y on 2016/10/6.
 */
public class RulerView extends View implements GestureDetector.OnGestureListener {

    private static final String TAG = "RulerView";
    private GestureDetector detector;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private Rect boundText = new Rect();
    private Path path = new Path();

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(context, this);
        paint = new Paint();
        paint.setColor(0xff999999);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);

        paint2 = new Paint();
        paint2.setColor(0xff666666);
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setTextSize(48);
        paint2.setStrokeWidth(3);

        paint3 = new Paint();
        paint3.setColor(0xff7d1db6);
        paint3.setAntiAlias(true);
        paint3.setTextAlign(Paint.Align.CENTER);
        paint3.setTextSize(256);
        paint3.setStrokeWidth(3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        final int sx = getScrollX();
        Log.i(TAG, String.format("drawIndicator %d/%d/%d", w, h, sx));
        for (int i = sx; i <= sx + w; i++) {
            if (i % 150 == 0) {
                canvas.drawLine(i, h - 100, i, h - 220, paint2);
                String text = String.valueOf(i / 30);
                canvas.drawText(text, i, h - 30, paint2);
            } else if (i % 30 == 0) {
                canvas.drawLine(i, h - 100, i, h - 190, paint);
            }
        }

        canvas.drawLine(sx + w / 2, h - 100, sx + w / 2, h - 220, paint3);
        path.reset();
        path.moveTo(sx + w / 2, h - 90);
        path.lineTo(sx + w / 2 - 10, h - 80);
        path.lineTo(sx + w / 2 + 10, h - 80);
        path.close();
        canvas.drawPath(path, paint3);

        String value = "0";
        canvas.drawText(value, sx + w / 2, h - 220 - 30, paint3);
        paint3.getTextBounds(value, 0, value.length(), boundText);
        canvas.drawText("分", sx + (w / 2 + boundText.width() + 10), h - 220 - 30, paint2);

        int top = h - 220 - 30 - boundText.height() - 30;
        path.reset();
        path.moveTo(sx + w / 2, top);
        path.lineTo(sx + w / 2 - 20, top - 20);
        path.lineTo(sx + w / 2 + 20, top - 20);
        path.close();
        canvas.drawPath(path, paint3);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollBy(Math.round(distanceX), 0);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
