package com.morrigan.m.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
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
public class MusicAmplitudeView extends View {

    private Paint mForePaint1 = new Paint();


    boolean active;
    private long updateUiTime;
    Random random = new Random();
    MyHandler myHandler = new MyHandler();
    int messageWhat = 1001;
    public void setActive(boolean active) {
        this.active = active;
    }

    public MusicAmplitudeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(0xff8c39e5);
        update();
    }


    private void update() {
        if (SystemClock.elapsedRealtime() - updateUiTime > 200) {
            ViewCompat.postInvalidateOnAnimation(this);
            updateUiTime = SystemClock.elapsedRealtime();
            update();
        }
        myHandler.sendEmptyMessage(messageWhat);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (active){
                ViewCompat.postInvalidateOnAnimation(MusicAmplitudeView.this);
                updateUiTime = SystemClock.elapsedRealtime();
            }
            myHandler.sendEmptyMessageDelayed(messageWhat, 200);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 4; i++) {
            float w = (float) (1 / 7.0) * getWidth();
            float l = i * w * 2;
            float t = 0;
            if (i == 0) {
                t = (float) (random.nextInt((int)(getHeight()/2.0)) );
            } else if (i == 3) {
                t = (float) (random.nextInt((int)(getHeight()/2.0)) );
            } else {
                t = (float) (random.nextInt(getHeight()));
            }
            float r = l + w;
            float b = (float) getHeight();
            canvas.drawRect(l, t, r, b, mForePaint1);
        }

    }
}
