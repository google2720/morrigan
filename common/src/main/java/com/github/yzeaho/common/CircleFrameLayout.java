package com.github.yzeaho.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 可见部分为圆型的ImageView
 *
 * @author y
 */
public class CircleFrameLayout extends FrameLayout {

    private final Paint restorePaint = new Paint();
    private final Paint maskXferPaint = new Paint();
    private final Paint canvasPaint = new Paint();
    private final Rect bounds = new Rect();
    private final RectF boundsf = new RectF();

    public CircleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        canvasPaint.setAntiAlias(true);
        canvasPaint.setColor(Color.argb(255, 255, 255, 255));
        restorePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        maskXferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        setLayerType(View.LAYER_TYPE_HARDWARE, restorePaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.getClipBounds(bounds);
        boundsf.set(bounds);
        super.dispatchDraw(canvas);
        canvas.saveLayer(boundsf, maskXferPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawARGB(0, 0, 0, 0);
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.drawCircle(w / 2, h / 2, w / 2, canvasPaint);
        canvas.restore();
    }
}
