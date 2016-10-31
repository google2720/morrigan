package com.morrigan.m.main;

import android.content.Context;
import android.content.Intent;
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
import com.morrigan.m.UserController;

import okhttp3.FormBody;
import okhttp3.Request;

public class MainFragment extends Fragment {

    private Listener listener;
    private RankTask task;
    private BatteryView batteryView;
    private CenterView centerView;
    private StarView starView;

    public static MainFragment newInstance() {
        return new MainFragment();
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
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                startActivity(intent);
            }
        });
        batteryView = (BatteryView) view.findViewById(R.id.battery);
        centerView = (CenterView) view.findViewById(R.id.center);
        starView = (StarView) view.findViewById(R.id.star);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface Listener {
        void onMenuClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (task != null) {
            task.cancel(true);
        }
        task = new RankTask(getContext());
        AsyncTaskCompat.executeParallel(task);
        centerView.setGoal(UserController.getInstance().getTarget(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel(true);
            task = null;
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
            } catch (Exception e) {
                Lg.w("user", "failed to modify user info", e);
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
