package com.morrigan.m.historyrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * Created by fei on 2016/10/15.
 */

public class WeekView extends View {

    List<Integer> datas;
    int max;
    Paint mPaint;
    public WeekView(Context context) {
        super(context);
        initView();
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    void initView(){
        mPaint=new Paint();
        mPaint.setColor(0xffbe8ef2);
        mPaint.setTextSize(40);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas!=null){
            canvas.drawLine(0,(float) 0.8*getHeight(),getWidth()-3,(float) 0.8*getHeight()+2,mPaint);
            for(int i=0; i <datas.size(); i ++){
                int w=(int)((1/13.0)*getWidth());
                int l=(int)((i*2/13.0)*getWidth());
                int t=(int)(getHeight()*(0.2+  0.6*(1-(datas.get(i)*1.0/max))));
                int r =l+w;
                int b=(int)(getHeight()*0.8);
                if (max!=0){
                    canvas.drawRect(l, t, r, b, mPaint);
                }

                if (max==datas.get(i)&&max!=0){
                    String str=max+"";
                    Rect bounds = new Rect();

                    mPaint.getTextBounds(str, 0, str.length(), bounds);
                   // Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
                   // int baseline = ((int)0.1*getMeasuredHeight()/2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                   int  baseline=(int)(0.2*getMeasuredHeight()/2)+bounds.height()/2;
                    canvas.drawText(str, l+w/2- bounds.width()/2,baseline , mPaint);
                }
                String strWeek="周一";
                Rect bounds = new Rect();
                strWeek="周一";


                switch (i){
                    case 0:{
                        strWeek="周一";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);

                    }
                    break;
                    case 1:{
                        strWeek="周二";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                    case 2:{
                        strWeek="周三";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                    case 3:{
                        strWeek="周四";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                    case 4:{
                        strWeek="周五";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                    case 5:{
                        strWeek="周六";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                    case 6:{
                        strWeek="周日";
                        mPaint.getTextBounds(strWeek, 0, strWeek.length(), bounds);
                    }
                    break;
                }
                float x=l+w/2- bounds.width()/2;
                float baseline=(int)(0.9*getMeasuredHeight())+bounds.height()/2;
                canvas.drawText(strWeek, x,baseline , mPaint);
            }
        }
    }

    public void refreshData(List<Integer> datas){
        this.datas=datas;
        max= Collections.max(datas);
        invalidate();
    }
}
