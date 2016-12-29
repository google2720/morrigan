package com.morrigan.m.main;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.c.MassageController;

import java.util.List;

/**
 * 自动按摩界面
 * Created by y on 2016/10/18.
 */
public class AutoActivity extends BaseActivity {

    private static final String TAG = "AutoActivity";
    private BleController ble = BleController.getInstance();
    private AutoLayout autoLayout;
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (autoLayout.isStart()) {
                        stop();
                    }
                }
            });
        }

        @Override
        public void onBluetoothOff() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (autoLayout.isStart()) {
                        stop();
                    }
                }
            });
        }
    };
    private View startView;
    private Handler handler = new Handler();
    private Runnable stopMassageRunnable = new Runnable() {
        @Override
        public void run() {
            massageExperience = false;
            ble.massageStopAsync();
        }
    };
    private int index;
    private int total;
    private List<AutoItem> autoItemList;
    private Runnable autoMassageSingleModeRunnable = new Runnable() {
        @Override
        public void run() {
            AutoItem autoItem = autoItemList.get(index++ % total);
            autoLayout.setAutoModeDrawable(autoItem.getDrawable(getApplicationContext()));
            ble.autoMassageSingleModeAsync(AutoItem.getMassageMode(autoItem.type));
            handler.postDelayed(autoMassageSingleModeRunnable, 6000);
        }
    };
    private boolean massageExperience;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ble.addCallback(cb);
        setContentView(R.layout.activity_auto);
        startView = findViewById(R.id.start);
        autoLayout = (AutoLayout) findViewById(R.id.auto);

        AutoImageView view = (AutoImageView) findViewById(R.id.massage_soft);
        view.setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_SOFT, R.drawable.massage_soft_check)));
        view.setOnClickListener(new AutoClickListener(this, AutoItem.TYPE_SOFT));

        view = (AutoImageView) findViewById(R.id.massage_wave);
        view.setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_WAVE, R.drawable.massage_wave_check)));
        view.setOnClickListener(new AutoClickListener(this, AutoItem.TYPE_WAVE));

        view = (AutoImageView) findViewById(R.id.massage_dynamic);
        view.setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_DYNAMIC, R.drawable.massage_dynamic_check)));
        view.setOnClickListener(new AutoClickListener(this, AutoItem.TYPE_DYNAMIC));

        view = (AutoImageView) findViewById(R.id.massage_gently);
        view.setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_GENTLY, R.drawable.massage_gently_check)));
        view.setOnClickListener(new AutoClickListener(this, AutoItem.TYPE_GENTLY));

        view = (AutoImageView) findViewById(R.id.massage_intense);
        view.setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_INTENSE, R.drawable.massage_intense_check)));
        view.setOnClickListener(new AutoClickListener(this, AutoItem.TYPE_INTENSE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }

    public void onClickScan(View view) {
        MassageController.getInstance().onClickConnect(this);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickStart(View view) {
        if (autoLayout.isEmpty()) {
            Lg.i(TAG, "onClickStart no choose auto mode");
            ToastUtils.show(this, R.string.massage_start_tip);
            return;
        }
        if (!ble.isDeviceReady()) {
            Lg.i(TAG, "onClickStart no device ready");
            showNoDeviceReady();
            return;
        }
        boolean a = view.isActivated();
        if (a) {
            Lg.i(TAG, "onClickStart stop");
            stop();
        } else {
            Lg.i(TAG, "onClickStart start");
            start();
        }
    }

    private void showNoDeviceReady() {
        ToastUtils.show(this, R.string.device_no_connect_tip);
    }

    private void start() {
        Lg.i(TAG, "start");
        startView.setActivated(true);
        autoLayout.start();
        handler.removeCallbacks(stopMassageRunnable);
        autoItemList = autoLayout.getAutoItemList();
        index = 0;
        total = autoItemList.size();
        handler.post(autoMassageSingleModeRunnable);
    }

    private void stop(boolean refreshUi) {
        Lg.i(TAG, "stop");
        handler.removeCallbacks(autoMassageSingleModeRunnable);
        handler.removeCallbacks(stopMassageRunnable);
        handler.post(stopMassageRunnable);
        if (refreshUi) {
            startView.setActivated(false);
        }
        autoLayout.stop(refreshUi);
        saveRecord();
    }

    private void stop() {
        stop(true);
    }

    @Override
    public void onBackPressed() {
        if (autoLayout.isStart()) {
            stop(false);
        }
        super.onBackPressed();
    }

    private void saveRecord() {
        String address = ble.getBindDeviceAddress();
        long startTime = autoLayout.getStartSystemTime();
        long endTime = autoLayout.getStopSystemTime();
        MassageController.getInstance().save(this, address, startTime, endTime);
    }

    public boolean onModeViewTouchDown() {
        if (autoLayout.isStart() || massageExperience) {
            ToastUtils.show(this, R.string.massage_no_drag_tip);
            return true;
        }
        return false;
    }

    public void onModeViewClick(View v, int type) {
        if (ble.isDeviceReady() && !autoLayout.isStart() && !massageExperience) {
            massageExperience = true;
            handler.removeCallbacks(stopMassageRunnable);
            handler.postDelayed(stopMassageRunnable, 3000);
            ble.autoMassageSingleModeAsync(AutoItem.getMassageMode(type));
        }
    }
}
