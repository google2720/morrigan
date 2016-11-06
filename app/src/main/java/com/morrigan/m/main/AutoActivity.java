package com.morrigan.m.main;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.c.MassageController;

/**
 * 自动按摩界面
 * Created by y on 2016/10/18.
 */
public class AutoActivity extends BaseActivity {

    private AutoLayout autoLayout;
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            if (autoLayout.isStart()) {
                stop();
            }
        }

        @Override
        public void onBluetoothOff() {
            if (autoLayout.isStart()) {
                stop();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BleController.getInstance().addCallback(cb);
        setContentView(R.layout.activity_auto);
        autoLayout = (AutoLayout) findViewById(R.id.auto);
        findViewById(R.id.massage_soft).setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_SOFT, R.drawable.massage_soft_check)));
        findViewById(R.id.massage_wave).setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_WAVE, R.drawable.massage_wave_check)));
        findViewById(R.id.massage_dynamic).setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_DYNAMIC, R.drawable.massage_dynamic_check)));
        findViewById(R.id.massage_gently).setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_GENTLY, R.drawable.massage_gently_check)));
        findViewById(R.id.massage_intense).setOnTouchListener(new AutoTouchListener(this, new AutoItem(AutoItem.TYPE_INTENSE, R.drawable.massage_intense_check)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleController.getInstance().removeCallback(cb);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickStart(View view) {
        if (!BleController.getInstance().isDeviceReady()) {
            showNoDeviceReady();
            return;
        }
        if (autoLayout.isModeEmpty()) {
            ToastUtils.show(this, "请先选择组合模式");
            return;
        }
        boolean a = view.isActivated();
        view.setActivated(!a);
        if (a) {
            stop();
        } else {
            start();
        }
    }

    private void showNoDeviceReady() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设备未连接");
        builder.setMessage("请先连接设备再来开始按摩吧");
        builder.setPositiveButton(R.string.action_confirm, null);
        builder.show();
    }

    private void start() {
        autoLayout.start();
        BleController.getInstance().autoMassageAsync(autoLayout.getMode());
    }

    private void stop() {
        autoLayout.stop();
        BleController.getInstance().manualStopAsync();
        saveRecord();
    }

    @Override
    public void onBackPressed() {
        if (autoLayout.isStart()) {
            stop();
        }
        super.onBackPressed();
    }

    private void saveRecord() {
        String address = BleController.getInstance().getBindDeviceAddress();
        long startTime = autoLayout.getStartSystemTime();
        long endTime = autoLayout.getStopSystemTime();
        MassageController.getInstance().save(this, address, startTime, endTime);
    }

    public boolean onTouchDown() {
        if (autoLayout.isStart()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("正在按摩，不能拖动");
            builder.setPositiveButton(R.string.action_confirm, null);
            builder.show();
            return true;
        }
        return false;
    }
}
