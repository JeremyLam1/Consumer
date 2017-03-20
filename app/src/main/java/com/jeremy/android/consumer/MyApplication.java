package com.jeremy.android.consumer;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jeremy.android.consumer.data.source.DataRepositoryModule;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Jeremy on 2016/3/28.
 */
public class MyApplication extends Application {

    private AppComponent appComponent;

    private static MyApplication _instance;

    private int currAppVersion = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = (MyApplication) getApplicationContext();

        FlowManager.init(this);

        initCurrAppVersion();

        appComponent = DaggerAppComponent.builder()
                .dataRepositoryModule(new DataRepositoryModule())
                .appModule(new AppModule(this))
                .build();
    }

    private void initCurrAppVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            currAppVersion = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    public static MyApplication get() {
        return _instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public int getCurrAppVersion() {
        return currAppVersion;
    }

    public void setCurrAppVersion(int currAppVersion) {
        this.currAppVersion = currAppVersion;
    }

}

