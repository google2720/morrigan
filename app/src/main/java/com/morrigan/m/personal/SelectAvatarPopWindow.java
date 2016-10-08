package com.morrigan.m.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.github.yzeaho.popupwindow.BottomPushPopupWindow;
import com.morrigan.m.R;

/**
 * 选择头像对话框
 * Created by y on 2016/10/6.
 */
public class SelectAvatarPopWindow extends BottomPushPopupWindow<Void> {

    private Listener listener;

    public SelectAvatarPopWindow(Context context) {
        super(context, null);
    }

    public interface Listener {

        void onClickPickPhoto();

        void onClickCapture();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected View generateCustomView(Void o) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_avatar, null);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.pick_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onClickPickPhoto();
                }
            }
        });
        view.findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onClickCapture();
                }
            }
        });
        return view;
    }
}
