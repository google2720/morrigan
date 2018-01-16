package com.bigkoo.pickerview.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.utils.PickerViewAnimateUtil;

/**
 * Created by Sai on 15/11/22.
 * 精仿iOSPickerViewController控件
 */
public abstract class BasePickerView {

    private Activity activity;
    private ViewGroup contentContainer;
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;//附加View 的 根View
    private OnDismissListener onDismissListener;
    private boolean dismissing;
    private Animation outAnim;
    private Animation inAnim;
    private boolean showing;
    private int gravity = Gravity.BOTTOM;
    private View outContainer;
    private boolean cancelable;
    private Handler handler = new Handler(Looper.getMainLooper());

    public BasePickerView(Activity activity) {
        this.activity = activity;
        initViews();
        init();
        initEvents();
    }

    private void initViews() {
        LayoutInflater inflater = activity.getLayoutInflater();
        decorView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) inflater.inflate(R.layout.layout_basepickerview, decorView, false);
        outContainer = rootView.findViewById(R.id.out_container);
        outContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cancelable && event.getAction() == MotionEvent.ACTION_DOWN) {
                    dismiss();
                }
                return false;
            }
        });
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
    }

    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    protected void initEvents() {
    }

    protected ViewGroup getContainer() {
        return contentContainer;
    }

    /**
     * show的时候调用
     *
     * @param view 这个View
     */
    private void onAttached(View view) {
        decorView.addView(view);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        outContainer.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
        contentContainer.startAnimation(inAnim);
    }

    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        showing = true;
        if (hideInputMethod(activity, activity.getWindow().getDecorView().getWindowToken())) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!activity.isFinishing()) {
                        onAttached(rootView);
                    }
                }
            }, activity.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        } else {
            onAttached(rootView);
        }
    }

    private boolean hideInputMethod(Context context, IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return im.hideSoftInputFromWindow(token, 0);
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return showing;
    }

    public void dismiss() {
        if (dismissing || !showing) {
            return;
        }

        dismissing = true;
        //消失动画
        outContainer.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 从activity根视图移除
                        decorView.removeView(rootView);
                        showing = false;
                        dismissing = false;
                        if (onDismissListener != null) {
                            onDismissListener.onDismiss(BasePickerView.this);
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        contentContainer.startAnimation(outAnim);
    }

    public Animation getInAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(activity, res);
    }

    public Animation getOutAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(activity, res);
    }

    public BasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public BasePickerView setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public View findViewById(int id) {
        return contentContainer.findViewById(id);
    }
}
