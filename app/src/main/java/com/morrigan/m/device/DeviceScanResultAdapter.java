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
public class DeviceScanResultAdapter extends RecyclerView.Adapter<DeviceScanResultAdapter.DeviceScanResultViewHolder> implements View.OnClickListener {

    private Activity activity;
    private Listener listener;
    private List<Device> objects;

    public interface Listener {
    }

    DeviceScanResultAdapter(@NonNull Activity activity, @NonNull Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setData(List<Device> t) {
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
        Device device = objects.get(position);
        holder.numView.setText(activity.getString(R.string.device_result_num, String.valueOf(position)));
        holder.nameView.setText(device.name);
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    @Override
    public void onClick(View v) {
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