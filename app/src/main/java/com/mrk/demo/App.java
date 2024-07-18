package com.mrk.demo;

import android.app.Application;

import com.mrk.device.MrkDeviceManger;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MrkDeviceManger.INSTANCE.init(this,true);
    }
}
