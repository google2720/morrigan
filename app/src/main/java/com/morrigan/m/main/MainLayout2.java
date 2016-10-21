package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.morrigan.m.R;

/**
 * 主界面
 * Created by y on 2016/10/20.
 */
public class MainLayout2 extends FrameLayout {

    private Paint paint = new Paint();
    private Rect rect = new Rect();
    private Drawable drawable;
    private BatteryView batteryView;

    public MainLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        drawable = getResources().getDrawable(R.drawable.main_center_bg);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        batteryView = (BatteryView) findViewById(R.id.battery);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        final int cx = getPaddingLeft() + w / 2;
        final int cy = getPaddingTop() + h / 2;
//
//        float radius = 120;
//        float x = getPaddingLeft() + w - radius - 40;
//        float y = getPaddingTop() + radius + 40;
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0xff9438ca);
//        canvas.drawCircle(x, y, radius + 40, paint);
//
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(30);
//        paint.setColor(Color.WHITE);
//        canvas.drawCircle(x, y, radius, paint);
//
//        paint.setStrokeWidth(10);
//        paint.setColor(0xffc5e3c9);
//        canvas.drawCircle(x, y, radius, paint);
//
//        radius = w / 2 * 3 / 5;
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0xff7128bd);
//        canvas.drawCircle(cx, cy, radius, paint);

        // canvas.drawBitmap(bitmap, cx - radius, cy - radius, paint);
//        rect.left = Math.round(cx - radius - 80);
//        rect.top = Math.round(cy - radius - 80);
//        rect.right = Math.round(cx + radius + 80);
//        rect.bottom = Math.round(cy + radius + 80);
//        drawable.setBounds(rect);
//        // drawable.draw(canvas);
//
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(30);
//        paint.setColor(Color.WHITE);
//        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x7fff0000);
        canvas.drawLine(getPaddingLeft(), cy, w, cy, paint);
        canvas.drawLine(cx, getPaddingTop(), cx, h, paint);
    }
}
