package com.morrigan.m.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.main.MainActivity;
import com.squareup.picasso.Picasso;

/**
 * 设备绑定失败界面
 * Created by y on 2016/10/13.
 */
public class DeviceBindFailedActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_bind_failed);
        ImageView iconView = (ImageView) findViewById(R.id.icon);
        Picasso.with(this).load(R.drawable.device_scan_result_top).into(iconView);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getBooleanExtra("backGotoMain", true)) {
                    Intent intent = new Intent(DeviceBindFailedActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    public void onClickScan(View view) {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        intent.putExtra("backGotoMain", getIntent().getBooleanExtra("backGotoMain", true));
        intent.putExtra("stopGotoMain", getIntent().getBooleanExtra("stopGotoMain", true));
        startActivity(intent);
        finish();
    }
}
