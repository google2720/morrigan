package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.morrigan.m.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动界面
 * Created by y on 2016/10/18.
 */
public class AutoLayout extends FrameLayout {

    private Paint paint = new Paint();
    private Paint textPaint = new Paint();
    private int strokeWidth = 5;
    private View startView;
    private AutoItemView massage1View;
    private AutoItemView massage2View;
    private AutoItemView massage3View;
    private AutoItemView massage4View;
    private AutoItemView massage5View;
    private boolean start;
    private int bgColor = 0xff7128bd;
    private String startTip;
    private Rect boundText = new Rect();
    private Drawable drawableY;
    private Rect rect = new Rect();
    private long startSystemTime;
    private long stopSystemTime;
    private Drawable drawable;

    public AutoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        strokeWidth *= density;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        startTip = getResources().getString(R.string.massage_ready);
        drawableY = ContextCompat.getDrawable(context, R.drawable.auto_y);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        startView = findViewById(R.id.start);
        massage1View = (AutoItemView) findViewById(R.id.massage1);
        massage2View = (AutoItemView) findViewById(R.id.massage2);
        massage3View = (AutoItemView) findViewById(R.id.massage3);
        massage4View = (AutoItemView) findViewById(R.id.massage4);
        massage5View = (AutoItemView) findViewById(R.id.massage5);
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
        int x = cx + (int) Math.round(radius * Math.cos(165 * Math.PI / 180));
        int y = cy + (int) Math.round(radius * Math.sin(165 * Math.PI / 180));
        l = x - vw / 2 + strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage1View.layout(l, t, r, b);

        vw = massage2View.getMeasuredWidth();
        vh = massage2View.getMeasuredHeight();
        x = cx + (int) Math.round(radius * Math.cos(215 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(215 * Math.PI / 180));
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
        x = cx + (int) Math.round(radius * Math.cos(325 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(325 * Math.PI / 180));
        l = x - vw / 2 - strokeWidth / 2;
        t = y - vh / 2;
        r = l + vw;
        b = t + vh;
        massage4View.layout(l, t, r, b);

        vw = massage5View.getMeasuredWidth();
        vh = massage5View.getMeasuredHeight();
        x = cx + (int) Math.round(radius * Math.cos(15 * Math.PI / 180));
        y = cy + (int) Math.round(radius * Math.sin(15 * Math.PI / 180));
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
        paint.setColor(bgColor);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        rect.left = getPaddingLeft();
        rect.top = getHeight() - getPaddingBottom() - h / 5;
        rect.right = getWidth() - getPaddingRight();
        rect.bottom = getHeight() - getPaddingBottom();
        drawableY.setBounds(rect);
        drawableY.draw(canvas);

        if (start) {
            if (drawable != null) {
                int vw = massage1View.getMeasuredWidth();
                int vh = massage1View.getMeasuredHeight();
                rect.left = cx - vw / 2;
                rect.top = cy - vh;
                rect.right = rect.left + vw;
                rect.bottom = rect.top + vh;
                paint.setColor(0x7fff0000);
                drawable.setBounds(rect);
                drawable.draw(canvas);
            }
            // canvas.drawRect(rect, paint);
        } else {
            textPaint.setTextSize(w / 22);
            textPaint.getTextBounds(startTip, 0, startTip.length(), boundText);
            canvas.drawText(startTip, cx, cy - boundText.height(), textPaint);
        }

        super.dispatchDraw(canvas);

        // canvas.drawLine(cx - radius, cy, cx + radius, cy, paint);
        // canvas.drawLine(cx, cy - radius, cx, cy + radius, paint);
    }

    private float generateRadius(int w, int h) {
        if (h <= w) {
            return (h - massage3View.getHeight() / 2f) * 50 / 100;
        } else {
            return w * 50 / 100f;
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

    public void start() {
        start = true;
        bgColor = 0xff9438ca;
        startSystemTime = System.currentTimeMillis();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void stop(boolean refreshUi) {
        start = false;
        bgColor = 0xff7128bd;
        stopSystemTime = System.currentTimeMillis();
        if (refreshUi) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public long getStartSystemTime() {
        return startSystemTime;
    }

    public long getStopSystemTime() {
        return stopSystemTime;
    }

    public boolean isStart() {
        return start;
    }

    public List<AutoItem> getAutoItemList() {
        List<AutoItem> autoItemList = new ArrayList<>();
        addAutoItem(massage1View, autoItemList);
        addAutoItem(massage2View, autoItemList);
        addAutoItem(massage3View, autoItemList);
        addAutoItem(massage4View, autoItemList);
        addAutoItem(massage5View, autoItemList);
        return autoItemList;
    }

    private void addAutoItem(AutoItemView view, List<AutoItem> autoItemList) {
        AutoItem autoItem = view.getAutoItem();
        if (autoItem != null) {
            autoItemList.add(autoItem);
        }
    }

    public boolean isEmpty() {
        return massage1View.isModeEmpty() && massage2View.isModeEmpty() && massage3View.isModeEmpty()
                && massage4View.isModeEmpty() && massage5View.isModeEmpty();
    }

    public void setAutoModeDrawable(Drawable drawable) {
        this.drawable = drawable;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
