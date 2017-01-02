package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.morrigan.m.R;

/**
 * 主界面布局
 * Created by y on 2016/10/3.
 */
public class MenuLayout extends FrameLayout {

    private static final String TAG = "MenuLayout";
    private ViewDragHelper dragHelper;
    private boolean stateOpened;
    private boolean touchDownCloseEnable;
    private Callback callback;
    private Rect rect = new Rect();
    private Paint paint = new Paint();
    private int shadeWidth = 10;

    public MenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final float density = context.getResources().getDisplayMetrics().density;
        shadeWidth *= density;
        paint.setColor(Color.WHITE);
        dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == getChildAt(1);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int leftBound = getPaddingLeft();
                int rightBound = getWidth() - getPaddingRight() - leftBound;
                return Math.min(Math.max(left, leftBound), rightBound);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }


            @Override
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                Log.i(TAG, "onEdgeTouched " + edgeFlags);
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                if (!touchDownCloseEnable) {
                    dragHelper.captureChildView(getChildAt(1), pointerId);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.i(TAG, "onViewReleased " + xvel + " " + yvel);
                if (!touchDownCloseEnable) {
                    if (releasedChild == getChildAt(1)) {
                        if (releasedChild.getLeft() >= getWidth() / 2) {
                            openMenuInner();
                        } else {
                            closeMenuInner();
                        }
                    }
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView == getChildAt(1)) {
                    setAnimValue(left);
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.i(TAG, "onViewDragStateChanged " + state);
            }
        });
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(getMeasuredWidth() * 0.8f), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (stateOpened) {
            setAnimValue(getChildAt(0).getWidth());
        }
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        Log.i(TAG, "onInterceptTouchEvent " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDownCloseEnable = false;
                if (stateOpened) {
                    View view = getChildAt(1);
                    if (pointInView(view, event.getX(), event.getY())) {
                        touchDownCloseEnable = true;
                    }
                }
                break;
            default:
                break;
        }
        return dragHelper.shouldInterceptTouchEvent(event) || touchDownCloseEnable;
    }

    private boolean pointInView(View view, float x, float y) {
        return x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        Log.i(TAG, "onTouchEvent " + action + " " + touchDownCloseEnable);
        if (touchDownCloseEnable) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (dragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL)) {
                        touchDownCloseEnable = false;
                        dragHelper.captureChildView(getChildAt(1), dragHelper.getActivePointerId());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    closeMenu();
                    break;
                default:
                    break;
            }
        }
        dragHelper.processTouchEvent(event);
        return true;
    }

    public boolean isMenuOpen() {
        return stateOpened;
    }

    public void closeMenuWithNoAnim() {
        Log.i(TAG, "closeMenuWithNoAnim " + stateOpened);
        stateOpened = false;
        setAnimValue(0);
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        if (callback != null) {
            callback.onMenuOpenStatusChange(false);
        }
    }

    public void closeMenu() {
        Log.i(TAG, "closeMenu " + stateOpened);
        if (stateOpened) {
            closeMenuInner();
        }
    }

    private void closeMenuInner() {
        stateOpened = false;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), 0);
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        if (callback != null) {
            callback.onMenuOpenStatusChange(stateOpened);
        }
    }

    public void openMenu() {
        Log.i(TAG, "openMenu " + stateOpened);
        if (!stateOpened) {
            openMenuInner();
        }
    }

    private void openMenuInner() {
        stateOpened = true;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), getChildAt(0).getWidth());
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        dragHelper.setEdgeTrackingEnabled(0);
        if (callback != null) {
            callback.onMenuOpenStatusChange(stateOpened);
        }
    }

    @Keep
    public void setAnimValue(int v) {
        View view = getChildAt(1);
        view.layout(v, v / 10, v + view.getWidth(), getHeight() - v / 10);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public interface Callback {
        void onMenuOpenStatusChange(boolean openStatus);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View view = getChildAt(1);
        final int left = view.getLeft();
        final int top = view.getTop();
        final int right = view.getRight();
        final int bottom = view.getBottom();
        final Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.nemu_bg);
        rect.left = left - shadeWidth;
        rect.top = top - shadeWidth;
        rect.right = right + shadeWidth;
        rect.bottom = bottom + shadeWidth;
        drawable.setBounds(rect);
        drawable.draw(canvas);
        super.dispatchDraw(canvas);
        final int alpha = Math.round((right - left) * 1f / right * 255);
        paint.setAlpha(255 - alpha);
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;
        canvas.drawRect(rect, paint);
    }
}
