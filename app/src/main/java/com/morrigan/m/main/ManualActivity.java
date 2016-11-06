package com.morrigan.m.main;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.c.MassageController;

/**
 * 手动按摩界面
 * Created by y on 2016/10/15.
 */
public class ManualActivity extends BaseActivity {

    private ManualView manualView;
    private View braLeftView;
    private View braRightView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        manualView = (ManualView) findViewById(R.id.manual);
        braLeftView = findViewById(R.id.bar_left);
        braLeftView.setActivated(true);
        braRightView = findViewById(R.id.bar_right);
        braRightView.setActivated(true);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickAddGear(View view) {
        if (manualView.addGear()) {
            if (manualView.isStart()) {
                BleController.getInstance().manualAsync(manualView.getGear(), getBar());
            }
        }
    }

    public void onClickDeleteGear(View view) {
        if (manualView.deleteGear()) {
            if (manualView.isStart()) {
                BleController.getInstance().manualAsync(manualView.getGear(), getBar());
            }
        }
    }

    public void onClickLeftBra(View view) {
        boolean activated = view.isActivated();
        if (activated && !braRightView.isActivated()) {
            ToastUtils.show(this, "同步操作开关至少一个被选中");
        } else {
            view.setActivated(!activated);
            if (manualView.isStart()) {
                BleController.getInstance().manualAsync(manualView.getGear(), getBar());
            }
        }
    }

    public void onClickRightBra(View view) {
        boolean activated = view.isActivated();
        if (activated && !braLeftView.isActivated()) {
            ToastUtils.show(this, "同步操作开关至少一个被选中");
        } else {
            view.setActivated(!activated);
            if (manualView.isStart()) {
                BleController.getInstance().manualAsync(manualView.getGear(), getBar());
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
            BleController.getInstance().manualAsync(manualView.getGear(), getBar());
        }
    }

    private void stop() {
        manualView.stop();
        BleController.getInstance().manualStopAsync();
        saveRecord();
    }

    private void showNoDeviceReady() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设备未连接");
        builder.setMessage("请先连接设备再来开始按摩吧");
        builder.setPositiveButton(R.string.action_confirm, null);
        builder.show();
    }

    private byte getBar() {
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
}
