package ir.fanap.chattestapp.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApp extends MultiDexApplication {


    private static MyApp instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;


    }

    public static MyApp getInstance() {
        return instance;
    }
}
