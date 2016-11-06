package com.morrigan.m.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.morrigan.m.R;

/**
 * 主界面
 * Created by y on 2016/10/20.
 */
public class MainLayout extends FrameLayout {

    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private BatteryView batteryView;
    private CenterView centerView;
    private StarView startView;
    private int outlineSize = 5;
    private int bOutlineSize = 6;
    private int offset = 1;
    private Path path = new Path();
    private Point point = new Point();
    private int pathOffset = 8;
    private int batteryPadding = 20;

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        outlineSize *= density;
        bOutlineSize *= density;
        pathOffset *= density;
        offset *= density;
        batteryPadding *= density;
        paint.setAntiAlias(true);
        // drawableBg = getResources().getDrawable(R.drawable.dial_bg);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        batteryView = (BatteryView) findViewById(R.id.battery);
        centerView = (CenterView) findViewById(R.id.center);
        startView = (StarView) findViewById(R.id.star);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int w = getMeasuredWidth();
        final int h = getMeasuredHeight();
        final int side = Math.min(w, h);
        int widthSpec = MeasureSpec.makeMeasureSpec(side * 60 / 100, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(side * 60 / 100, MeasureSpec.EXACTLY);
        centerView.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(side / 5, MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(side / 5, MeasureSpec.EXACTLY);
        batteryView.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(side / 4, MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(side / 4, MeasureSpec.EXACTLY);
        startView.measure(widthSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int w = getWidth();
        final int h = getHeight();

        int vw = centerView.getMeasuredWidth();
        int vh = centerView.getMeasuredHeight();
        int l = w / 2 - vw / 2;
        int t = h / 2 - vh / 2 - batteryPadding;
        int r = l + vw;
        int b = t + vh;
        centerView.layout(l, t, r, b);

        vw = batteryView.getMeasuredWidth();
        vh = batteryView.getMeasuredHeight();
        l = w - vw - batteryPadding;
        t = batteryPadding;
        r = l + vw;
        b = t + vh;
        batteryView.layout(l, t, r, b);

        vw = startView.getMeasuredWidth();
        vh = startView.getMeasuredHeight();
        l = w - vw - batteryView.getMeasuredWidth() / 2;
        t = h - vh - batteryPadding;
        r = l + vw;
        b = t + vh;
        startView.layout(l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();

        int cx = centerView.getLeft() + centerView.getWidth() / 2;
        int cy = centerView.getTop() + centerView.getHeight() / 2;
        float radius = centerView.getWidth() / 2 + outlineSize + offset * 4;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff9a43cd);
        canvas.drawCircle(cx, cy - offset, radius, paint);
        radius = centerView.getWidth() / 2 + outlineSize + offset;
        paint.setColor(0xffbc55f8);
        canvas.drawCircle(cx, cy - offset, radius, paint);

        int ccx = centerView.getLeft() + centerView.getWidth() / 2;
        int ccy = centerView.getTop() + centerView.getHeight() / 2;
        int cr = centerView.getWidth() / 2 + outlineSize;
        paint.setColor(0xff852abb);
        canvas.drawCircle(ccx, ccy, cr, paint);

        cx = batteryView.getLeft() + batteryView.getWidth() / 2;
        cy = batteryView.getTop() + batteryView.getHeight() / 2;
        radius = batteryView.getWidth() / 2 + bOutlineSize + offset * 4;
        paint.setColor(0xff9a43cd);
        canvas.drawCircle(cx, cy + offset, radius, paint);
        radius = batteryView.getWidth() / 2 + bOutlineSize + offset;
        paint.setColor(0xffbc55f8);
        canvas.drawCircle(cx, cy + offset, radius, paint);

        int bcx = batteryView.getLeft() + batteryView.getWidth() / 2;
        int bcy = batteryView.getTop() + batteryView.getHeight() / 2;
        int br = batteryView.getWidth() / 2 + bOutlineSize;
        paint.setColor(0xff852abb);
        canvas.drawCircle(bcx, bcy, br, paint);

        cx = startView.getLeft() + startView.getWidth() / 2;
        cy = startView.getTop() + startView.getHeight() / 2;
        radius = startView.getWidth() / 2 + outlineSize / 2 + offset * 4;
        paint.setColor(0xff9a43cd);
        canvas.drawCircle(cx, cy + offset, radius, paint);
        radius = startView.getWidth() / 2 + outlineSize / 2 + offset;
        paint.setColor(0xffbc55f8);
        canvas.drawCircle(cx, cy + offset, radius, paint);

        int scx = startView.getLeft() + startView.getWidth() / 2;
        int scy = startView.getTop() + startView.getHeight() / 2;
        int sr = startView.getWidth() / 2 + outlineSize / 2;
        paint.setColor(0xff852abb);
        canvas.drawCircle(scx, scy, sr, paint);

        super.dispatchDraw(canvas);

//        paint.setColor(Color.BLACK);
//        rect.left = batteryView.getLeft();
//        rect.top = batteryView.getTop();
//        rect.right = batteryView.getRight();
//        rect.bottom = batteryView.getBottom();
//        canvas.drawRect(rect, paint);
//        paint.setColor(Color.RED);

        // cosA=(b方＋c方－a方)／2*b*c
        int a = bcx - ccx;
        int b = ccy - bcy;
        double c = Math.sqrt(a * a + b * b);
        double ba = Math.acos(b / c) * 180 / Math.PI;
        Log.i("abc", String.format("a %s/%s/%s/%s", a, b, c, ba));

        /*
        圆点坐标：(x0,y0)
        半径：r
        角度：angle
        则圆上任一点为：（x1,y1）
        x1   =   x0   +   r   *   cos(angle   *   3.14   /180   )
        y1   =   y0   +   r   *   sin(angle   *   3.14   /180   )
        */
        // (x-a)^2+(y-b)^2=c^2 其中(a,b)为圆心，c为半径。
        int x2 = (int) Math.round(bcx + br * Math.cos((90 + ba + 20) * Math.PI / 180));
        int y2 = (int) Math.round(bcy + br * Math.sin((90 + ba + 20) * Math.PI / 180));
        int x3 = (int) Math.round(bcx + br * Math.cos((90 + ba - 20) * Math.PI / 180));
        int y3 = (int) Math.round(bcy + br * Math.sin((90 + ba - 20) * Math.PI / 180));
        calculateCenterPoint(ccx, ccy, x2, y2, cr, point);
        int x1 = point.x;
        int y1 = point.y;
        calculateCenterPoint(ccx, ccy, x3, y3, cr, point);
        int x4 = point.x;
        int y4 = point.y;
        Log.i("abc", String.format("x %s/%s/%s/%s", x1, y1, x2, y2));
        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(x1 + (x2 - x1) / 2 + pathOffset, y1 + (y2 - y1) / 2 + pathOffset, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(x3 - (x3 - x4) / 2 - pathOffset, y3 - (y3 - y4) / 2 - pathOffset, x4, y4);
        path.lineTo(x1, y1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff852abb);
        canvas.drawPath(path, paint);

//        path.reset();
//        path.moveTo(x1, y1);
//        path.quadTo(x1 + (x2 - x1) / 2 + pathOffset, y1 + (y2 - y1) / 2 + pathOffset, x2, y2);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(offset * 2);
//        paint.setColor(0xff9a43cd);
//        canvas.drawPath(path, paint);
//        path.reset();
//        path.moveTo(x3, y3);
//        path.quadTo(x3 - (x3 - x4) / 2 - pathOffset, y3 - (y3 - y4) / 2 - pathOffset, x4, y4);
//        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(x1 + (x2 - x1) / 2 + pathOffset, y1 + (y2 - y1) / 2 + pathOffset, x2, y2);
//        path.cubicTo(x1 + 1, y1 + 2, x1 + (x2 - x1) / 2 + pathOffset, y1 + (y2 - y1) / 2 + pathOffset, x2, y2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(offset);
        paint.setColor(0xffbc55f8);
        canvas.drawPath(path, paint);
        path.reset();
        path.moveTo(x3, y3);
        path.quadTo(x3 - (x3 - x4) / 2 - pathOffset, y3 - (y3 - y4) / 2 - pathOffset, x4, y4);
        canvas.drawPath(path, paint);

//        paint.setColor(Color.RED);
//        canvas.drawLine(x3, y3, ccx, ccy, paint);
//        canvas.drawLine(ccx, ccy, x3, ccy, paint);
//        canvas.drawLine(x3, y3, x3, ccy, paint);
//        canvas.drawLine(bcx, bcy, ccx, ccy, paint);
//        canvas.drawLine(bcx, bcy, bcx, ccy, paint);
//        canvas.drawLine(ccx, ccy, bcx, ccy, paint);

//        bounds.left = getPaddingLeft();
//        bounds.top = getPaddingTop();
//        bounds.right = getRight() - getPaddingRight();
//        bounds.bottom = getBottom() - getPaddingBottom();
//        drawableBg.setBounds(bounds);
//        drawableBg.draw(canvas);
//        super.dispatchDraw(canvas);
    }

    private void calculateCenterPoint(int ccx, int ccy, int x2, int y2, int r, Point point) {
        int a = x2 - ccx;
        int b = y2 - ccy;
        double c = Math.sqrt(a * a + b * b);
        double angle = Math.acos(a / c) * 180 / Math.PI;
        point.x = (int) Math.round(ccx + r * Math.cos((360 - angle) * Math.PI / 180));
        point.y = (int) Math.round(ccy + r * Math.sin((360 - angle) * Math.PI / 180));
    }
}
