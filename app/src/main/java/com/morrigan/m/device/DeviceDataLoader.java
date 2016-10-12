package com.morrigan.m.device;

import android.content.Context;

import com.morrigan.m.DataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息加载器
 * Created by y on 2016/10/12.
 */
public class DeviceDataLoader extends DataLoader<List<Device>> {

    public DeviceDataLoader(Context context) {
        super(context, null);
    }

    @Override
    public List<Device> loadInBackground() {
        List<Device> dataList = new ArrayList<>();
        Device device = new Device();
        device.type = Device.TYPE_ADD;
        dataList.add(device);

        device = new Device();
        device.type = Device.TYPE_DEVICE;
        device.num = 1;
        device.name = "xx";
        device.mac = "21729213921";
        dataList.add(device);

        device = new Device();
        device.type = Device.TYPE_DEVICE;
        device.num = 2;
        device.name = "xx";
        device.mac = "21729213921";
        dataList.add(device);

        return dataList;
    }
}
