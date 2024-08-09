package com.mrk.demo;

import android.app.Application;

import com.mrk.device.MrkDeviceManger;


public class App extends Application {
    String sign = "Ov92oVQaZB4d8XIT2I9Bhd0/sg6YxBkJC+AyGSSjrQE1vT9286tiroJslO4eYTZLiebxKNQVWIw03dxPVOO76FcGZJJ2MHSqJvHgRRk2w+dIcjisz1gUiEEUktswF81H+ezDc93rd8c802zONh3ResNMdxUveKNcEMdlNh6M5vMzMRFT94KlUWtiNpPR48jHLIQsjwzpmTcLWqyAy5Gvqw==";
    @Override
    public void onCreate() {
        super.onCreate();
        MrkDeviceManger.INSTANCE.init(this,sign,true);
    }
}
