package com.jeremy.android.consumer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.List;

/**
 * 发送广播的公共单元
 */

public class BroadcastUtils {

    public static String ACTION_SHOW_VERSION_DIALOG = "action_show_version_dialog";

    /**
     * 注册广播接收器
     *
     * @param action
     * @param receiver
     */
    public static void registBroadcastReceiver(Context context, String action,
                                               BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        context.registerReceiver(receiver, filter);
    }

    /**
     * 注册广播接收器
     *
     * @param actions
     * @param receiver
     */
    public static void registBroadcastReceiver(Context context,
                                               List<String> actions, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions)
            filter.addAction(action);
        context.registerReceiver(receiver, filter);
    }

    /**
     * 注册广播接收
     *
     * @param actions
     * @param receiver
     */
    public static void registBroadcastReceiver(Context context,
                                               String[] actions, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions)
            filter.addAction(action);
        context.registerReceiver(receiver, filter);
    }

    /**
     * 注销广播接收
     *
     * @param receiver
     */
    public static void unregistBroadcastReceiver(Context context,
                                                 BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    /**
     * 发送一个广播消息
     *
     * @param action
     */
    public static void broadcastAction(Context context, String action) {
        broadcastAction(context, action, null);
    }

    /**
     * 发送一个广播消息
     *
     * @param action
     * @param bundle
     */
    public static void broadcastAction(Context context, String action,
                                       Bundle bundle) {
        Intent mIntent = new Intent(action);
        if (bundle != null)
            mIntent.putExtras(bundle);
        context.sendBroadcast(mIntent);
    }

}
