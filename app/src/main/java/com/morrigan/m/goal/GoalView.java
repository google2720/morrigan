package com.morrigan.m.goal;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.morrigan.m.R;

/**
 * 刻度尺View
 * Created by y on 2016/10/6.
 */
public class GoalView extends View implements GestureDetector.OnGestureListener {

    private static final String TAG = "GoalView";
    private static final long DELAY_MILLIS = 50;

    private ScrollerCompat scroller;
    private GestureDetector detector;
    private Paint linePaint;
    private Paint rulePaint;
    private Paint trianglePaint;
    private Paint valuePaint;
    private Paint unitPaint;
    private Paint tipPaint;
    private Rect boundText = new Rect();
    private Path path = new Path();
    private int lineWidth = 1;
    private int divide = 10;
    private int divideGroup = divide * 5;
    private int triangleGap = 3;
    private String unit;
    private String tip;
    private String tip2;
    private int minValue = 0;
    private int maxValue = 180;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean a = scroller.computeScrollOffset();
            if (!scroller.isFinished()) {
                Log.i(TAG, String.format("computeScroll2 %s/%s", false, a));
                postDelayed(runnable, DELAY_MILLIS);
            } else {
                int sx = getScrollX();
                int mode = sx % divide;
                if (mode != 0) {
                    // scroller.abortAnimation();
                    if (mode >= divide / 2) {
                        scrollBy(divide - mode, 0);
                        // scroller.startScroll(sx, 0, divide - mode, 0, 150);
                    } else {
                        scrollBy(-mode, 0);
                        // scroller.startScroll(sx, 0, -mode, 0, 150);
                    }
                    //ViewCompat.postInvalidateOnAnimation(GoalView.this);
                }
                Log.i(TAG, String.format("computeScroll2 %s/%s", mode, a));
            }
        }
    };
    private int value;
    private int valueMinHeight = 92;
    private boolean firstLoad = true;

    public GoalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        scroller = ScrollerCompat.create(context, null);
        detector = new GestureDetector(context, this);
        float density = getResources().getDisplayMetrics().density;
        lineWidth *= density;
        divide *= density;
        divideGroup = divide * 5;
        triangleGap *= density;
        valueMinHeight *= density;
        unit = context.getString(R.string.minute);
        tip = context.getString(R.string.goal_title_message);
        tip2 = context.getString(R.string.goal_title_message2);
        initPaint();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);

        rulePaint = new Paint();
        rulePaint.setColor(0xb2000000);
        rulePaint.setAntiAlias(true);
        rulePaint.setTextAlign(Paint.Align.CENTER);

        trianglePaint = new Paint();
        trianglePaint.setColor(0xff075D97);
        trianglePaint.setAntiAlias(true);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltqh.ttf");
        valuePaint = new Paint();
        valuePaint.setColor(0xff075D97);
        valuePaint.setStrokeWidth(1);
        valuePaint.setAntiAlias(true);
        valuePaint.setTypeface(font);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        unitPaint = new Paint();
        unitPaint.setColor(0xff7f7f7f);
        unitPaint.setAntiAlias(true);
        unitPaint.setTypeface(font);

        tipPaint = new Paint();
        tipPaint.setColor(0x7f000000);
        tipPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.app_text_small));
        tipPaint.setAntiAlias(true);
        tipPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && firstLoad) {
            firstLoad = false;
            scrollTo(value * divide, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = MotionEventCompat.getActionMasked(event);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                removeCallbacks(runnable);
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                postDelayed(runnable, DELAY_MILLIS);
                break;
            default:
                break;
        }
        detector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int sx = getScrollX();
        final int centerX = sx + w / 2;
        final int offset = w / 2;

        // 倒三角
        int top = getPaddingTop();
        int bottom = top + triangleGap * 3;
        path.reset();
        path.moveTo(centerX, bottom);
        path.lineTo(centerX - triangleGap * 2, top);
        path.lineTo(centerX + triangleGap * 2, top);
        path.close();
        canvas.drawPath(path, trianglePaint);

        // 值
        top = bottom + divide;
        final String valueStr = String.valueOf((sx + w / 2 - offset) / divide);
        valuePaint.setTextSize(h / 4);
        valuePaint.getTextBounds(valueStr, 0, valueStr.length(), boundText);
        final int valueY = top + Math.max(valueMinHeight, boundText.height());
        canvas.drawText(valueStr, centerX, valueY, valuePaint);

        // 单位:分
        unitPaint.setTextSize(h / 8);
        final int unitX = centerX + (boundText.width() / 2) + divide * 3;
        canvas.drawText(unit, unitX, valueY, unitPaint);

        // 刻度
        top = valueY + divide * 3;
        int lineBottom = top + divide * 3;
        rulePaint.setTextSize(h / 20);
        rulePaint.getTextBounds("0", 0, "0".length(), boundText);
        int th = boundText.height();
        int ruleTextBottom = lineBottom + th + divide * 2;
        int v;
        for (int i = sx; i <= sx + w; i++) {
            if (i < w / 2) {
                continue;
            }
            if (i > maxValue * divide + offset) {
                break;
            }
            v = i - offset;
            if (v % divideGroup == 0) {
                linePaint.setColor(0xb2000000);
                canvas.drawLine(i, lineBottom, i, lineBottom - divide * 3, linePaint);
                canvas.drawText(String.valueOf(v / divide), i, ruleTextBottom, rulePaint);
            } else if (v % divide == 0) {
                linePaint.setColor(0x7f000000);
                canvas.drawLine(i, lineBottom, i, lineBottom - divide * 2, linePaint);
            }
        }
        linePaint.setColor(0xcc8c39e5);
        canvas.drawLine(centerX, lineBottom, centerX, top, linePaint);

        // 三角
        path.reset();
        path.moveTo(centerX, lineBottom + triangleGap);
        path.lineTo(centerX - triangleGap, lineBottom + triangleGap * 2);
        path.lineTo(centerX + triangleGap, lineBottom + triangleGap * 2);
        path.close();
        canvas.drawPath(path, trianglePaint);

        // 文字提示
        top = ruleTextBottom + divide * 3;
        tipPaint.getTextBounds(tip, 0, tip.length(), boundText);
        th = boundText.height();
        canvas.drawText(tip, centerX, top + th, tipPaint);

        top = top + th + divide / 2;
        canvas.drawText(tip2, centerX, top + th, tipPaint);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        overScrollByCompat(Math.round(distanceX), Math.round(distanceY), getScrollX(), getScrollY(), getHorizontalScrollRange(), 0, 0, 0, true);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int scrollX = getScrollX();
        int range = getHorizontalScrollRange();
        boolean canFling = (scrollX > 0 || velocityX > 0) && (scrollX < range || velocityX < 0);
        Log.i(TAG, String.format("onFling %s/%s/%s/%s", velocityX, scrollX, range, canFling));
        if (canFling) {
            scroller.fling(scrollX, 0, Math.round(-velocityX), 0, 0, range, 0, 0, getWidth() / 2, 0);
        }
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    @Override
    public void computeScroll() {
        Log.i(TAG, String.format("computeScroll %s", scroller.computeScrollOffset()));
        if (scroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            if (oldX != x || oldY != y) {
                int rangeHorizontal = getHorizontalScrollRange();
                overScrollByCompat(x - oldX, y - oldY, oldX, oldY, rangeHorizontal, 0, 0, 0, false);
            }
        }
    }

    private int getHorizontalScrollRange() {
        return Math.max(0, getViewMaxWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
    }

    boolean overScrollByCompat(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int overScrollMode = getOverScrollMode();
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || overScrollMode == 1 && canScrollHorizontal;
        boolean overScrollVertical = overScrollMode == 0 || overScrollMode == 1 && canScrollVertical;
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = 0;
        }

        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY = 0;
        }

        int left = -maxOverScrollX;
        int right = maxOverScrollX + scrollRangeX;
        int top = -maxOverScrollY;
        int bottom = maxOverScrollY + scrollRangeY;
        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }

        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }

        if (clampedX) {
            scroller.springBack(newScrollX, 0, 0, getHorizontalScrollRange(), 0, 0);
        }

        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);

        Log.i(TAG, String.format("overScrollByCompat %s/%s/%s/%s/%s", canScrollHorizontal, clampedX, scrollX, newScrollX, scrollRangeX));
        return clampedX || clampedY;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int scrollRange = getViewMaxWidth();
        int scrollX = getScrollX();
        int overScrollLeft = Math.max(0, scrollRange - contentWidth);
        if (scrollX < 0) {
            scrollRange -= scrollX;
        } else if (scrollX > overScrollLeft) {
            scrollRange += scrollX - overScrollLeft;
        }
        return scrollRange;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.scrollTo(scrollX, scrollY);
    }

    private int getViewMaxWidth() {
        return maxValue * divide + getWidth();
    }

    public int getValue() {
        return (getScrollX() + getWidth() / 2 - getWidth() / 2) / divide;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
