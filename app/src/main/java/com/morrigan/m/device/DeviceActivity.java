package com.morrigan.m.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.morrigan.m.R;
import com.morrigan.m.SpacingDecoration;
import com.morrigan.m.ToolbarActivity;

import java.util.List;

public class DeviceActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<List<Device>>, DeviceAdapter.Listener {

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int padding = getResources().getDimensionPixelSize(R.dimen.device_padding);
        recyclerView.addItemDecoration(new SpacingDecoration(padding, padding, true));
        adapter = new DeviceAdapter(this, this);
        recyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Device>> onCreateLoader(int id, Bundle args) {
        return new DeviceDataLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Device>> loader, List<Device> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Device>> loader) {
    }

    @Override
    public void onListItemClick(View v, Device data) {
        if (data.type == Device.TYPE_ADD) {
            Intent intent = new Intent(this, DeviceScanActivity.class);
            startActivity(intent);
        }
    }
}
