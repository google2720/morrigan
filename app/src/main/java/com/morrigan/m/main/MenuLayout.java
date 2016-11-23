package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Keep;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * 主界面布局
 * Created by y on 2016/10/3.
 */
public class MenuLayout extends FrameLayout {

    private static final String TAG = "MainLayout";
    private ViewDragHelper dragHelper;
    private boolean open;
    private boolean touchDownCloseEnable;

    public MenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                dragHelper.captureChildView(getChildAt(1), pointerId);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == getChildAt(1)) {
                    if (releasedChild.getLeft() >= getWidth() / 2) {
                        openMenuInner();
                    } else {
                        closeMenuInner();
                    }
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView == getChildAt(1)) {
                    setAnimValue(left);
                }
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
        if (open) {
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
        if (action == MotionEvent.ACTION_DOWN) {
            if (open) {
                View view = getChildAt(1);
                if (pointInView(view, event.getX(), event.getY())) {
                    closeMenu();
                    touchDownCloseEnable = true;
                    return true;
                }
            }
        }
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    private boolean pointInView(View view, float x, float y) {
        return x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchDownCloseEnable) {
            int action = MotionEventCompat.getActionMasked(event);
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                touchDownCloseEnable = false;
            }
            return true;
        }
        dragHelper.processTouchEvent(event);
        return true;
    }

    public boolean isMenuOpen() {
        return open;
    }

    public void closeMenu() {
        if (open) {
            open = false;
            closeMenuInner();
        }
    }

    private void closeMenuInner() {
        open = false;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), 0);
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public void openMenu() {
        if (!open) {
            openMenuInner();
        }
    }

    private void openMenuInner() {
        open = true;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), getChildAt(0).getWidth());
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        dragHelper.setEdgeTrackingEnabled(0);
    }

    @Keep
    public void setAnimValue(int v) {
        View view = getChildAt(1);
        view.layout(v, v / 10, v + view.getWidth(), getHeight() - v / 10);
    }
}
