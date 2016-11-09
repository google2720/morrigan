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
 * Created by fei on 2016/10/12.
 */
public class DayHistotyRecordFragment extends Fragment {

    private static final String TAG = "DayHistotyRecordFragment";
    private TextView txt_total_min;
    private TextView txt_date;
    private TextView txt_goal_min;
    private TextView txt_nursing_min;
    private TextView txt_sulplus_min;
    private TextView txt_average_nursing_min;
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
        txt_average_nursing_min = (TextView) view.findViewById(R.id.txt_average_nursing_min);
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
            String str = UserController.getInstance().getTarget(context);
            int total = str == null ? 0 : Integer.parseInt(str);
            int nursing = 0;
            int sulplus = 0;
            for (int i = 0; i < data.records.length; i++) {
                nursing = nursing + data.records[i];
            }
            sulplus = total - nursing;
            if (total != 0) {
                txt_total_min.setText(total + "分钟");
                txt_goal_min.setText(total + "分钟");
                txt_total_min.setCompoundDrawables(null, null, null, null);
                txt_goal_min.setCompoundDrawables(null, null, null, null);
            }
            if (nursing != 0) {
                txt_nursing_min.setText(nursing + "分钟");
                txt_nursing_min.setCompoundDrawables(null, null, null, null);
            }
            if (sulplus != 0) {
                txt_sulplus_min.setText(sulplus + "分钟");
                txt_sulplus_min.setCompoundDrawables(null, null, null, null);
            }
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
