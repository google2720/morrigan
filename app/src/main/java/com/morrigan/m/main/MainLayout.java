package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * 主界面布局
 * Created by y on 2016/10/3.
 */
public class MainLayout extends FrameLayout {

    private boolean open;

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public boolean isMenuOpen() {
        return open;
    }

    public void closeMenu() {
        if (open) {
            open = false;
            ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), 0);
            animator.setDuration(250);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }

    public void openMenu() {
        if (!open) {
            open = true;
            ObjectAnimator animator = ObjectAnimator.ofInt(this, "animValue", getChildAt(1).getLeft(), getChildAt(0).getWidth());
            animator.setDuration(250);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }

    @Keep
    public void setAnimValue(int v) {
        View view = getChildAt(1);
        view.layout(v, v / 10, v + view.getWidth(), getHeight() - v / 10);
    }
}
