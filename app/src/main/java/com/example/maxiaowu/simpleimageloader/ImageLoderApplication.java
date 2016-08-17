package com.example.maxiaowu.simpleimageloader;

import android.app.Application;
import android.content.Context;

/**
 * Created by xiaowu on 2016-8-17.
 */
public class ImageLoderApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }
}
