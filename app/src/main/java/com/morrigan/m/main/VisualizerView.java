package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 音乐振幅
 * Created by y on 2016/10/19.
 */
public class VisualizerView extends View {

    private Paint mForePaint1 = new Paint();
    private Paint mForePaint2 = new Paint();
    List<Float> vols;
    private long updateUiTime;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(0xffffffff);
        mForePaint2.setStrokeWidth(1f);
        mForePaint2.setAntiAlias(true);
        mForePaint2.setColor(0xff5B73EE);
        vols = new ArrayList<>();
        Random random = new Random();
        int max = 256;
        vols = new ArrayList<>();
        int tem2 = max;
        for (int i = 0; i < 13; i++) {
            tem2 = (int) ((2.0 / 4) * max);
            float tem3 = random.nextInt(tem2);
            vols.add(tem3);
        }
        for (int i = 13; i < 28; i++) {
            tem2 = max;
            float tem3 = random.nextInt(tem2);
            vols.add(tem3);
        }
        for (int i = 28; i < 41; i++) {
            tem2 = (int) ((2.0 / 4) * max);
            float tem3 = random.nextInt(tem2);
            vols.add(tem3);
        }
    }


    public void updateVisualizer(byte[] bytes) {
        if (bytes != null) {
//            int max = 0;
//            for (int i = 0; i < bytes.length; i++) {
//                if (bytes[i] > max) {
//                    max = bytes[i];
//                }
//            }
//            max = max + 128;
            int max = 256;
            Random random = new Random();
            vols = new ArrayList<>();
            int tem2 = max;
            for (int i = 0; i < 13; i++) {
                tem2 = (int) ((2.0 / 4) * max);
                float tem3 = random.nextInt(tem2);
                vols.add(tem3);
            }
            for (int i = 13; i < 28; i++) {
                tem2 = max;
                float tem3 = random.nextInt(tem2);
                vols.add(tem3);
            }
            for (int i = 28; i < 41; i++) {
                tem2 = (int) ((2.0 / 4) * max);
                float tem3 = random.nextInt(tem2);
                vols.add(tem3);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void update() {
        if (SystemClock.elapsedRealtime() - updateUiTime > 2) {
            if (vols.size() == 41) {
                for (int i = 0; i < 41; i++) {
                    if (vols.get(i) > 1) {
                        vols.set(i, vols.get(i) - 1);
                    }
                }
                ViewCompat.postInvalidateOnAnimation(this);
                updateUiTime = SystemClock.elapsedRealtime();
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 41; i++) {
            float vol = vols.get(i);
            float w = (float) (1 / 122.0) * getWidth();
            float l = i * w * 3;
            float t = (float) ((getHeight() - (vol / 256f) * getHeight()) * 0.5);
            float r = l + 2 * w;
            float b = (float) (getHeight() * 0.5) - 5;
            canvas.drawRect(l, t, r, b, mForePaint1);
        }
        for (int i = 0; i < 41; i++) {
            float vol = vols.get(i);
            float w = (float) (1 / 122.0) * getWidth();
            float l = i * w * 3;
            float t = (float) (getHeight() * 0.5 + 5);
            float r = l + 2 * w;
            float b = (float) (t + ((vol / 256f) * getHeight() * 0.5));
            canvas.drawRect(l, t, r, b, mForePaint2);
        }
    }
}
