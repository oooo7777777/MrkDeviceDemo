package com.mrk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrk.device.MrkDeviceManger;
import com.mrk.device.bean.BluetoothEnum;
import com.mrk.device.bean.DeviceMangerBean;
import com.mrk.device.bean.DeviceSearchBean;
import com.mrk.device.device.DeviceListener;
import com.mrk.device.listener.BluetoothSearchListener;
import com.mrk.device.listener.BluetoothStatusListener;
import com.mrk.device.listener.DeviceDisConnectListener;

public class MainActivity extends Activity {

    private TextView tvOpen;
    private ListView listView;
    private SearchDeviceAdapter adapter;

    private ProgressDialog loading;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        registerDeviceListener();
//        FitnessDeviceManager.getInstance();
    }


    private void initView() {
        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");
        loading.setCancelable(false);

        tvOpen = (TextView) findViewById(R.id.tvOpen);

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        adapter = new SearchDeviceAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceSearchBean item = adapter.getData().get(position);
                if (MrkDeviceManger.INSTANCE.isConnect(item.getMac())) {
                    Intent intent = new Intent(
                            MainActivity.this,
                            DeviceDetailsActivity.class
                    );
                    Bundle bundle = new Bundle();
                    bundle.putString(DeviceDetailsActivity.PRODUCT_ID, item.getProductId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    deviceConnect(item);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceSearchBean item = adapter.getData().get(position);
                MrkDeviceManger.INSTANCE.removeBondedDevice(item.getMac());
                adapter.getData().remove(item);
                adapter.notifyDataSetChanged();
                toast("设备配对信息清空");
                return false;
            }
        });

        findViewById(R.id.tvSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 101);
                } else {
                    startSearch();
                }
            }
        });

        ((TextView) findViewById(R.id.tvBluetoothStatus)).setText("蓝牙开关状态:" + (MrkDeviceManger.INSTANCE.getBluetoothStatus() ? "开启" : "关闭"));
        tvOpen.setText(MrkDeviceManger.INSTANCE.getBluetoothStatus() ? "关闭蓝牙" : "开启蓝牙");
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MrkDeviceManger.INSTANCE.getBluetoothStatus()) {
                    MrkDeviceManger.INSTANCE.closeBluetooth(MainActivity.this);
                } else {
                    MrkDeviceManger.INSTANCE.openBluetooth(MainActivity.this);
                }
            }
        });


        findViewById(R.id.tvSearchStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MrkDeviceManger.INSTANCE.stopSearch();
            }
        });
        findViewById(R.id.tvDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        DeviceDetailsActivity.class
                );

                //根据可以连接的设备,自己更改对应的参数
                Bundle bundle = new Bundle();
                bundle.putString(DeviceDetailsActivity.MAC, "57:4C:54:6D:24:8C");
                bundle.putString(DeviceDetailsActivity.PRODUCT_ID, "1");
                bundle.putString(DeviceDetailsActivity.BLUETOOTH_NAME, "MRK-S16-248C");
                bundle.putString(DeviceDetailsActivity.MODEL_ID, "1744599596504129559");
                bundle.putString(DeviceDetailsActivity.CHARACTERISTIC_VALUE, "");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * 监听所有设备连接的对象
     */
    private void registerDeviceListener() {
        MrkDeviceManger.INSTANCE.registerBluetoothStateListener(new BluetoothStatusListener() {
            @Override
            public void onBluetoothStatus(BluetoothEnum bluetoothEnum) {
                ((TextView) findViewById(R.id.tvBluetoothStatus)).setText("蓝牙开关状态:" + bluetoothEnum.name());

                if (bluetoothEnum == BluetoothEnum.OPEN) {
                    tvOpen.setText("关闭蓝牙");

                } else {
                    tvOpen.setText("开启蓝牙");
                }


            }
        }).registerDeviceListener(this, new DeviceListener() {
            @Override
            public void onConnectStatus(boolean isAutoReconnect, DeviceMangerBean bean) {
                switch (bean.getConnectEnum()) {
                    case ON:
                        toast("连接成功");
                        loading.dismiss();
                        break;
                    case OFF:
                        toast("断开连接");
                        loading.dismiss();
                        break;
                    case ING:
                        loading.show();
                        break;
                    case ERROR:
                        toast("连接失败");
                        loading.dismiss();
                        break;
                    default:
                        break;
                }
                adapter.refresh(bean);
            }
        });
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        MrkDeviceManger.INSTANCE.startSearch(this, new BluetoothSearchListener() {
            @Override
            public void onSearchStatus(BluetoothEnum bluetoothEnum) {
                ((TextView) findViewById(R.id.tvSearchStatus)).setText("设备搜索状态:" + bluetoothEnum.name());
                if (bluetoothEnum == BluetoothEnum.STOP) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchDevice(DeviceSearchBean bean) {
                bean.setConnectEnum(MrkDeviceManger.INSTANCE.getDeviceStatus(bean.getMac()));
                adapter.addData(bean);
            }
        });
    }


    /**
     * 设备连接
     */
    private void deviceConnect(final DeviceSearchBean bean) {
        //一个设备类型只能连接一台设备,连接前请先判断有设备是否连接
        if (MrkDeviceManger.INSTANCE.isConnect(bean.getProductId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("已有相同类型设备连接,是否断开已连接设备再连接?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MrkDeviceManger.INSTANCE.disConnect(bean.getProductId(), new DeviceDisConnectListener() {
                        @Override
                        public void onSuccess() {
                            deviceConnect(bean);
                        }
                    });
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            MrkDeviceManger.INSTANCE.create(this, bean).autoConnectAlways().connect();
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
