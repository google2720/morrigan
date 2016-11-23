/*
 * 文件名：CalendarSwitchPoPWindow.java
 * 创建人：fei
 * 创建时间：2015-12-22
 * 版     权：Copyright Easier Digital Tech. Co. Ltd. All Rights Reserved.
 */
package com.morrigan.m.music;

import android.app.Activity;
import android.content.Context;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.github.yzeaho.popupwindow.BottomPushPopupWindow;
import com.morrigan.m.R;

import java.util.List;

import com.morrigan.m.music.MusicLoader.MusicInfo;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author fei
 * @version [NMOA, 2015-12-22]
 */
public class MusicsPopupWindow extends BottomPushPopupWindow<Activity> {

    RecyclerView listView;
    Activity activity;
    MusicAdapter adapter;
    List<MusicLoader.MusicInfo> musicInfos;
    FlingDownImageView downImageView;

    public MusicsPopupWindow(Activity activity) {
        super(activity, activity);
    }

    public void setData(List<MusicLoader.MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
        adapter.setData(musicInfos);

    }

    public void setOpenPopup(OpenPopup openPopup) {
        downImageView.setOpenPopup(openPopup);
    }

    @Override
    protected View generateCustomView(Activity activity) {
        setOutsideTouchable(true);
        this.activity = activity;
        View root = View.inflate(context, R.layout.popup_musics, null);
        root.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        root.findViewById(R.id.ll_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        downImageView = (FlingDownImageView) root.findViewById(R.id.iv_top);
        intiListView(root);
        return root;
    }

    private void intiListView(View root) {
        listView = (RecyclerView) root.findViewById(R.id.rw_music);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.addItemDecoration(new com.morrigan.m.view.DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        adapter = new MusicAdapter(activity);
        adapter.setCallback((MusicAdapter.Callback) activity);
        listView.setAdapter(adapter);
    }

    public void setPlayIndex(int playIndex) {
        if (adapter == null) {
            return;
        }
        adapter.setPlayIndex(playIndex);
    }
}
