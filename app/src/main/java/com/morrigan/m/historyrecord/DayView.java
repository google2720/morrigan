package com.morrigan.m.historyrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.morrigan.m.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by fei on 2016/10/15.
 */

public class DayView extends View {

    List<Integer> datas;
    int max;
    int showNum;
    int showIndex;
    Paint mPaint;
    Paint mPaintNum;
    Context context;

    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }

    public DayView(Context context) {
        super(context);
        initView(context);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    void initView(Context context) {
        this.context=context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffbe8ef2);
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.record_txt_size));
        mPaintNum= new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintNum.setColor(0xffbe8ef2);
        mPaintNum.setTextSize(context.getResources().getDimension(R.dimen.record_txt_size));
        mPaintNum.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas != null) {

            canvas.drawLine(0, (float) 0.85 * getHeight(), getWidth() - 3 - (int) ((1 / 49.0) * getWidth() * 2), (float) 0.85 * getHeight() + 2, mPaint);
            for (int i = 0; i < datas.size(); i++) {
                int w = (int) ((1 / 49.0) * getWidth());
                int l = (int) ((i * 2 / 49.0) * getWidth());
                int t = (int) (getHeight() * (0.2 + 0.6 * (1 - (datas.get(i) * 1.0 / max))));
                int r = l + w;
                int b = (int) (getHeight() * 0.85 + 1);
                if (max == 0) {
                    t = b - 5;//数据全为0时。//默认画出高度为5px柱形
                } else {
                    if (b - t < 5) {
                        t = b - 5; //数据小于默认值时。画出高度为5px柱形
                    }

                }
                if (datas.get(i) == 0) {
                    t = b - 5;//数据为0时。//默认画出高度为5px柱形
                }
                canvas.drawRect(l, t, r, b, mPaint);
                if (showIndex == i && showNum != 0 && showNum == datas.get(i)) {//画出显示数字
                    String str = showNum + "";
                    Rect bounds = new Rect();
                    mPaint.getTextBounds(str, 0, str.length(), bounds);
                    int baseline = t - bounds.height();
                    canvas.drawText(str, l + w / 2 , baseline, mPaintNum);
                }
                int hour = i + 1;
                if (hour == 1 || hour % 6 == 0) {//画出1，6，12，18，24下标
                    String str = "";
                    if (hour == 24) {
                        str = hour + "时";
                    } else {
                        str = hour + "";
                    }

                    Rect bounds = new Rect();
                    mPaint.getTextBounds(str, 0, str.length(), bounds);
                    mPaint.setTextSize(context.getResources().getDimension(R.dimen.record_txt_size2));
                    float x = 0;
                    if (hour == 24) {
                        x = l + w - bounds.width() / 2;
                    } else if (hour == 1) {
                        x = l;
                    } else {
                        x = l + w / 2 - bounds.width() / 2;
                    }

                    float baseline = (int) (0.925 * getMeasuredHeight()) + bounds.height() / 2;

                    canvas.drawText(str, x, baseline, mPaint);
                }

            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                float x = event.getX();
                float y = event.getY();
                int w = (int) ((1 / 49.0) * getWidth());
                int index = (int) x / (2 * w);
                if (index < 24 && datas.get(index) != 0) {
                    int t = (int) (getHeight() * (0.2 + 0.6 * (1 - (datas.get(index) * 1.0 / max))));
                    if (y >= t) {
                        showIndex = index;
                        showNum = datas.get(index);
                        invalidate();
                        ;
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                return true;
        }
        //这句话不要修改
        return super.onTouchEvent(event);
    }

    public void refreshData(List<Integer> datas) {
        this.datas = datas;
        max = Collections.max(datas);
        showIndex = datas.indexOf(max);
        showNum = max;
        invalidate();
    }
}
