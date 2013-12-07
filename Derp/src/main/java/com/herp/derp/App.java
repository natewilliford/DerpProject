package com.herp.derp;

import android.app.Application;
import android.util.Log;

/**
 * Created by nathan on 11/25/13.
 */
public class App extends Application {

    public static final String PACKAGE_NAME = "com.herp.derp";

    private static App instance;

    private static BitstampData bitstamp;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(MainActivity.LOG_TAG, "App onCreate");


        instance = this;
    }

    public static final App getInstance() {
        return instance;
    }

//    public void toast(final String message) {
//
//        App.getInstance().getApplicationContext().
//        App.getInstance().getApplicationContext().runOnUiThread(new Runnable() {
//            public void run() {
//                Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public static final void setBitstamp(BitstampData b) {
        App.bitstamp = b;
    }

    public static final BitstampData getBitstamp() {
        return App.bitstamp;
    }
}
