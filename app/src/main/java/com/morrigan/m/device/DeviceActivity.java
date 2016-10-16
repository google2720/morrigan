package com.morrigan.m.device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.SpacingDecoration;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

public class DeviceActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<List<Device>>, DeviceAdapter.Listener {

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int padding = getResources().getDimensionPixelSize(R.dimen.device_padding);
        recyclerView.addItemDecoration(new SpacingDecoration(padding, padding, true));
        adapter = new DeviceAdapter(this, this);
        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Device>> onCreateLoader(int id, Bundle args) {
        return new DeviceDataLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Device>> loader, List<Device> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Device>> loader) {
    }

    @Override
    public void onListItemClick(View v, Device data) {
        if (data.type == Device.TYPE_ADD) {
            Intent intent = new Intent(this, DeviceScanActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onListItemUpdateClick(View v, Device data) {
        Intent intent = new Intent(this, DeviceNameUpdateActivity.class);
        intent.putExtra("name", data.name);
        intent.putExtra("mac", data.mac);
        startActivity(intent);
    }

    @Override
    public void onListItemDeleteClick(View v, final Device data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否需要解绑");
        builder.setNegativeButton("否", null);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBind(data);
            }
        });
        builder.show();
    }

    private void removeBind(Device data) {
        RemoveDeviceTask task = new RemoveDeviceTask(this, data);
        AsyncTaskCompat.executeParallel(task);
    }

    class RemoveDeviceTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private Device data;
        private ProgressDialog dialog;

        RemoveDeviceTask(Activity activity, Device data) {
            this.activity = activity;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.changing));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            UiResult uiResult = new UiResult();
            try {
                String url = activity.getString(R.string.host) + "/rest/moli/remove-bind";
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", UserController.getInstance().getUserId(activity));
                b.add("mac", data.mac);
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(b.build());
                HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
                HttpResult r = result.parse(HttpResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
            } catch (Exception e) {
                Lg.w("user", "failed to remove device", e);
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
                adapter.remove(data);
            }
        }
    }
}
