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
import com.morrigan.m.R;
import com.morrigan.m.SpacingDecoration;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.UiResult;
import com.morrigan.m.c.UserController;

import java.util.List;

public class DeviceActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<List<UiData>>, DeviceAdapter.Listener {

    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int padding = getResources().getDimensionPixelSize(R.dimen.device_padding);
        recyclerView.addItemDecoration(new SpacingDecoration(padding, padding, true));
        adapter = new DeviceAdapter(this, this);
        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DeviceController.getInstance().fetchAsync(this);
    }

    @Override
    public Loader<List<UiData>> onCreateLoader(int id, Bundle args) {
        return new DeviceDataLoader(this, UserController.getInstance().getUserId(this));
    }

    @Override
    public void onLoadFinished(Loader<List<UiData>> loader, List<UiData> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<UiData>> loader) {
    }

    @Override
    public void onListItemClick(View v, UiData data) {
        if (data.type == UiData.TYPE_ADD) {
            Intent intent = new Intent(this, DeviceScanActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onListItemUpdateClick(View v, UiData data) {
        Intent intent = new Intent(this, DeviceNameUpdateActivity.class);
        intent.putExtra("name", data.name);
        intent.putExtra("address", data.address);
        startActivity(intent);
    }

    @Override
    public void onListItemDeleteClick(View v, final UiData data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.device_remove);
        builder.setMessage(R.string.device_remove_msg);
        builder.setNegativeButton(R.string.action_no, null);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBind(data);
            }
        });
        builder.show();
    }

    private void removeBind(UiData data) {
        RemoveDeviceTask task = new RemoveDeviceTask(this, data);
        AsyncTaskCompat.executeParallel(task);
    }

    class RemoveDeviceTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private UiData data;
        private ProgressDialog dialog;

        RemoveDeviceTask(Activity activity, UiData data) {
            this.activity = activity;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.deleting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            return DeviceController.getInstance().remove(activity, data.address);
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
