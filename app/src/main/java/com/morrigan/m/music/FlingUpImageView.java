package com.morrigan.m.music;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by fei on 2016/11/6.
 */

public class FlingUpImageView extends ImageView {

    private GestureDetector mGD;
    private OpenPopup openPopup;
    public void setOpenPopup(OpenPopup openPopup){
        this.openPopup=openPopup;
    }

    public FlingUpImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGD = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int dy= (int) (e2.getY() - e1.getY()); //计算滑动的距离
                if (Math.abs(dy) > 10 && Math.abs(velocityY) > Math.abs(velocityX)) { //降噪处理，必须有较大的动作才识别
                    if (velocityY <-10 ) {
                       if(openPopup!=null){
                           openPopup.openPopup();
                       }

                    }
                    return true;
                } else {
                    return false; //当然可以处理velocityY处理向上和向下的动作
                }
            }
        });
    }
    /*提示大家上面仅仅探测了Fling动作仅仅实现了onFling方法，这里相关的还有以下几种方法来实现具体的可以参考我们以前的文章有详细的解释:
    boolean onDoubleTap(MotionEvent e)
    boolean onDoubleTapEvent(MotionEvent e)
    boolean onDown(MotionEvent e)
    void onLongPress(MotionEvent e)
    boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    void onShowPress(MotionEvent e)
    boolean onSingleTapConfirmed(MotionEvent e)
    boolean onSingleTapUp(MotionEvent e)
    */
//接下来是重点，让我们的View接受触控，需要使用下面两个方法让GestureDetector类去处理onTouchEvent和onInterceptTouchEvent方法。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGD.onTouchEvent(event);
        return true;
    }

}
