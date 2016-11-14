package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * 音乐振幅
 * Created by y on 2016/10/19.
 */
public class VisualizerView extends View {

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();
    List<Float> vols;
    float max = 0;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(0xffB269FE);
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        if (bytes != null && bytes.length == 1024) {
            vols = new ArrayList<>();
            float vol = 0;
            for (int i = 0; i < 41; i++) {
                vol = 128 - Math.abs(mBytes[i * 24]);
                vols.add(vol);
            }
            max = Collections.max(vols);
            float min=Collections.min(vols);
            if (max!=0){
                if (max==min) {
                    for (int i = 0; i < 41; i++) {
                        Random random=new Random();
                        float tem=random.nextInt(128);
                        vols.set(i, tem);
                    }
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }


        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null) {
            return;
        }
        for (int i = 0; i < 41; i++) {
            float vol = vols.get(i);
            float w = (float) (1 / 122.0) * getWidth();
            float l = i * w * 3;
            float t = (float) ((getHeight() - (vol / max) * getHeight()) * 0.5);
            float r = l + 2 * w;
            float b = (float) (getHeight() * 0.5) - 5;
            canvas.drawRect(l, t, r, b, mForePaint);
        }
        for (int i = 0; i < 41; i++) {
            float vol = vols.get(i);
            float w = (float) (1 / 122.0) * getWidth();
            float l = i * w * 3;
            float t = (float) (getHeight() * 0.5 + 5);
            float r = l + 2 * w;
            float b = (float) (t + ((vol / max) * getHeight() * 0.5));
            canvas.drawRect(l, t, r, b, mForePaint);
        }

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (mBytes == null) {
//            return;
//        }
//        if (mPoints == null || mPoints.length < mBytes.length * 4) {
//            mPoints = new float[mBytes.length * 4];
//        }
//        mRect.set(0, 0, getWidth(), getHeight());
//        for (int i = 0; i < mBytes.length - 1; i++) {
//            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
//            mPoints[i * 4 + 1] = mRect.height() / 2
//                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
//            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
//            mPoints[i * 4 + 3] = mRect.height() / 2
//                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
//        }
//        canvas.drawLines(mPoints, mForePaint);
//    }


}
