package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.morrigan.m.BuildConfig;
import com.morrigan.m.R;

/**
 * 图片光环动画
 * Created by y on 2017/2/14.
 */
public class DrawableHalo implements ManualView.Halo {

    private static final String TAG = "DrawableHalo";
    private Context context;
    private float time;
    private int gear;
    private DrawableItemHalo h1;
    private DrawableItemHalo h2;

    private class DrawableItemHalo {

        private final int defaultWaitTime;
        private float v = 0;
        private Rect rect = new Rect();
        private int alpha = 0xff;
        private int waitTime;
        private boolean debug;
        private final Drawable drawable;
        private Paint paint = new Paint();

        private DrawableItemHalo(int drawableResId, int waitTime, int haloColor, boolean debug) {
            this.waitTime = waitTime;
            this.defaultWaitTime = waitTime;
            this.debug = debug;
            this.drawable = ContextCompat.getDrawable(context, drawableResId);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(haloColor);
        }

        private void draw(Canvas canvas, float totalTime, int cx, int cy, float initRadius, float maxRadius) {
            final float path = maxRadius - initRadius;
            final float offset = path / totalTime;
            if (waitTime > 0) {
                waitTime--;
            } else {
                if (debug) {
                    Log.i(TAG, "draw " + v + " " + alpha + " " + path + " " + offset + " " + totalTime);
                }
                if (BuildConfig.MANUAL_DRAWABLE) {
                    rect.left = Math.round(cx - initRadius - v);
                    rect.top = Math.round(cy - initRadius - v);
                    rect.right = Math.round(cx + initRadius + v);
                    rect.bottom = Math.round(cy + initRadius + v);
                    drawable.setBounds(rect);
                    drawable.setAlpha(alpha);
                    drawable.draw(canvas);
                } else {
                    paint.setAlpha(Math.min(alpha, 0xff));
                    canvas.drawCircle(cx, cy, initRadius + v, paint);
                }
                alpha = 0xff - Math.round(0xff / path * v);
                v += offset;
                if (v > path) {
                    v = 0;
                    alpha = 0xff;
                }
            }
        }

        private void stop() {
            v = 0;
            alpha = 0xff;
            waitTime = defaultWaitTime;
        }
    }

    public DrawableHalo(Context context, float time) {
        this.context = context;
        this.time = time;
        h1 = new DrawableItemHalo(R.drawable.manual_halo, 0, 0xffffffff, false);
        h2 = new DrawableItemHalo(R.drawable.manual_halo, Math.round(time / 2), 0xffffffff, true);
    }

    @Override
    public void draw(Canvas canvas, int cx, int cy, float initRadius, float maxRadius) {
        final float totalTime = time - (gear - 1) * 20;
//        final float totalTime = time / 3 * (3 + 1 - gear);
        h1.draw(canvas, totalTime, cx, cy, initRadius, maxRadius);
        h2.draw(canvas, totalTime, cx, cy, initRadius, maxRadius);
    }

    @Override
    public void setGear(int gear) {
        this.gear = gear;
    }

    @Override
    public void stop() {
        h1.stop();
        h2.stop();
    }
}
