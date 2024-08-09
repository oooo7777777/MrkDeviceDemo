package com.mrk.demo;

import android.app.Application;

import com.mrk.device.MrkDeviceManger;


public class App extends Application {
    String sign = "9yBhYFw9eav5evuVufkuSQwAbM3rmsuP7UgzFkLElPEo6aXwIJPuNwnn7X7yLyfWPnje42y0lMa/PWu+tgfmSlcGZJJ2MHSqJvHgRRk2w+cLozqbzqjHEWkaE1PIadE5+ezDc93rd8c802zONh3ResNMdxUveKNcEMdlNh6M5vNKod5gilqePVkoo5pF5pXRFuaCQ7usop+poSSbHY6IQw==";
    @Override
    public void onCreate() {
        super.onCreate();
        MrkDeviceManger.INSTANCE.init(this,sign,true);
    }
}
