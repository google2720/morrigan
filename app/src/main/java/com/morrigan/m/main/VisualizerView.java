package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 音乐振幅
 * Created by y on 2016/10/19.
 */
public class VisualizerView extends View {

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBytes = null;
        // mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.WHITE);
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null) {
            return;
        }
        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        final int cy = getHeight() / 2;
        mRect.set(0, 0, getWidth(), getHeight());
        mForePaint.setStrokeWidth(mRect.width() / mBytes.length);
        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = cy;
            mPoints[i * 4 + 2] = mPoints[i * 4];
            mPoints[i * 4 + 3] = cy - cy * Math.abs(mBytes[i]) / 128f;
        }
        canvas.drawLines(mPoints, mForePaint);
        canvas.drawLine(0, cy, getWidth(), cy, mForePaint);
    }
}