package com.morrigan.m.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.ble.db.Massage;
import com.morrigan.m.c.UserController;
import com.morrigan.m.device.DeviceScanActivity;

import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

public class MainFragment extends Fragment implements CenterView.Callback {

    private Listener listener;
    private RankTask rankTask;
    private BatteryView batteryView;
    private CenterView centerView;
    private StarView starView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(centerView.getAm());
        }
    };
    private LoadDataTask loadDataTask;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public interface Listener {
        void onMenuClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainFragment.Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMenuClick();
            }
        });
        view.findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.knead_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManualActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.knead_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AutoActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.knead_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.morrigan.m.music.MusicActivity.class);
                startActivity(intent);
            }
        });
        batteryView = (BatteryView) view.findViewById(R.id.battery);
        centerView = (CenterView) view.findViewById(R.id.center);
        centerView.setCallback(this);
        starView = (StarView) view.findViewById(R.id.star);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAmChange(boolean am) {
        loadData(am);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        getContext().registerReceiver(receiver, filter);
    }

    private void loadData(boolean am) {
        if (loadDataTask != null) {
            loadDataTask.cancel(true);
        }
        loadDataTask = new LoadDataTask(getActivity(), am);
        AsyncTaskCompat.executeParallel(loadDataTask);
    }

    private class LoadDataTask extends AsyncTask<Void, Void, List<Massage>> {

        private Context context;
        private boolean am;

        private LoadDataTask(Context _context, boolean am) {
            context = _context.getApplicationContext();
            this.am = am;
        }

        @Override
        protected List<Massage> doInBackground(Void... params) {
            String userId = UserController.getInstance().getUserId(context);
            return Massage.queryToday(context, userId, am);
        }

        @Override
        protected void onPostExecute(List<Massage> result) {
            if (getActivity() == null) {
                return;
            }
            centerView.setMassageData(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRank();
        centerView.setGoal(UserController.getInstance().getTarget(getContext()));
        centerView.setDate(Calendar.getInstance());
        loadData(centerView.getAm());
    }

    private void loadRank() {
        if (rankTask != null) {
            rankTask.cancel(true);
        }
        rankTask = new RankTask(getContext());
        AsyncTaskCompat.executeParallel(rankTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
        if (rankTask != null) {
            rankTask.cancel(true);
            rankTask = null;
        }
        if (loadDataTask != null) {
            loadDataTask.cancel(true);
            loadDataTask = null;
        }
    }

    private class RankTask extends AsyncTask<Void, Void, UiResult<RankResult>> {

        private Context context;

        private RankTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected UiResult<RankResult> doInBackground(Void... params) {
            UiResult<RankResult> uiResult = new UiResult<>();
            try {
                String url = context.getString(R.string.host) + "/rest/moli/get-rank";
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", UserController.getInstance().getUserId(context));
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
                RankResult r = result.parse(RankResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                uiResult.t = r;
            } catch (Exception e) {
                Lg.w("user", "failed to get user rank", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult<RankResult> result) {
            if (result.success && result.t != null) {
                starView.setStar(result.t.rank);
            }
        }
    }
}
