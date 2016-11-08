package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.morrigan.m.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 中心view
 * Created by y on 2016/10/22.
 */
public class CenterView extends View implements GestureDetector.OnGestureListener {

    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Paint textPaint3 = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private RectF rectF = new RectF();
    private int textPadding = 10;
    private int strokeWidth = 9;
    private String goal = "60";
    private int roundRectRadius = 5;
    private Rect bounds = new Rect();
    private Drawable drawable;
    private Date date = new Date();
    private String dateStr = createDateStr(date);
    private boolean am = true;
    private GestureDetector detector;

    public CenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(context, this);
        float density = getResources().getDisplayMetrics().density;
        textPadding *= density;
        strokeWidth *= density;
        roundRectRadius *= density;
        paint.setAntiAlias(true);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        textPaint3.setAntiAlias(true);
        textPaint3.setColor(0xfff0f0f0);
        drawable = ContextCompat.getDrawable(context, R.drawable.dial1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        float radius = w / 2;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xfff8f9ff);
        canvas.drawCircle(cx, cy, radius, paint);

        radius = radius - strokeWidth;
        paint.setColor(0xff7128bd);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = Math.round(cy + radius / 4);
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        drawStaticWave(canvas, left, right, bottom, top, w, radius, cx, cy);

        textPaint1.setTextSize(w / 4);
        textPaint1.getTextBounds(goal, 0, goal.length(), boundText1);
        textPaint2.setTextSize(w / 10);
        textPaint2.getTextBounds("min", 0, "min".length(), boundText2);
        int x = (w - boundText1.width() - boundText2.width() + textPadding) / 2;
        int y = cy;
        canvas.drawText(goal, x, y, textPaint1);
        x = x + boundText1.width() + textPadding;
        canvas.drawText("min", x, y, textPaint2);

        textPaint2.setTextSize(w / 12);
        textPaint3.setTextSize(w / 14);
        textPaint3.getTextBounds(dateStr, 0, dateStr.length(), boundText1);
        String amStr = am ? "AM" : "PM";
        textPaint2.getTextBounds(amStr, 0, amStr.length(), boundText2);
        x = (w - boundText1.width() - boundText2.width() - textPadding) / 2;
        y = Math.round(cy + radius * 3 / 5);
        canvas.drawText(dateStr, x, y, textPaint3);
        x = x + boundText1.width() + textPadding;
        canvas.drawText(amStr, x, y, textPaint2);

        rectF.left = cx - textPadding - textPadding / 2;
        rectF.top = y + textPadding + textPadding / 2;
        rectF.right = rectF.left + textPadding;
        rectF.bottom = rectF.top + textPadding / 3;
        paint.setStyle(am ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setColor(0xffee7bb1);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);

        rectF.left = cx + textPadding / 2;
        rectF.right = rectF.left + textPadding;
        paint.setStyle(am ? Paint.Style.STROKE : Paint.Style.FILL);
        paint.setColor(0xffee7bb1);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);

        bounds.left = 0;
        bounds.top = 0;
        bounds.right = w;
        bounds.bottom = h;
        drawable.setBounds(bounds);
        drawable.draw(canvas);
    }

    private String createDateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        return sdf.format(date);
    }

    private void drawStaticWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        long waveProgress = w / 2;
        int sy, ey;
        float period = (float) (2 * Math.PI / w);
        float a = (startY - endY) / 10;
        for (int i = startX; i < endX; i++) {
            sy = (int) Math.round(cy + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));
            ey = (int) Math.round(a * Math.sin(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9147dd);
            canvas.drawLine(i, sy, i, ey, paint);

            ey = (int) Math.round(a * Math.cos(period * (waveProgress + i)) + endY);
            ey = Math.min(sy, ey);
            paint.setColor(0x7f9c47d3);
            canvas.drawLine(i, sy, i, ey, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        am = velocityX > 0;
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    public void setDate(Date date) {
        this.date = date;
        this.dateStr = createDateStr(date);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setGoal(String target) {
        this.goal = target;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
