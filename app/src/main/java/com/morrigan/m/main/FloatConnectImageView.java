package com.morrigan.m.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;

/**
 * 带显示连接状态动画的按钮
 * Created by y on 2016/11/17.
 */
public class FloatConnectImageView extends ImageButton {

    private static final String TAG = "FloatConnectImageView";
    private Runnable showConnectRunnable = new Runnable() {
        @Override
        public void run() {
            showConnect();
        }
    };
    private Runnable showNoConnectRunnable = new Runnable() {
        @Override
        public void run() {
            showNoConnect();
        }
    };
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBind) {
            post(showConnectRunnable);
        }

        @Override
        public void onBindDeviceFailed(int error) {
            post(showNoConnectRunnable);
        }

        @Override
        public void onBluetoothOff() {
            post(showNoConnectRunnable);
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            post(showNoConnectRunnable);
        }
    };
    private ObjectAnimator animator;

    public FloatConnectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BleController.getInstance().addCallback(cb);
        if (BleController.getInstance().isDeviceReady()) {
            post(showConnectRunnable);
        } else {
            post(showNoConnectRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BleController.getInstance().removeCallback(cb);
        removeCallbacks(showNoConnectRunnable);
        removeCallbacks(showConnectRunnable);
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i(TAG, "onWindowVisibilityChanged " + visibility);
    }

    private void showNoConnect() {
        animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        animator.setDuration(750);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    private void showConnect() {
        animator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f);
        animator.setDuration(150);
        animator.setInterpolator(new LinearInterpolator());
        animator.setAutoCancel(true);
        animator.start();
    }
}
