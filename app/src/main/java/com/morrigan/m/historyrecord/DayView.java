package com.morrigan.m.historyrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * Created by fei on 2016/10/15.
 */

public class DayView extends View {

    List<Integer> datas;
    int max;
    Paint mPaint;

    public DayView(Context context) {
        super(context);
        initView();
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffbe8ef2);
        mPaint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas != null) {

            canvas.drawLine(0, (float) 0.85 * getHeight(), getWidth() - 3, (float) 0.85 * getHeight() + 2, mPaint);
            for (int i = 0; i < datas.size(); i++) {
                int w = (int) ((1 / 47.0) * getWidth());
                int l = (int) ((i * 2 / 47.0) * getWidth());
                int t = (int) (getHeight() * (0.2 + 0.6 * (1 - (datas.get(i) * 1.0 / max))));
                int r = l + w;
                int b = (int) (getHeight() * 0.85+1);
                if (max==0){
                    t=b-5;//数据全为0时。//默认画出高度为5px柱形
                }else {
                    if(b-t<5){
                        t=b-5; //数据小于默认值时。画出高度为5px柱形
                    }

                }
                canvas.drawRect(l, t, r, b, mPaint);
                if (max == datas.get(i) && max != 0) {//画出最大值数字
                    String str = max + "";
                    Rect bounds = new Rect();
                    mPaint.getTextBounds(str, 0, str.length(), bounds);
                    int baseline = (int) (0.2 * getMeasuredHeight() / 2) + bounds.height() / 2;
                    canvas.drawText(str, l + w / 2 - bounds.width() / 2, baseline, mPaint);
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
                    mPaint.setTextSize(35);
                    float x = 0;
                    if (hour == 24) {
                        x = l + w / 2 - bounds.width() / 2 - w;
                    } else {
                        x = l + w / 2 - bounds.width() / 2;
                    }

                    float baseline = (int) (0.925 * getMeasuredHeight()) + bounds.height() / 2;

                    canvas.drawText(str, x, baseline, mPaint);
                }

            }

        }
    }

    public void refreshData(List<Integer> datas) {
        this.datas = datas;
        max = Collections.max(datas);
        invalidate();
    }
}
