package com.morrigan.m.device;

import android.content.Context;

import com.morrigan.m.DataLoader;

import java.util.ArrayList;
import java.util.List;

import static com.morrigan.m.device.DeviceController.NOTIFY_URI;

/**
 * 设备信息加载器
 * Created by y on 2016/10/12.
 */
public class DeviceDataLoader extends DataLoader<List<Device>> {

    public DeviceDataLoader(Context context) {
        super(context, NOTIFY_URI);
    }

    @Override
    public List<Device> loadInBackground() {
        List<Device> dataList = new ArrayList<>();
        Device device = new Device();
        device.type = Device.TYPE_ADD;
        dataList.add(device);
        List<DeviceInfo> deviceInfoList = DeviceController.getInstance().load();
        if (deviceInfoList != null) {
            int i = 1;
            for (DeviceInfo d : deviceInfoList) {
                device = new Device();
                device.name = d.deviceName;
                device.mac = d.mac;
                device.type = Device.TYPE_DEVICE;
                device.num = i++;
                dataList.add(device);
            }
        }
        return dataList;
    }
}
