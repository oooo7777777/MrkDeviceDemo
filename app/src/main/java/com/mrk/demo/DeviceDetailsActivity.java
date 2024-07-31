package com.mrk.demo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.control.bean.DeviceTrainBO;
import com.cc.control.protocol.DeviceConstants;
import com.mrk.device.MrkDeviceManger;
import com.mrk.device.bean.DeviceMangerBean;
import com.mrk.device.bean.DeviceTreadmillEnum;
import com.mrk.device.device.DeviceControl;
import com.mrk.device.device.DeviceListener;

import java.util.HashMap;

/**
 * author  : ww
 * desc    :
 * time    : 2024/5/28 13:41
 */
public class DeviceDetailsActivity extends Activity implements View.OnClickListener {

    public static final String PRODUCT_ID = "productId";
    public static final String MAC = "mac";
    public static final String BLUETOOTH_NAME = "bluetoothName";
    public static final String MODEL_ID = "modelId";
    public static final String CHARACTERISTIC_VALUE = "characteristicValue";

    private String productId, mac, bluetoothName, modelId, characteristicValue;


    private Button btConnect, btDisConnect, btStart, btPause, btDataClear, btRegisterNotify, btUnRegisterNotify, btResistance, btSpeed, btSlope, btVideoDemo;
    private EditText etResistance, etSpeed, etSlope;
    private TextView tvContent, tvResistance, tvSpeed, tvSlope, tvCourseInfo, tvPlayTime, tvGear;

    private ProgressBar progressBar;

    //课程父类合集
    HashMap parentMap = new HashMap<String, CourseCatalogue>();
    //课程子类合集
    HashMap childMap = new HashMap<String, CourseLink>();

    private int progress = 0; // 当前进度
    private final int maxProgress = 1493; // 最大进度
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (progress < maxProgress) {
                tvPlayTime.setText(progress + "秒");
                CourseCatalogue parentBean = (CourseCatalogue) parentMap.get(progress);
                if (parentBean != null) {
                    tvCourseInfo.setText(parentBean.getName() + "  ");
                }

                CourseLink childBean = (CourseLink) childMap.get(progress);
                if (parentBean != null) {
                    tvCourseInfo.setText(tvCourseInfo.getText().toString() + childBean.getName());
                    Toast.makeText(DeviceDetailsActivity.this, "阻力调节至:" + childBean.getAdviseNum(), Toast.LENGTH_SHORT).show();
                    tvGear.setText("消耗Kcal:" + childBean.getKcal() + " 最小踏频:" + childBean.getMinNum() + " 最大踏频:" + childBean.getMaxNum());
                    deviceControl.sendCommandResistance(childBean.getAdviseNum());
                }

                progress++;
                progressBar.setProgress(progress);

            }
            // 每1000毫秒（1秒）调用一次
            handler.postDelayed(this, 1000);
        }
    };


    private ProgressDialog loading;


    private DeviceControl deviceControl;

    private final DeviceListener deviceListener = new DeviceListener() {
        @Override
        public void onConnectStatus(boolean isAutoReconnect, DeviceMangerBean bean) {
            ((TextView) findViewById(R.id.tvConnectStatus)).setText("连接状态\n" + MrkDeviceManger.INSTANCE.getTypeName(productId) + " 连接状态:" + bean.getConnectEnum());


            switch (bean.getConnectEnum()) {
                case ON:
                    loading.dismiss();
                    initDevice();
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
        tvCourseInfo = (TextView) findViewById(R.id.tvCourseInfo);
        tvPlayTime = (TextView) findViewById(R.id.tvPlayTime);
        tvGear = (TextView) findViewById(R.id.tvGear);


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


        btVideoDemo = (Button) findViewById(R.id.btVideoDemo);
        btVideoDemo.setOnClickListener(this);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btResistance = (Button) findViewById(R.id.btResistance);
        btResistance.setOnClickListener(this);

        btSpeed = (Button) findViewById(R.id.btSpeed);
        btSpeed.setOnClickListener(this);

        btSlope = (Button) findViewById(R.id.btSlope);
        btSlope.setOnClickListener(this);


        findViewById(R.id.btAutoConnect).setOnClickListener(this);
        findViewById(R.id.btAutoConnectAlways).setOnClickListener(this);
        findViewById(R.id.btAutoConnectClose).setOnClickListener(this);

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        productId = getIntent().getStringExtra(PRODUCT_ID);
        mac = getIntent().getStringExtra(MAC);
        bluetoothName = getIntent().getStringExtra(BLUETOOTH_NAME);
        modelId = getIntent().getStringExtra(MODEL_ID);
        characteristicValue = getIntent().getStringExtra(CHARACTERISTIC_VALUE);

        if (productId.equals(DeviceConstants.D_BICYCLE)) {
            //模拟视频教案播放,只提供动感单车的,主要是提供下教案示例,具体逻辑需要自己实现
            initCourseDetail();
        }

        if (MrkDeviceManger.INSTANCE.isConnect(productId)) {
            deviceControl = MrkDeviceManger.INSTANCE.getDeviceControl(this, productId)
                    .setOnDeviceListener(deviceListener);
        } else {
            deviceControl = MrkDeviceManger.INSTANCE.getDeviceControl(this, mac, productId, bluetoothName, modelId, characteristicValue)
                    .setOnDeviceListener(deviceListener);
        }
    }

    private void initDevice() {

        ((TextView) findViewById(R.id.tvConnectStatus)).setText("连接状态\n" + MrkDeviceManger.INSTANCE.getTypeName(productId) + " 连接状态:" + MrkDeviceManger.INSTANCE.getDeviceStatus(productId));
        DeviceMangerBean bean = MrkDeviceManger.INSTANCE.getDeviceMangerBean(productId);

        if (bean == null)
            return;

        ((TextView) findViewById(R.id.tvConnectInfo)).setText("设备信息\n" + bean.getDeviceDetails());
        //连接设备是否为跑步机
        if (bean.getConnectBean().getProductId().equals(DeviceConstants.D_TREADMILL)) {
            findViewById(R.id.llTreadmill).setVisibility(View.VISIBLE);
        }

        if (bean.getDeviceDetails() != null) {
            //是否支持设置坡度
            if (bean.getDeviceDetails().getProductModelTsl().getControlSlope() == 1) {
                findViewById(R.id.llSlope).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.llSlope).setVisibility(View.GONE);
            }
            //是否支持阻力
            if (bean.getDeviceDetails().getProductModelTsl().getControlResistance() == 1) {
                findViewById(R.id.llResistance).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.llResistance).setVisibility(View.GONE);
            }

            //是否支持设备清零
            if (bean.getDeviceDetails().getProductModelTsl().isClean() == 1) {
                findViewById(R.id.btDataClear).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.btDataClear).setVisibility(View.GONE);
            }
        }


    }

    private void initCourseDetail() {
        findViewById(R.id.llVideoDemo).setVisibility(View.VISIBLE);
        CourseDetailBean courseDetailBean = LoadDataUtils.getCourseDetailBean(this);
        for (int i = 0; i < courseDetailBean.getCourseCatalogue().size(); i++) {
            CourseCatalogue parentBean = courseDetailBean.getCourseCatalogue().get(i);
            parentMap.put(parentBean.getBeginTime(), parentBean);

            for (int j = 0; j < parentBean.getCourseLinks().size(); j++) {
                CourseLink childBean = parentBean.getCourseLinks().get(j);
                childMap.put(childBean.getBeginTime(), childBean);
            }
        }
    }

    public void videoDemo() {
        progressBar.setMax(maxProgress);
        handler.post(runnable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btConnect:
                deviceControl.connect();
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
                break;
            case R.id.btSlope:
                if (deviceControl.getDeviceMangerBean().getConnectBean().getProductId().equals(DeviceConstants.D_TREADMILL)) {
                    //跑步机只能发送组合指令,即不论发送坡度还是速度,都需要一起
                    deviceControl.sendCommandTreadmill(Integer.parseInt(etSpeed.getText().toString()), Integer.parseInt(etSlope.getText().toString()));
                } else {
                    deviceControl.sendCommandSlope(Integer.parseInt(etSlope.getText().toString()));
                }
                break;
            case R.id.btVideoDemo:
                //模拟视频教案播放,只提供动感单车的,主要是提供下教案示例,具体逻辑需要自己实现
                if (productId.equals(DeviceConstants.D_BICYCLE)) {
                    videoDemo();
                } else {
                    Toast.makeText(this, "模拟视频教案播放,只提供动感单车的,主要是提供下教案示例,具体逻辑需要自己实现", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btAutoConnect:
                deviceControl.autoConnect();
                break;

            case R.id.btAutoConnectAlways:
                deviceControl.autoConnectAlways();
                break;

            case R.id.btAutoConnectClose:
                deviceControl.unAutoConnect();
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initDevice();

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        deviceControl.clear();
        super.onDestroy();
    }
}
