package com.morrigan.m.music;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morrigan.m.R;

import java.util.List;
import com.morrigan.m.music.MusicLoader.MusicInfo;

/**
 * Created by fei on 2016/9/12.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {

    private Activity activity;
    private List<MusicLoader.MusicInfo> datas;
    private Callback callback;
    private int playIndex=-1;

    public int getPlayIndex() {
        return playIndex;
    }

    public void setPlayIndex(int playIndex) {
        this.playIndex = playIndex;
        this.notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public MusicAdapter(Activity activity) {
        this.activity = activity;
    }

    public interface Callback {
        void onListItemClick(View v, int index);
    }

    public void setData(List<MusicLoader.MusicInfo> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.music_popup_item, parent, false);
        Holder holder = new Holder(view);
        view.setTag(holder);
        holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
        holder.tv_artist = (TextView) view.findViewById(R.id.tv_artist);
        holder.flash = view.findViewById(R.id.flash);
        holder.rl_root = view.findViewById(R.id.rl_root);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        final MusicInfo data = datas.get(position);
        holder.rl_root.setTag(data);
        holder.tv_name.setText(data.getTitle());
        holder.tv_time.setText(MusicLoader.toTime(data.getDuration()));
        holder.tv_artist.setText("-  "+data.getArtist());
        if (position==playIndex){
            holder.tv_name.setTextColor(activity.getResources().getColor( R.color.c8c39e5));
            holder.tv_artist.setTextColor(activity.getResources().getColor( R.color.cffbe8ef2));
        }else {
            holder.tv_name.setTextColor(activity.getResources().getColor( R.color.c000000));
            holder.tv_artist.setTextColor(activity.getResources().getColor( R.color.c7E7879));
        }
        holder.rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    MusicLoader.MusicInfo data = (MusicLoader.MusicInfo) view.getTag();
                    callback.onListItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    static class Holder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_time;
        TextView tv_artist;
        View rl_root;
        View flash;

        Holder(View view) {
            super(view);
        }

    }
}