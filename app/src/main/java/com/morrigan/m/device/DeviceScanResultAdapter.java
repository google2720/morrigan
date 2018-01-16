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
 * 扫描设备的结果适配器
 * Created by y on 2016/10/12.
 */
public class DeviceScanResultAdapter extends RecyclerView.Adapter<DeviceScanResultAdapter.DeviceScanResultViewHolder> implements View.OnClickListener {

    private Activity activity;
    private Listener listener;
    private List<UiData> objects;

    public interface Listener {
        void onListItemClick(View v, UiData device);
    }

    DeviceScanResultAdapter(@NonNull Activity activity, @NonNull Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setData(List<UiData> t) {
        this.objects = t;
        notifyDataSetChanged();
    }

    @Override
    public DeviceScanResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.activity_device_scan_result_item, parent, false);
        DeviceScanResultViewHolder holder = new DeviceScanResultViewHolder(view);
        holder.numView = (TextView) view.findViewById(R.id.num);
        holder.nameView = (TextView) view.findViewById(R.id.name);
        holder.iconView = view.findViewById(R.id.icon);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeviceScanResultViewHolder holder, int position) {
        UiData device = objects.get(position);
        holder.itemView.setTag(device);
        holder.numView.setText(activity.getString(R.string.device_result_num, String.valueOf(position + 1)));
        holder.nameView.setText(device.name);
        holder.iconView.setVisibility(device.showIcon ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    @Override
    public void onClick(View v) {
        UiData device = (UiData) v.getTag();
        if (device != null) {
            listener.onListItemClick(v, device);
        }
    }

    static class DeviceScanResultViewHolder extends RecyclerView.ViewHolder {
        TextView numView;
        TextView nameView;
        View iconView;

        DeviceScanResultViewHolder(View view) {
            super(view);
        }
    }
}
