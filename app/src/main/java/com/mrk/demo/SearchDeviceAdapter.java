package com.mrk.demo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrk.device.bean.DeviceMangerBean;
import com.mrk.device.bean.DeviceSearchBean;

import java.util.ArrayList;
import java.util.List;


/**
 * author  : ww
 * desc    :
 * time    : 2024/6/12 16:29
 */
public class SearchDeviceAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceSearchBean> dataList = new ArrayList<>();

    private ConnectClickListener connectClickListener;

    public SearchDeviceAdapter(Context context, ConnectClickListener connectClickListener) {
        this.context = context;
        this.connectClickListener = connectClickListener;
    }

    public List<DeviceSearchBean> getData() {
        return dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_search_item, parent, false);
        }

        final DeviceSearchBean bean = dataList.get(position);

        TextView tvBluetoothName = (TextView) convertView.findViewById(R.id.tvBluetoothName);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvMac = (TextView) convertView.findViewById(R.id.tvMac);
        ImageView ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
        TextView tvConnect = (TextView) convertView.findViewById(R.id.tvConnect);


        tvName.setText(bean.getModelName());
        tvBluetoothName.setText(bean.getBluetoothName());
        tvMac.setText(bean.getMac());
        Glide.with(context).load(bean.getCover()).into(ivCover);
        tvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectClickListener.onClick(bean);
            }
        });

        switch (bean.getConnectEnum()) {
            case ON:
                tvConnect.setText("已连接");
                break;
            case ING:
                tvConnect.setText("连接中");
                break;
            default:
                tvConnect.setText("连接");
        }
        return convertView;
    }

    public void addData(DeviceSearchBean bean) {
        dataList.add(bean);
        notifyDataSetChanged();
    }

    public void refresh(DeviceMangerBean bean) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getMac().equals(bean.getConnectBean().getMac())) {
                dataList.get(i).setConnectEnum(bean.getConnectEnum());
                notifyDataSetChanged();
                break;
            }
        }
    }
}

