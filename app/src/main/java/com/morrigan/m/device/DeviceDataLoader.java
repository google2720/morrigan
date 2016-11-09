package com.morrigan.m.device;

import android.content.Context;

import com.morrigan.m.DataLoader;
import com.morrigan.m.ble.db.Device;

import java.util.ArrayList;
import java.util.List;

import static com.morrigan.m.device.DeviceController.NOTIFY_URI;

/**
 * 设备信息加载器
 * Created by y on 2016/10/12.
 */
public class DeviceDataLoader extends DataLoader<List<UiData>> {

    private String userId;

    public DeviceDataLoader(Context context, String userId) {
        super(context, NOTIFY_URI);
        this.userId = userId;
    }

    @Override
    public List<UiData> loadInBackground() {
        List<UiData> dataList = new ArrayList<>();
        UiData device = new UiData();
        device.type = UiData.TYPE_ADD;
        dataList.add(device);
        List<Device> deviceList = DeviceController.getInstance().load(getContext(), userId);
        int i = 1;
        for (Device d : deviceList) {
            device = new UiData();
            device.name = d.name;
            device.address = d.address;
            device.type = UiData.TYPE_DEVICE;
            device.num = i++;
            dataList.add(device);
        }
        return dataList;
    }
}
