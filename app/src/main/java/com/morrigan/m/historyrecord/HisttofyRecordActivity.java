package com.morrigan.m.historyrecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2016/10/12.
 */

public class HisttofyRecordActivity extends ToolbarActivity {
    private View radio1;
    private View radio2;
    private MyViewPager mViewPager;
    private List<Fragment> fragments;
    private static final String TAG = "HisttofyRecordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        initView();
    }

    private void initView() {
        radio1 = findViewById(R.id.contact_radio1);
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDay();
                mViewPager.setCurrentItem(0);
            }
        });
        radio2 = findViewById(R.id.contact_radio2);
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeek();
                mViewPager.setCurrentItem(1);
            }
        });

        mViewPager = (MyViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    showDay();
                } else if (position == 1) {
                    showWeek();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fragments = new ArrayList<>();
        DayHistotyRecordFragment day = new DayHistotyRecordFragment();
        WeekHistotyRecordFragment week=new WeekHistotyRecordFragment();
        fragments.add(day);
        fragments.add(week);
        HisttofyRecordActivity.TabAdapter tabAdapter = new HisttofyRecordActivity.TabAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(tabAdapter);
        showDay();
        mViewPager.setCurrentItem(0);
    }

    private void showDay() {
        radio1.setActivated(true);
        radio2.setActivated(false);
        setTitle(R.string.history_title);
    }

    private void showWeek() {
        radio1.setActivated(false);
        radio2.setActivated(true);
        setTitle(R.string.history_title);
    }

    public static class TabAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }
    }
}
