package com.morrigan.m.device;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.squareup.picasso.Picasso;

public class DeviceScanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView iconView = (ImageView) findViewById(R.id.icon);
        Picasso.with(this).load(R.drawable.device_scan_top).into(iconView);
    }
}
