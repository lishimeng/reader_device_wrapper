package com.thingple.tagservice.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thingple.tagservice.DeviceApp;
import com.thingple.tagservice.R;
import com.thingple.tagservice.ReadCardListener;
import com.thingple.tagservice.device.DeviceContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReadCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card);

        DeviceApp app = this.getApp();
        doReadCard(app, getPower());
    }

    public void cancelInventory(View view) {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    private void stopInventory() {

        DeviceContext deviceContext = getDeviceContext(getApp());
        if (deviceContext != null) {
            deviceContext.inventoryStop();
        }
    }

    private ReadCardListener createListener() {

        return new ReadCardListener() {
            private AtomicBoolean flag = new AtomicBoolean(false);

            @Override
            public void onReadCard(String tid, String epc) {

                if (!flag.getAndSet(true)) {// 只返回一次
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("TID", tid);
                    bundle.putString("EPC", epc);
                    intent.putExtras(bundle);
                    setResult(9, intent);
                    Log.d(getClass().getName() + "#onReadCard", "返回数据并关闭界面");
                    finish();
                }
            }
        };
    }

    private DeviceContext getDeviceContext(final DeviceApp app) {
        DeviceContext deviceContext = null;
        if (app != null) {
            deviceContext = app.getDeviceContext();
        }
        return deviceContext;
    }

    private void doReadCard(final DeviceApp app, final int power) {
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
                    TextView lableStatus = (TextView) findViewById(R.id.label_read_status);
                    lableStatus.setText(R.string.status_reading);
                    ReadCardListener listener = createListener();
                    deviceContext.inventoryOnce(listener, filterExp, power);
                } else {
                    TextView lableStatus = (TextView) findViewById(R.id.label_read_status);
                    lableStatus.setText(R.string.status_initial);
                    handler.postDelayed(this, 500);
                }
            }
        }, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopInventory();
    }
}