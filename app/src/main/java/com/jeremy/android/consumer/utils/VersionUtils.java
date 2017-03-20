package com.jeremy.android.consumer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jeremy.android.consumer.data.bean.VersionConfig;

/**
 * Created by Jeremy on 2016/3/28.
 */
public class VersionUtils {

    public static String getAppName(Context mContext) {
        String appName = "";
        PackageInfo pkg;
        try {
            pkg = mContext.getPackageManager().getPackageInfo(
                    mContext.getApplicationContext().getPackageName(), 0);
            appName = pkg.applicationInfo.loadLabel(
                    mContext.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    public static String getVersionName(Context mContext) {
        String versionName = "";
        PackageInfo pkg;
        try {
            pkg = mContext.getPackageManager().getPackageInfo(
                    mContext.getApplicationContext().getPackageName(), 0);
            versionName = pkg.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "v " + versionName;
    }

    public static String getAppAndVersionName(Context mContext) {
        return getAppName(mContext) + "(" + getVersionName(mContext) + ")";
    }

    public static final boolean isNewVersion(Context context, VersionConfig config) {
        if (config != null) {
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
                String currentVersionName = info.versionName;
                String newVersionName = config.getVerName();
                String configVersion[] = newVersionName.split("[.]");
                String currentVersion[] = currentVersionName.split("[.]");
                int minLength = configVersion.length < currentVersion.length ? configVersion.length : currentVersion.length;
                for (int i = 0; i < minLength; i++) {
                    int cv = Integer.parseInt(configVersion[i]);
                    int av = Integer.parseInt(currentVersion[i]);
                    if (cv > av) {
                        return true;
                    } else if (cv < av) {
                        return false;
                    }
                }
                return configVersion.length > currentVersion.length ? true : false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
