package com.mrk.demo;

import android.content.Context;
import android.content.res.AssetManager;

import com.cc.control.FastJsonUtilKt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * author  : ww
 * desc    :
 * time    : 2024/7/4 16:23
 */
public class LoadDataUtils {

    public static CourseDetailBean getCourseDetailBean(Context context) {
        String jsonString = loadJSONFromAsset(context, "data.json");
        return FastJsonUtilKt.vbToBean(jsonString, CourseDetailBean.class);
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
