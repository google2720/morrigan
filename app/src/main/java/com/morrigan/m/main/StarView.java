package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.morrigan.m.R;

/**
 * 星标控件
 * Created by y on 2016/10/22.
 */
public class StarView extends View {

    private Paint paint = new Paint();
    private Paint textPaint1 = new Paint();
    private Paint textPaint2 = new Paint();
    private Rect boundText1 = new Rect();
    private Rect boundText2 = new Rect();
    private int textPadding = 5;
    private int offsetRadius = 5;
    private int strokeWidth = 2;
    private Rect bounds = new Rect();
    private Drawable drawable;

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        textPadding *= density;
        offsetRadius *= density;
        strokeWidth *= density;
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.WHITE);
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(Color.WHITE);
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_star0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth();
        final int h = getHeight();
        final int cx = w / 2;
        final int cy = h / 2;
        float radius = w / 2 - strokeWidth;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff9438ca);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.drawCircle(cx, cy, radius - offsetRadius, paint);

        textPaint1.setTextSize(w / 4);
        textPaint1.getTextBounds("88", 0, "88".length(), boundText1);
        textPaint2.setTextSize(w / 8);
        textPaint2.getTextBounds("star", 0, "star".length(), boundText2);
        int x = (w - boundText1.width() - boundText2.width() - textPadding) / 2;
        canvas.drawText("88", x, cy, textPaint1);
        x = x + boundText1.width() + textPadding;
        canvas.drawText("star", x, cy, textPaint2);

        bounds.left = (w - drawable.getIntrinsicWidth()) / 2;
        bounds.top = cy + textPadding + textPadding / 2;
        bounds.right = bounds.left + drawable.getIntrinsicWidth();
        bounds.bottom = bounds.top + drawable.getIntrinsicHeight();
        drawable.setBounds(bounds);
        drawable.draw(canvas);
    }

    public void setStar(int rank) {
        switch (rank) {
            case 0:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star1);
                break;
            case 1:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star2);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star3);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star4);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star5);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star5);
                break;
            case -1:
            default:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star0);
                break;
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
