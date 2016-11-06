package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.morrigan.m.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 中心view
 * Created by y on 2016/10/22.
 */
public class CenterView extends View {

    private float density;
    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Paint textPaint3 = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private RectF rectF = new RectF();
    private float offset = 5;
    private int textPadding = 10;
    private int strokeWidth = 9;
    private String goal = "60";
    private int roundRectRadius = 5;
    private Rect bounds = new Rect();
    private Drawable drawable;

    public CenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        offset *= density;
        textPadding *= density;
        strokeWidth *= density;
        roundRectRadius *= density;
        paint.setAntiAlias(true);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        textPaint3.setAntiAlias(true);
        textPaint3.setColor(0xfff0f0f0);
        drawable = ContextCompat.getDrawable(context, R.drawable.dial1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        float radius = w / 2;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xfff8f9ff);
        canvas.drawCircle(cx, cy, radius, paint);

        radius = radius - strokeWidth;
        paint.setColor(0xff7128bd);
        canvas.drawCircle(cx, cy, radius, paint);

        int left = Math.round(cx - radius);
        int top = cy;
        int right = Math.round(cx + radius);
        int bottom = Math.round(cy + radius);
        drawDynamicWave(canvas, left, right, bottom, top, w, radius, cx, cy);

        textPaint1.setTextSize(w / 4);
        textPaint1.getTextBounds(goal, 0, goal.length(), boundText1);
        textPaint2.setTextSize(w / 10);
        textPaint2.getTextBounds("min", 0, "min".length(), boundText2);
        int x = (w - boundText1.width() - boundText2.width() + textPadding) / 2;
        int y = cy;
        canvas.drawText(goal, x, y, textPaint1);
        x = x + boundText1.width() + textPadding;
        canvas.drawText("min", x, y, textPaint2);

        String date = createDate();
        textPaint2.setTextSize(w / 12);
        textPaint3.setTextSize(w / 14);
        textPaint3.getTextBounds(date, 0, date.length(), boundText1);
        textPaint2.getTextBounds("PM", 0, "PM".length(), boundText2);
        x = (w - boundText1.width() - boundText2.width() - textPadding) / 2;
        y = Math.round(cy + radius / 2); // cy + boundText1.height() + textPadding
        canvas.drawText(date, x, y, textPaint3);
        x = x + boundText1.width() + textPadding;
        canvas.drawText("PM", x, y, textPaint2);

        rectF.left = cx - textPadding - textPadding / 2;
        rectF.top = y + textPadding + textPadding;
        rectF.right = rectF.left + textPadding;
        rectF.bottom = rectF.top + textPadding / 3;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffee7bb1);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);

        rectF.left = cx + textPadding / 2;
        rectF.right = rectF.left + textPadding;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffee7bb1);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, paint);

        bounds.left = 0;
        bounds.top = 0;
        bounds.right = w;
        bounds.bottom = h;
        drawable.setBounds(bounds);
        drawable.draw(canvas);
    }

    private String createDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        return sdf.format(new Date());
    }

    private float av = 1;
    private int gear = 2;

    private void drawDynamicWave(Canvas canvas, int startX, int endX, int startY, int endY, int w, float radius, int cx, int cy) {
        int p = w / 2;
        int y;
        float period = (float) (2 * Math.PI / w);
        float offset = av * (startY - endY) / (10 - gear - 1);
        for (int i = startX; i < endX; i++) {
            // (x-a)^2+(y-b)^2=c^2 其中(a,b)为圆心，c为半径。
            startY = (int) Math.round(endY + Math.sqrt(radius * radius - Math.pow(i - cx, 2)));
            // y = Asin(wx+b)+h ，这个公式里：w影响周期，A影响振幅，h影响y位置，b为初相；
            y = endY + (int) Math.round(offset * (Math.sin(period * (p + i)) + 1) / 2f);

            paint.setColor(0xff8c3eda);
            canvas.drawLine(i, startY, i, y, paint);
        }
        // p += density * 10;
    }

    public void setGoal(String target) {
        goal = target;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
