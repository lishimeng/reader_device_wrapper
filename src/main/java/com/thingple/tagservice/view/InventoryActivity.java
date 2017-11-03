package com.thingple.tagservice.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.thingple.tagservice.DeviceApp;
import com.thingple.tagservice.R;
import com.thingple.tagservice.device.DeviceContext;


public class InventoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        getIntent().getExtras();
        doInventory(getApp(), getPower());
    }

    private DeviceContext getDeviceContext(final DeviceApp app) {
        DeviceContext deviceContext = null;
        if (app != null) {
            deviceContext = app.getDeviceContext();
        }
        return deviceContext;
    }

    private void doInventory(final DeviceApp app, final int power) {
        Bundle bundle = getIntent().getExtras();
        String filter = null;
        if (bundle != null) {
            filter = bundle.getString("filter");
            filter = filter == null || filter.trim().equals("") ? null : filter;
        }
        final String filterExp = filter;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceContext deviceContext = getDeviceContext(app);
                if (deviceContext != null) {
                    Log.d("inventory_view", "-->设备正常,开始Inventory");
                    deviceContext.inventoryStart(filterExp, power);
                    finish();
                } else {
                    Log.d("inventory_view", "等待设备启动");
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
}