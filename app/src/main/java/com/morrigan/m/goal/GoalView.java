package com.morrigan.m.goal;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
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
    private ScrollerCompat scroller;
    private GestureDetector detector;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private Paint paint4;
    private Rect boundText = new Rect();
    private Path path = new Path();
    private int divide = 10;
    private int divideGroup = divide * 5;
    private int triangleGap = 3;
    private int minValueHeight = 64;
    private String unit;
    private int minValue = 0;
    private int maxValue = 300;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean a = scroller.computeScrollOffset();
            if (!scroller.isFinished()) {
                Log.i(TAG, String.format("computeScroll2 %s/%s", false, a));
                postDelayed(runnable, 250);
            } else {
                int sx = getScrollX();
                int mode = sx % divide;
                if (mode != 0) {
                    if (mode >= divide / 2) {
                        scroller.startScroll(sx, 0, divide - mode, 0, 150);
                    } else {
                        scroller.startScroll(sx, 0, -mode, 0, 150);
                    }
                    ViewCompat.postInvalidateOnAnimation(GoalView.this);
                }
                Log.i(TAG, String.format("computeScroll2 %s/%s", mode, a));
            }
        }
    };
    private int value;

    public GoalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        scroller = ScrollerCompat.create(context, null);
        detector = new GestureDetector(context, this);
        initPaint();
        float density = getResources().getDisplayMetrics().density;
        divide *= density;
        divideGroup = divide * 5;
        triangleGap *= density;
        minValueHeight *= density;
        unit = context.getString(R.string.minute);
    }

    private void initPaint() {
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
        paint3.setStrokeWidth(3);

        paint4 = new Paint();
        paint4.setColor(0xff7d1db6);
        paint4.setAntiAlias(true);
        paint4.setTextAlign(Paint.Align.CENTER);
        paint4.setTextSize(256);
        paint4.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (value != 0) {
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
                postDelayed(runnable, 500);
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
        paint2.getTextBounds("0", 0, "0".length(), boundText);
        final int th = boundText.height();
        final int lineBottom = h - th - divide * 2;
        final int offset = w / 2;
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
                canvas.drawLine(i, lineBottom, i, lineBottom - divide * 3, paint2);
                canvas.drawText(String.valueOf(v / divide), i, h - divide, paint2);
            } else if (v % divide == 0) {
                canvas.drawLine(i, lineBottom, i, lineBottom - divide * 2, paint);
            }
        }

        final int centerX = sx + w / 2;
        canvas.drawLine(centerX, lineBottom, centerX, lineBottom - divide * 3, paint3);
        path.reset();
        path.moveTo(centerX, lineBottom + triangleGap);
        path.lineTo(centerX - triangleGap, lineBottom + triangleGap * 2);
        path.lineTo(centerX + triangleGap, lineBottom + triangleGap * 2);
        path.close();
        canvas.drawPath(path, paint3);

        final int vBottom = lineBottom - divide * 5;
        final String valueStr = String.valueOf((sx + w / 2 - offset) / divide);
        canvas.drawText(valueStr, centerX, vBottom, paint4);
        paint4.getTextBounds(valueStr, 0, valueStr.length(), boundText);
        canvas.drawText(unit, centerX + boundText.width() / 2 + divide * 2, vBottom, paint2);

        final int top = vBottom - Math.max(minValueHeight, boundText.height()) - divide;
        path.reset();
        path.moveTo(centerX, top);
        path.lineTo(centerX - triangleGap * 2, top - triangleGap * 2);
        path.lineTo(centerX + triangleGap * 2, top - triangleGap * 2);
        path.close();
        canvas.drawPath(path, paint3);
        // Log.i(TAG, String.format("onDraw %d/%d/%d/%d/%d", w, h, sx, vBottom, boundText.height()));
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
        overScrollByCompat(Math.round(distanceX), Math.round(distanceY), getScrollX(), getScrollY(),
                getHorizontalScrollRange(), 0, 0, 0, true);
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
        return Math.max(0, getMaxWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
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
        int scrollRange = getMaxWidth();
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

    private int getMaxWidth() {
        return maxValue * divide + getWidth();
    }

    public int getValue() {
        return (getScrollX() + getWidth() / 2 - getWidth() / 2) / divide;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
