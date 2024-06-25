package com.mrk.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cc.control.bean.DeviceTrainBO;
import com.cc.control.protocol.DeviceConstants;
import com.mrk.device.MrkDeviceManger;
import com.mrk.device.bean.DeviceMangerBean;
import com.mrk.device.bean.DeviceTreadmillEnum;
import com.mrk.device.device.DeviceControl;
import com.mrk.device.device.DeviceListener;

/**
 * author  : ww
 * desc    :
 * time    : 2024/5/28 13:41
 */
public class DeviceDetailsActivity extends Activity implements View.OnClickListener {

    private Button btConnect, btDisConnect, btStart, btPause, btDataClear, btRegisterNotify, btUnRegisterNotify, btResistance, btSpeed, btSlope;
    private EditText etResistance, etSpeed, etSlope;
    private TextView tvContent, tvResistance, tvSpeed, tvSlope;

    public static final String PRODUCT_ID = "productId";

    private ProgressDialog loading;

    private String productId;

    private DeviceControl deviceControl;

    private final DeviceListener deviceListener = new DeviceListener() {
        @Override
        public void onConnectStatus(boolean isAutoReconnect, DeviceMangerBean bean) {
            ((TextView) findViewById(R.id.tvConnectStatus)).setText("连接状态\n" + MrkDeviceManger.INSTANCE.getTypeName(productId) + " 连接状态:" + bean.getConnectEnum());


            switch (bean.getConnectEnum()) {
                case ON:
                    loading.dismiss();
                    break;
                case OFF:
                    loading.dismiss();
                    break;
                case ING:
                    loading.setTitle("连接中...");
                    loading.show();
                    break;
                case ERROR:
                    loading.dismiss();
                    break;
            }
        }

        @Override
        public void onDeviceTreadmillStatus(DeviceTreadmillEnum status) {
            ((TextView) findViewById(R.id.tvDeviceStatus)).setText("跑步机状态:" + status);

            switch (status) {
                case COUNT_TIME:
                    loading.setTitle("跑步机启动中");
                    loading.show();
                    break;
                case SLOW_DOWN:
                    loading.setTitle("跑步机减速中");
                    loading.show();
                    break;
                case START:
                    loading.dismiss();
                    if (deviceControl != null) {
                        deviceControl.deviceStart();
                    }
                    break;
                default:
                    loading.dismiss();
                    break;
            }
        }

        @Override
        public void onNotifyData(DeviceTrainBO bean) {
            tvContent.setText("数据回调\n" + bean.toString());
            tvResistance.setText(Integer.toString(bean.getDrag()));
            tvSpeed.setText(Integer.toString((int) bean.getSpeed()));
            tvSlope.setText(Integer.toString(bean.getGradient()));
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);


        etResistance = (EditText) findViewById(R.id.etResistance);
        etSpeed = (EditText) findViewById(R.id.etSpeed);
        etSlope = (EditText) findViewById(R.id.etSlope);


        tvContent = (TextView) findViewById(R.id.tvContent);
        tvResistance = (TextView) findViewById(R.id.tvResistance);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvSlope = (TextView) findViewById(R.id.tvSlope);


        btConnect = (Button) findViewById(R.id.btConnect);
        btConnect.setOnClickListener(this);

        btDisConnect = (Button) findViewById(R.id.btDisConnect);
        btDisConnect.setOnClickListener(this);

        btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(this);

        btPause = (Button) findViewById(R.id.btPause);
        btPause.setOnClickListener(this);

        btDataClear = (Button) findViewById(R.id.btDataClear);
        btDataClear.setOnClickListener(this);

        btRegisterNotify = (Button) findViewById(R.id.btRegisterNotify);
        btRegisterNotify.setOnClickListener(this);

        btUnRegisterNotify = (Button) findViewById(R.id.btUnRegisterNotify);
        btUnRegisterNotify.setOnClickListener(this);

        btResistance = (Button) findViewById(R.id.btResistance);
        btResistance.setOnClickListener(this);

        btSpeed = (Button) findViewById(R.id.btSpeed);
        btSpeed.setOnClickListener(this);

        btSlope = (Button) findViewById(R.id.btSlope);
        btSlope.setOnClickListener(this);

        productId = getIntent().getStringExtra(PRODUCT_ID);

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        initDevice();
    }

    private void initDevice() {
        if (deviceControl == null) {

            ((TextView) findViewById(R.id.tvConnectStatus)).setText("连接状态\n" + MrkDeviceManger.INSTANCE.getTypeName(productId) + " 连接状态:" + MrkDeviceManger.INSTANCE.getDeviceStatus(productId));
            DeviceMangerBean bean = MrkDeviceManger.INSTANCE.getDeviceMangerBean(productId);
            if (bean.getConnectBean() != null) {

                ((TextView) findViewById(R.id.tvConnectInfo)).setText("设备信息\n" + bean.toString());
                //连接设备是否为跑步机
                if (bean.getConnectBean().getProductId().equals(DeviceConstants.D_TREADMILL)) {
                    findViewById(R.id.llTreadmill).setVisibility(View.VISIBLE);

                }
            }
            //是否支持设置坡度
            if (bean.getDeviceDetails() != null) {
                if (bean.getDeviceDetails().getProductModelTsl().getControlSlope() == 1) {
                    findViewById(R.id.llSlope).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.llSlope).setVisibility(View.GONE);
                }
            }
            deviceControl = MrkDeviceManger.INSTANCE.create(this, productId)
                    .setOnDeviceListener(deviceListener)
                    .registerDevice();
        }
        deviceControl.autoConnect();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btConnect:
                connect();
                break;
            case R.id.btDisConnect:
                deviceControl.disConnect();
                break;
            case R.id.btDataClear:
                deviceControl.clearData();
                break;
            case R.id.btRegisterNotify:
                deviceControl.setNotifyData(true);
                break;
            case R.id.btUnRegisterNotify:
                deviceControl.setNotifyData(false);
                break;
            case R.id.btResistance:
                deviceControl.sendCommandResistance(Integer.parseInt(etResistance.getText().toString()));
                break;
            case R.id.btStart:
                deviceControl.deviceStart();
                break;
            case R.id.btPause:
                deviceControl.devicePause();
                break;
            case R.id.btSpeed:
                //跑步机只能发送组合指令,即不论发送坡度还是速度,都需要一起
                deviceControl.sendCommandTreadmill(Integer.parseInt(etSpeed.getText().toString()), Integer.parseInt(etSlope.getText().toString()));
            case R.id.btSlope:
                if (deviceControl.getDeviceMangerBean().getConnectBean().getProductId().equals(DeviceConstants.D_TREADMILL)) {
                    //跑步机只能发送组合指令,即不论发送坡度还是速度,都需要一起
                    deviceControl.sendCommandTreadmill(Integer.parseInt(etSpeed.getText().toString()), Integer.parseInt(etSlope.getText().toString()));
                } else {
                    deviceControl.sendCommandSlope(Integer.parseInt(etSlope.getText().toString()));
                }
                break;
        }
    }

    private void connect() {
        deviceControl.connect();
        deviceControl.autoConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDevice();
    }
}
