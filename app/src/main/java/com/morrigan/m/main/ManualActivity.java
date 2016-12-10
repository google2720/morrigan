package com.morrigan.m.main;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.c.MassageController;

/**
 * 手动按摩界面
 * Created by y on 2016/10/15.
 */
public class ManualActivity extends BaseActivity {

    private ManualView manualView;
    private View braLeftView;
    private View braRightView;
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (manualView.isStart()) {
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
                    if (manualView.isStart()) {
                        stop();
                    }
                }
            });
        }
    };
    private View startView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        manualView = (ManualView) findViewById(R.id.manual);
        braLeftView = findViewById(R.id.bar_left);
        braLeftView.setActivated(true);
        braRightView = findViewById(R.id.bar_right);
        braRightView.setActivated(true);
        startView = findViewById(R.id.start);
        BleController.getInstance().addCallback(cb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleController.getInstance().removeCallback(cb);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickAddGear(View view) {
        if (manualView.addGear()) {
            if (manualView.isStart()) {
                BleController.getInstance().manualMassageAsync(manualView.getGear(), getBarMode());
            }
        } else {
            ToastUtils.show(this, R.string.bra_max_gear);
        }
    }

    public void onClickDeleteGear(View view) {
        if (manualView.deleteGear()) {
            if (manualView.isStart()) {
                BleController.getInstance().manualMassageAsync(manualView.getGear(), getBarMode());
            }
        } else {
            ToastUtils.show(this, R.string.bra_min_gear);
        }
    }

    public void onClickLeftBra(View view) {
        boolean activated = view.isActivated();
        if (activated && !braRightView.isActivated()) {
            ToastUtils.show(this, R.string.bra_empty_tip);
        } else {
            view.setActivated(!activated);
            if (manualView.isStart()) {
                BleController.getInstance().manualMassageAsync(manualView.getGear(), getBarMode());
            }
        }
    }

    public void onClickRightBra(View view) {
        boolean activated = view.isActivated();
        if (activated && !braLeftView.isActivated()) {
            ToastUtils.show(this, R.string.bra_empty_tip);
        } else {
            view.setActivated(!activated);
            if (manualView.isStart()) {
                BleController.getInstance().manualMassageAsync(manualView.getGear(), getBarMode());
            }
        }
    }

    public void onClickStart(View view) {
        if (!BleController.getInstance().isDeviceReady()) {
            showNoDeviceReady();
            return;
        }
        boolean a = view.isActivated();
        view.setActivated(!a);
        if (a) {
            stop();
        } else {
            manualView.start();
            BleController.getInstance().manualMassageAsync(manualView.getGear(), getBarMode());
        }
    }

    private void stop() {
        startView.setActivated(false);
        manualView.stop();
        BleController.getInstance().massageStopAsync();
        saveRecord();
    }

    private void showNoDeviceReady() {
        ToastUtils.show(this, R.string.device_no_connect_tip);
    }

    private byte getBarMode() {
        boolean l = braLeftView.isActivated();
        boolean r = braRightView.isActivated();
        if (l && r) {
            return 0x00;
        } else if (l) {
            return 0x01;
        } else if (r) {
            return 0x02;
        } else {
            return 0x00;
        }
    }

    @Override
    public void onBackPressed() {
        if (manualView.isStart()) {
            stop();
        }
        super.onBackPressed();
    }

    private void saveRecord() {
        String address = BleController.getInstance().getBindDeviceAddress();
        long startTime = manualView.getStartSystemTime();
        long endTime = manualView.getStopSystemTime();
        MassageController.getInstance().save(this, address, startTime, endTime);
    }

    public void onClickScan(View view) {
        MassageController.getInstance().onClickConnect(this);
    }
}
