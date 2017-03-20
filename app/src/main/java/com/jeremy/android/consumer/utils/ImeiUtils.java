package com.jeremy.android.consumer.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/5/25 0025.
 */
public class ImeiUtils {

    public static String getImei(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String DeviceId = tm.getDeviceId();
        if (DeviceId == null || DeviceId.equals("")) {
            DeviceId = PreferencesUtils.getInstance(ctx).getImei();
            if (DeviceId.equals("")) {
                DeviceId = MD5Utils.stringToMD5(getKernalVersion()
                        + getDeviceModel() + getOSVersion() + getRadioVersion()
                        + getManufacturer()
                        + getMacAddress(ctx));
                PreferencesUtils.getInstance(ctx).setImei(DeviceId);
            }
        }
        return DeviceId;
    }

    private static String getKernalVersion() {
        Process process = null;
        String kernal = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != "") {
            String Keyword = "version ";
            int index = result.indexOf(Keyword);
            line = result.substring(index + Keyword.length());
            index = line.indexOf(" ");
            kernal = line.substring(0, index);
        }
        try {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kernal;
    }

    private static String getManufacturer() {
        try {
            return Build.MANUFACTURER;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取OS版本
     */
    private static String getOSVersion() {
        try {
            return Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机机型
     */
    private static String getDeviceModel() {
        try {
            return Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取基带版本
     */
    // Android 4.0
    private static String getRadioVersion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return Build.getRadioVersion();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMacAddress(Context ctx) {
        WifiManager mWifiManager = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.getConnectionInfo().getMacAddress();
    }
}
