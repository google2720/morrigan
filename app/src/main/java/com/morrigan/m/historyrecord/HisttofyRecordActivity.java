package com.morrigan.m.historyrecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;
import com.morrigan.m.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by fei on 2016/10/12.
 */

public class HisttofyRecordActivity extends ToolbarActivity {
    private View radio1;
    private View radio2;
    private MyViewPager mViewPager;
    private List<Fragment> fragments;
    private static final String TAG = "HisttofyRecordActivity";
    GetHistTask getHistTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        initView();
        intiData();
    }

    private void intiData() {
        if (getHistTask != null) {
            getHistTask.cancel(true);
        }
        getHistTask = new GetHistTask(this);
        AsyncTaskCompat.executeParallel(getHistTask);
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
    }

    private void showDay() {
        radio1.setActivated(true);
        radio2.setActivated(false);
        setTitle(R.string.history_title_today);
    }

    private void showWeek() {
        radio1.setActivated(false);
        radio2.setActivated(true);
        setTitle(R.string.history_title_week);
    }

    class GetHistTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private HttpInterface.Result result;
        private ProgressDialog dialog;

        GetHistTask(Activity activity) {
            this.activity = activity;

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.history_record_ing_message));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            UiResult uiResult = new UiResult();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/get-record-list";
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", UserController.getInstance().getUserId(activity));
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                result = HttpInterface.Factory.create().execute(builder.build());
                HistoryResult r = result.parse(HistoryResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                if (uiResult.success) {
                    uiResult.t = r;
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to login", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {

                fragments = new ArrayList<>();
                DayHistotyRecordFragment day = new DayHistotyRecordFragment();
                WeekHistotyRecordFragment week = null;
                if (result.t != null) {
                    week = WeekHistotyRecordFragment.getIntance((HistoryResult) result.t);
                } else {
                    week = WeekHistotyRecordFragment.getIntance(null);
                }
                fragments.add(day);
                fragments.add(week);
                TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setOffscreenPageLimit(fragments.size());
                mViewPager.setAdapter(tabAdapter);
                showDay();
                mViewPager.setCurrentItem(0);


            }
        }
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
