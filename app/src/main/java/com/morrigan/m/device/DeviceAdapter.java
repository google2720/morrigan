package com.morrigan.m.device;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morrigan.m.R;

import java.util.List;

/**
 * 选择系统通讯录联系人界面的列表适配器
 * Created by y on 2016/10/12.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements View.OnClickListener {

    private Activity activity;
    private Listener listener;
    private List<Device> objects;

    public interface Listener {

        void onListItemClick(View v, Device data);
    }

    DeviceAdapter(@NonNull Activity activity, @NonNull Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setData(List<Device> t) {
        this.objects = t;
        notifyDataSetChanged();
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Device.TYPE_ADD) {
            View view = activity.getLayoutInflater().inflate(R.layout.device_add_item, parent, false);
            DeviceViewHolder holder = new DeviceViewHolder(view);
            holder.itemView.setOnClickListener(this);
            return holder;
        } else {
            View view = activity.getLayoutInflater().inflate(R.layout.device_item, parent, false);
            DeviceViewHolder holder = new DeviceViewHolder(view);
            holder.numView = (TextView) view.findViewById(R.id.num);
            holder.nameView = (TextView) view.findViewById(R.id.name);
            holder.macView = (TextView) view.findViewById(R.id.mac);
            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return objects == null ? super.getItemViewType(position) : objects.get(position).type;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        Device data = objects.get(position);
        if (data.type == Device.TYPE_ADD) {
            holder.itemView.setTag(data);
        } else {
            holder.numView.setText(String.valueOf(data.num));
            holder.nameView.setText(data.name);
            holder.macView.setText(data.mac);
        }
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    @Override
    public void onClick(View v) {
        Device data = (Device) v.getTag();
        if (data != null) {
            listener.onListItemClick(v, data);
        }
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView macView;
        TextView numView;

        DeviceViewHolder(View view) {
            super(view);
        }
    }
}
