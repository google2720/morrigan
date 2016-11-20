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
    private Paint mForePaint1 = new Paint();
    private Paint mForePaint2 = new Paint();
    List<Float> vols;
    float max = 0;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBytes = null;
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(0xffffffff);
        mForePaint2.setStrokeWidth(1f);
        mForePaint2.setAntiAlias(true);
        mForePaint2.setColor(0xffB269FE);
        vols = new ArrayList<>();
        Random random=new Random();
        for (int i = 0; i < 41; i++) {
            int tem1=i%3;
            int tem2=128;
            switch (tem1){
                case 0:{
                     tem2=60;
                }break;
                case 1:{
                     tem2=80;
                }break;
                case 2:{
                     tem2=128;
                }break;
            }
            float tem3=random.nextInt(tem2);
            vols.add( tem3);
        }
        max = Collections.max(vols);
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
            if (max==0||max==min){
                for (int i = 0; i < 41; i++) {
                    Random random=new Random();
                    float tem=random.nextInt(128);
                    vols.set(i, tem);
                }
                max = Collections.max(vols);
                min=Collections.min(vols);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 41; i++) {
            float vol = vols.get(i);
            float w = (float) (1 / 122.0) * getWidth();
            float l = i * w * 3;
            float t = (float) ((getHeight() - (vol / max) * getHeight()) * 0.5);
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
            float b = (float) (t + ((vol / max) * getHeight() * 0.5));
            canvas.drawRect(l, t, r, b, mForePaint2);
        }

    }

}
