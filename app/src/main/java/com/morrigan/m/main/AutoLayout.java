package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.morrigan.m.R;

/**
 * 自动界面
 * Created by y on 2016/10/18.
 */
public class AutoLayout extends FrameLayout {

    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private int strokeWidth = 5;
    private View startView;
    private View massage1View;
    private View massage2View;
    private View massage3View;
    private View massage4View;
    private View massage5View;

    public AutoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        strokeWidth *= density;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        startView = findViewById(R.id.start);
        massage1View = findViewById(R.id.massage1);
        massage2View = findViewById(R.id.massage2);
        massage3View = findViewById(R.id.massage3);
        massage4View = findViewById(R.id.massage4);
        massage5View = findViewById(R.id.massage5);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        final float radius = generateRadius(w, h);
        final int cx = generateCenterX(w, h);
        final int cy = generateCenterY(w, h);

        int vw = startView.getMeasuredWidth();
        int vh = startView.getMeasuredHeight();
        int l = cx - vw / 2;
        int t = Math.round(getHeight() - getPaddingBottom() - h / 5f - vh / 2);
        int r = l + vw;
        int b = t + vh;
        startView.layout(l, t, r, b);

        /*
        圆点坐标：(x0,y0)
        半径：r
        角度：a0
        则圆上任一点为：（x1,y1）
        x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
        y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
        */
        vw = massage1View.getMeasuredWidth();
        vh = massage1View.getMeasuredHeight();
        int x = cx + (int) Math.round(radius * Math.cos(160 * Math.PI / 180));
        int y = cy + (int) Math.round(radius * Math.sin(160 * Math.PI / 180));
        l = x - vw / 2 + strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage1View.layout(l, t, r, b);

        vw = massage2View.getMeasuredWidth();
        vh = massage2View.getMeasuredHeight();
        x = cx + (int) Math.round(radius * Math.cos(210 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(210 * Math.PI / 180));
        l = x - vw / 2 + strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage2View.layout(l, t, r, b);

        vw = massage3View.getMeasuredWidth();
        vh = massage3View.getMeasuredHeight();
        l = cx - vw / 2;
        t = Math.round(cy - radius) - vh / 2;
        r = l + vw;
        b = t + vh;
        massage3View.layout(l, t, r, b);

        vw = massage4View.getMeasuredWidth();
        vh = massage4View.getMeasuredHeight();
        x = cx + (int) Math.round(radius * Math.cos(330 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(330 * Math.PI / 180));
        l = x - vw / 2 - strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage4View.layout(l, t, r, b);

        vw = massage5View.getMeasuredWidth();
        vh = massage5View.getMeasuredHeight();
        x = cx + (int) Math.round(radius * Math.cos(20 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(20 * Math.PI / 180));
        l = x - vw / 2 - strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage5View.layout(l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        final float radius = generateRadius(w, h);
        final int cx = generateCenterX(w, h);
        final int cy = generateCenterY(w, h);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff7128bd);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        rectF.left = getPaddingLeft() - strokeWidth / 2;
        rectF.top = getHeight() - getPaddingBottom() - h / 5f;
        rectF.right = getWidth() - getPaddingRight() + strokeWidth / 2;
        rectF.bottom = getHeight() - getPaddingBottom() + h / 5f + strokeWidth / 2;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffdcbafa);
        canvas.drawArc(rectF, 180, 180, false, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawArc(rectF, 180, 180, false, paint);

        super.dispatchDraw(canvas);
    }

    private float generateRadius(int w, int h) {
        if (h <= w) {
            return (h - massage3View.getHeight() / 2f) * 50 / 100;
        } else {
            return w * 40 / 100f;
        }
    }

    private int generateCenterX(int w, int h) {
        return w / 2;
    }

    private int generateCenterY(int w, int h) {
        if (h <= w) {
            return (h - massage3View.getHeight() / 2) / 2 + massage3View.getHeight() / 2;
        } else {
            return h / 2;
        }
    }
}
