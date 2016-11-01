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

import java.util.List;

public class DeviceActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<List<Device>>, DeviceAdapter.Listener {

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
            dialog.setMessage(activity.getString(R.string.deleting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            return DeviceController.getInstance().remove(activity, data.mac);
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
