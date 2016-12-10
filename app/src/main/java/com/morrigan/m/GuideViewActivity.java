package com.morrigan.m;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.morrigan.m.c.UserController;
import com.morrigan.m.login.LoginActivity;
import com.morrigan.m.main.MainActivity;


public class GuideViewActivity extends BaseActivity implements OnPageChangeListener {

    public static final String SPNAME = "";
    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    /**
     * 图片资源id
     */
    private int[] imgIdArray;

    private boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gudie_view);
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        findViewById(R.id.txt_enter).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                enter();
            }
        });

        // 载入图片资源ID
        imgIdArray = new int[]{R.drawable.gride_view1, R.drawable.gride_view2, R.drawable.gride_view3,R.drawable.gride_view4};

        // 将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = dip2px(this, 5);
            layoutParams.rightMargin = layoutParams.leftMargin;
            group.addView(imageView, layoutParams);
        }

        // 将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }

        // 设置Adapter
        viewPager.setAdapter(new MyAdapter());
        // 设置监听，主要是设置点点的背景
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        UserController.getInstance().setFirstLogin(this, false);
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position]);

        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewGroup) container).addView(mImageViews[position]);
            return mImageViews[position];
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        switch (arg0) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                flag = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                flag = true;
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !flag) {
                    enter();
                }
                flag = true;
                break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        if (arg0 == mImageViews.length - 1) {
            findViewById(R.id.txt_enter).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.txt_enter).setVisibility(View.GONE);
        }
        setImageBackground(arg0 % mImageViews.length);
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    private void enter() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
