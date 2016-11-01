package com.morrigan.m.device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.R;
import com.morrigan.m.Toolbar2Activity;
import com.morrigan.m.UiResult;

/**
 * 修改绑定设备的名字界面
 * Created by y on 2016/10/15.
 */
public class DeviceNameUpdateActivity extends Toolbar2Activity {

    private EditText editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_name_update);
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editView.setText(null);
            }
        });
        editView = (EditText) findViewById(R.id.edit);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmView.setEnabled(s.length() > 0);
            }
        });
        editView.setText(getIntent().getStringExtra("name"));
        editView.setSelection(editView.getText().length());
    }

    @Override
    protected void onClickConfirm() {
        String mac = getIntent().getStringExtra("mac");
        String name = editView.getText().toString().trim();
        UpdateDeviceNameTask task = new UpdateDeviceNameTask(this, mac, name);
        AsyncTaskCompat.executeParallel(task);
    }

    class UpdateDeviceNameTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private String mac;
        private String name;
        private ProgressDialog dialog;

        UpdateDeviceNameTask(Activity activity, String mac, String name) {
            this.activity = activity;
            this.mac = mac;
            this.name = name;
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
            return DeviceController.getInstance().modifyDeviceName(activity, mac, name);
        }

        @Override
        protected void onPostExecute(UiResult result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {
                finish();
            }
        }
    }
}
