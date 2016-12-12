package com.morrigan.m.historyrecord;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.c.UserController;
import com.morrigan.m.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * 本周护养
 * Created by fei on 2016/10/12.
 */
public class WeekHistotyRecordFragment extends Fragment {

    private static final String TAG = "WeekHistotyRecordFragment";

    private WeekView weekView;
    private GetHistTask task;
    private HistoryResult data;
    private TextView txt_total_min;
    private TextView txt_date;
    private TextView txt_goal_min;
    private TextView txt_nursing_min;
    private TextView txt_sulplus_min;
    private TextView txt_average_nursing_min;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_week, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        weekView = (WeekView) view.findViewById(R.id.weekView);
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
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
        }
    }

    private void initData() {
        if (task != null) {
            task.cancel();
        }
        task = new GetHistTask(this.getActivity());
        AsyncTaskCompat.executeParallel(task);
    }

    public void refreshData() {
        SimpleDateFormat sd = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        String monday = DateUtils.getMondayOfThisWeek(sd);
        String sunday = DateUtils.getSundayOfThisWeek(sd);
        txt_date.setText(monday + "-" + sunday);
        if (data != null && data.hlInfo != null) {
            int nursing = 0;
            int size = data.hlInfo.size();
            for (int i = 0; i < size; i++) {
                final HlInfo info = data.hlInfo.get(i);
                if (info != null) {
                    nursing = nursing + info.timeLong;
                }
            }

            // 本周目标
            int target = UserController.getInstance().getTargetInt(getContext());
            int targetWeek = target * 7;
            txt_goal_min.setText(getString(R.string.history_time, targetWeek));
            txt_goal_min.setCompoundDrawables(null, null, null, null);

            // 剩余目标值
            txt_sulplus_min.setText(getString(R.string.history_time, Math.max(0, targetWeek - nursing)));
            txt_sulplus_min.setCompoundDrawables(null, null, null, null);

            // 本周护养
            if (nursing > 0) {
                txt_nursing_min.setText(getString(R.string.history_time, nursing));
                txt_nursing_min.setCompoundDrawables(null, null, null, null);
                txt_total_min.setText(getString(R.string.history_time, nursing));
                txt_total_min.setCompoundDrawables(null, null, null, null);
            }

            // 平均护养
            int average = (int) (nursing / 7.0);
            if (average != 0) {
                txt_average_nursing_min.setText(getString(R.string.history_time_average, average));
                txt_average_nursing_min.setCompoundDrawables(null, null, null, null);
            }
        }
        refreshBarChart();
    }

    private void refreshBarChart() {
        List<HlInfo> llInfo = new ArrayList<>();
        if (data != null) {
            llInfo = data.hlInfo;
        }
        if (llInfo == null) {
            llInfo = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                llInfo.add(new HlInfo());
            }
        }
        while (llInfo.size() < 7) {
            llInfo.add(new HlInfo());
        }
        List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < llInfo.size(); i++) {
            if (llInfo.get(i) == null) {
                datas.add(0);
            } else {
                datas.add(llInfo.get(i).timeLong);
            }
        }
        weekView.refreshData(datas);
    }

    class GetHistTask extends AsyncTask<Void, Void, UiResult<HistoryResult>> {

        private Activity activity;
        private HttpProxy proxy = new HttpProxy();

        GetHistTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected UiResult<HistoryResult> doInBackground(Void... params) {
            UiResult<HistoryResult> uiResult = new UiResult<>();
            try {
                String userId = UserController.getInstance().getUserId(activity);
                String url = activity.getString(R.string.host) + "/rest/moli/get-record-list";
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", userId);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                HistoryResult r = proxy.execute(activity, builder.build(), HistoryResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                if (uiResult.success) {
                    uiResult.t = r;
                    if (r.hlInfo.size() == 7) {
                        Calendar calendar = Calendar.getInstance();
                        int index = calendar.get(Calendar.DAY_OF_WEEK);
                        index = (index - 2 + 7) % 7;
                        r.hlInfo.get(index).timeLong = Massage.sum(activity, userId);
                    }
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to get history", e);
                uiResult.message = HttpProxy.parserError(activity, e);
            }
            return uiResult;
        }

        void cancel() {
            if (proxy != null) {
                proxy.cancel();
            }
            cancel(true);
        }

        @Override
        protected void onCancelled() {
            task = null;
        }

        @Override
        protected void onPostExecute(UiResult<HistoryResult> result) {
            task = null;
            data = result.t;
            refreshData();
        }
    }
}
