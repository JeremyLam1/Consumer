package com.jeremy.android.consumer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jeremy on 2016/4/5.
 */
public class PreferencesUtils {

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "setting";


    private volatile static PreferencesUtils instance;
    private volatile static SharedPreferences myPreference;
    private volatile static SharedPreferences.Editor myPreferenceEditor;

    public static PreferencesUtils getInstance(Context ctx) {
        if (instance == null) {
            synchronized (PreferencesUtils.class) {
                if (instance == null) {      // 第二次检查
                    instance = new PreferencesUtils();
                    myPreference = ctx.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                    myPreferenceEditor = myPreference.edit();
                }
            }
        }
        return instance;
    }

    /**
     * 获取店铺名称
     *
     * @return
     */
    public String getStoreName(Context context) {
        return myPreference.getString("storeName", VersionUtils.getAppName(context));
    }

    /**
     * 设置店铺名称
     *
     * @param storeName
     */
    public void setStoreName(String storeName) {
        myPreferenceEditor.putString("storeName", storeName);
        myPreferenceEditor.commit();
    }

    /**
     * 获取短信通知开关
     *
     * @return
     */
    public boolean getSMSEnable() {
        return myPreference.getBoolean("smsEnable", true);
    }

    /**
     * 设置短信通知开关
     *
     * @param enable
     */
    public void setSMSEnable(boolean enable) {
        myPreferenceEditor.putBoolean("smsEnable", enable);
        myPreferenceEditor.commit();
    }

    /**
     * 获取广告关闭按钮显示状态
     *
     * @return
     */
    public boolean getAdCloseBtnShow() {
        return myPreference.getBoolean("adCloseBtnShow", true);
    }

    /**
     * 设置广告关闭按钮显示状态
     *
     * @param show
     */
    public void setAdCloseBtnShowe(boolean show) {
        myPreferenceEditor.putBoolean("adCloseBtnShow", show);
        myPreferenceEditor.commit();
    }


    /**
     * 获取IMEI号
     *
     * @return
     */
    public String getImei() {
        return myPreference.getString("imei", "");
    }

    /**
     * 设置IMEI号
     *
     * @param imei
     */
    public void setImei(String imei) {
        myPreferenceEditor.putString("imei", imei);
        myPreferenceEditor.commit();
    }


}
