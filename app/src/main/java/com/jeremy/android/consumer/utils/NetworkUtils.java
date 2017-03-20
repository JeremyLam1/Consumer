package com.jeremy.android.consumer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络工具类
 */
public class NetworkUtils {

    public static final int NETWORK_TYPE_WIFI = 99;

    /**
     * wifi状态 开启为true，否则为false
     *
     * @param context
     * @return
     */
    public static boolean isWifiActive(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否正在使用GPRS网络
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前网络的名字
     *
     * @param context
     * @return
     */
    public static String getNetworkName(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info.getTypeName();
    }

    public static int getMobileNetworkType(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        android.net.NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null) {
            if (ConnectivityManager.TYPE_MOBILE == ni.getType()) {
                TelephonyManager mTelephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                return mTelephonyManager.getNetworkType();
            } else if (ConnectivityManager.TYPE_WIFI == ni.getType()) {
                return NETWORK_TYPE_WIFI;
            }
        }

        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }
}
