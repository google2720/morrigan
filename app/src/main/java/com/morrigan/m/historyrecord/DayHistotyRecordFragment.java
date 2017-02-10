package com.morrigan.m.historyrecord;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morrigan.m.R;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.c.UserController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 当天记录
 * Created by fei on 2016/10/12.
 */
public class DayHistotyRecordFragment extends Fragment {
    private TextView txt_total_min_num;
    private TextView txt_total_min;
    private TextView txt_date;
    private TextView txt_goal_min;
    private TextView txt_nursing_min;
    private TextView txt_sulplus_min;
    private DayView dayView;
    private DataTask task;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_day, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dayView = (DayView) view.findViewById(R.id.dayView);
        txt_total_min = (TextView) view.findViewById(R.id.txt_total_min);
        txt_date = (TextView) view.findViewById(R.id.txt_date);
        txt_goal_min = (TextView) view.findViewById(R.id.txt_goal_min);
        txt_nursing_min = (TextView) view.findViewById(R.id.txt_nursing_min);
        txt_sulplus_min = (TextView) view.findViewById(R.id.txt_sulplus_min);
        txt_total_min_num=(TextView) view.findViewById(R.id.txt_total_min_num);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        txt_date.setText(sd.format(new Date()));
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private void loadData() {
        task = new DataTask();
        AsyncTaskCompat.executeParallel(task);
    }

    private class DataTask extends AsyncTask<Void, Void, TodayRecord> {

        private Context context;
        private String userId;

        private DataTask() {
            this.context = getActivity().getApplicationContext();
            this.userId = UserController.getInstance().getUserId(context);
        }

        @Override
        protected TodayRecord doInBackground(Void... voids) {
            return Massage.queryTodayHistory(context, userId);
        }

        @Override
        protected void onPostExecute(TodayRecord data) {
            if (getActivity() == null) {
                return;
            }
            int total = UserController.getInstance().getTargetInt(context);
            int nursing = 0;
            for (int i = 0; i < data.records.length; i++) {
                nursing = nursing + data.records[i];
            }

            // 目标值
            txt_goal_min.setText(getString(R.string.history_time, total));
            txt_goal_min.setCompoundDrawables(null, null, null, null);

            // 护养时间
            if (nursing > 0) {
                txt_total_min.setText(R.string.history_time1);
                txt_total_min.setCompoundDrawables(null, null, null, null);
                txt_total_min_num.setText(nursing+"");
                txt_nursing_min.setText(getString(R.string.history_time, nursing));
                txt_nursing_min.setCompoundDrawables(null, null, null, null);
            }else {
                txt_total_min_num.setVisibility(View.GONE);
            }

            // 剩余目标值
            txt_sulplus_min.setText(getString(R.string.history_time, Math.max(0, total - nursing)));
            txt_sulplus_min.setCompoundDrawables(null, null, null, null);

            refreshBarChart(data);
        }
    }

    private void refreshBarChart(TodayRecord data) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < data.records.length; i++) {
            list.add(data.records[i]);
        }
        dayView.refreshData(list);
    }
}
