package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 中心view
 * Created by y on 2016/10/22.
 */
public class CenterView extends View implements GestureDetector.OnGestureListener {

    private static final String TAG = "CenterView";
    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Paint textPaint3 = new Paint();
    private Paint massagePaint = new Paint();
    private Paint massageBgPaint = new Paint();
    private Paint massageConnectPaint = new Paint();
    private Paint wave1Paint = new Paint();
    private Paint wave2Paint = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private RectF rectF = new RectF();
    private int textPadding = 10;
    private int strokeWidth = 9;
    private String goal = "60";
    private int roundRectRadius = 5;
    private String dateStr = createDateStr(new Date());
    private boolean am = true;
    private GestureDetector detector;
    private RectF oval = new RectF();
    private int dialPadding = 1;
    private int dialStrokeWidth = 1;
    private int dialStrokeWidth2 = 2;
    private Callback callback;
    private List<CenterData> dataList;

    public CenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(context, this);

        float density = getResources().getDisplayMetrics().density;
        textPadding *= density;
        strokeWidth *= density;
        roundRectRadius *= density;
        dialPadding *= density;
        dialStrokeWidth *= density;
        dialStrokeWidth2 *= density;

        paint.setAntiAlias(true);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltqh.ttf");
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint1.setTypeface(font);

        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTypeface(font);

        textPaint3.setAntiAlias(true);
        textPaint3.setColor(0xfff0f0f0);
        textPaint3.setTypeface(font);

        massagePaint.setAntiAlias(true);
        massagePaint.setStyle(Paint.Style.STROKE);
        massagePaint.setStrokeWidth(strokeWidth * 50 / 100);
        massagePaint.setColor(0xffed73ac);
        massagePaint.setStrokeCap(Paint.Cap.ROUND);

        massageBgPaint.setAntiAlias(true);
        massageBgPaint.setStyle(Paint.Style.STROKE);
        massageBgPaint.setStrokeWidth(strokeWidth);
        massageBgPaint.setColor(0xfff8f9ff);
        massageBgPaint.setStrokeCap(Paint.Cap.ROUND);

        massageConnectPaint.setAntiAlias(true);
        massageConnectPaint.setStyle(Paint.Style.STROKE);
        massageConnectPaint.setStrokeWidth(strokeWidth / 5);
        massageConnectPaint.setColor(0xffed73ac);

        wave1Paint.setAntiAlias(true);
        wave1Paint.setColor(0x7fbc54e2);

        wave2Paint.setAntiAlias(true);
        wave2Paint.setColor(0x7fa753f7);
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
        paint.setStrokeCap(Paint.Cap.BUTT);
        radius = radius - strokeWidth;
        paint.setColor(0xff7128bd);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = Math.round(cy + radius / 4);
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        drawStaticWave(canvas, left, right, bottom, top, w, radius, cx, cy);

        final int saveCount = canvas.save();
        textPaint1.setTextSize(w / 4f * 1.3f);
        textPaint1.getTextBounds(goal, 0, goal.length(), boundText1);
        textPaint2.setTextSize(w / 10f * 1.3f);
        textPaint2.getTextBounds("min", 0, "min".length(), boundText2);
        int tw1 = boundText1.left + boundText1.width();
        int tw2 = boundText2.left + boundText2.width();
        int x = (w - tw1 - tw2 - textPadding) / 2;
        int y = cy;
        canvas.drawText(goal, x, y, textPaint1);
        x = x + tw1 + textPadding;
        // canvas.skew((float) Math.tan(Math.toRadians(340)), 0);
        canvas.drawText("min", x, y, textPaint2);
        canvas.restoreToCount(saveCount);

        textPaint2.setTextSize(w / 12);
        textPaint3.setTextSize(w / 16);
        textPaint3.getTextBounds(dateStr, 0, dateStr.length(), boundText1);
        String amStr = am ? "AM" : "PM";
        textPaint2.getTextBounds(amStr, 0, amStr.length(), boundText2);
        x = (w - (boundText1.left + boundText1.width()) - (boundText2.left + boundText2.width()) - textPadding) / 2;
        y = Math.round(cy + radius * 3 / 5);
        canvas.drawText(dateStr, x, y, textPaint3);
        x = x + boundText1.left + boundText1.width() + textPadding;
        canvas.drawText(amStr, x, y, textPaint2);

        int offset = textPadding / 2;
        int roundWidth = textPadding * 2;
        int roundHeight = textPadding / 3;
        rectF.left = cx - roundWidth - offset;
        rectF.top = y + textPadding + offset;
        rectF.right = rectF.left + roundWidth;
        rectF.bottom = rectF.top + roundHeight;
        paint.setStyle(am ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setColor(0xffee7bb1);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);
        rectF.left = cx + offset;
        rectF.right = rectF.left + roundWidth;
        paint.setStyle(am ? Paint.Style.STROKE : Paint.Style.FILL);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(0xfff8f9ff);
        canvas.drawCircle(cx, cy, w / 2 - strokeWidth / 2, paint);

        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, am ? 0 : 12);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        final Calendar calendar = Calendar.getInstance();
        final long time = calendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        final int angle = Math.max(0, (int) time / 120000);
        drawDial(canvas, w, h, angle);
        // drawProgress(canvas, cx, cy, w / 2 - strokeWidth / 2, angle);
        if (dataList != null && !dataList.isEmpty()) {
            drawProgress(canvas, cx, cy, w / 2 - strokeWidth / 2, dataList);
        }

//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(1);
//        paint.setColor(Color.RED);
//        canvas.drawLine(0, cy, getWidth(), cy, paint);
//        canvas.drawLine(cx, 0, cx, getHeight(), paint);
    }

    private void drawDial(Canvas canvas, int w, int h, int angle) {
        paint.setStyle(Paint.Style.FILL);
        int sx = 0;
        int ex = 0;
        int sy = 0;
        for (int i = 0; i <= 360; i++) {
            if (i % 90 == 0) {
                sx = w - strokeWidth + dialPadding;
                ex = w - dialPadding;
                sy = h / 2;
                paint.setStrokeWidth(dialStrokeWidth2);
                paint.setColor(0xffea5b9d);
            } else if (i % 30 == 0) {
                sx = w - strokeWidth + dialPadding;
                ex = w - dialPadding;
                sy = h / 2;
                paint.setStrokeWidth(dialStrokeWidth2);
                paint.setColor(0xfff7bdd8);
            } else if (i % 5 == 0) {
                sx = w - strokeWidth + dialPadding * 2;
                ex = w - dialPadding;
                sy = h / 2;
                paint.setStrokeWidth(dialStrokeWidth);
                paint.setColor(0xfff4d6e6);
            }

            if (i % 5 == 0) {
                final int saveCount = canvas.save();
                canvas.rotate(-90 + i, w / 2, h / 2);
                canvas.drawLine(sx, sy, ex, sy, paint);
                canvas.restoreToCount(saveCount);
            }
        }
    }

    private void drawProgress(Canvas canvas, int cx, int cy, float radius, int angle) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth / 4);
        paint.setColor(0xffed73ac);
        paint.setStrokeCap(Paint.Cap.ROUND);
        oval.left = cx - radius;
        oval.top = cy - radius;
        oval.right = cx + radius;
        oval.bottom = cy + radius;
        canvas.drawArc(oval, 270, angle, false, paint);
    }

    private void drawProgress(Canvas canvas, int cx, int cy, float radius, List<CenterData> dataList) {
        float startAngle = dataList.get(0).startAngle;
        float endAngle = dataList.get(dataList.size() - 1).endAngle;
        canvas.drawArc(oval, 270 + startAngle, endAngle - startAngle, false, massageBgPaint);
        canvas.drawArc(oval, 270 + startAngle, endAngle - startAngle, false, massageConnectPaint);
        for (CenterData data : dataList) {
            oval.left = cx - radius;
            oval.top = cy - radius;
            oval.right = cx + radius;
            oval.bottom = cy + radius;
            Log.i(TAG, "drawProgress " + data.startAngle + " " + data.endAngle);
            canvas.drawArc(oval, 270 + data.startAngle, data.endAngle - data.startAngle, false, massagePaint);
        }
    }

    private String createDateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        return sdf.format(date);
    }

    private void drawStaticWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        int waveProgress = (endX - startX) / 2;
        int sy, ey;
        float period = (float) (2 * Math.PI / ((endX - startX) / 1.2));
        float a = (startY - endY) / 10;
        for (int i = startX; i < endX; i++) {
            sy = (int) Math.round(cy + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));

            ey = (int) Math.round(a * Math.sin(period * (waveProgress + i - startX)) + endY);
            ey = Math.min(sy, ey);
            canvas.drawLine(i, sy, i, ey, wave2Paint);
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
        boolean amTmp = velocityX > 0;
        if (am != amTmp) {
            am = amTmp;
            callback.onAmChange(am);
        }
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    public boolean getAm() {
        return am;
    }

    public interface Callback {
        void onAmChange(boolean am);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setDate(Calendar calendar) {
        am = calendar.get(Calendar.HOUR_OF_DAY) < 12;
        dateStr = createDateStr(calendar.getTime());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setGoal(String target) {
        this.goal = target;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setCenterDataList(List<CenterData> dataList) {
        this.dataList = dataList;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
