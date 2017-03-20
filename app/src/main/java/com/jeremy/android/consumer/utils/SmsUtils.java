package com.jeremy.android.consumer.utils;

import android.content.Context;
import android.telephony.SmsManager;

import com.jeremy.android.consumer.R;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Recharge;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2016/4/7 0007.
 */
public class SmsUtils {

    public static String getRechargeSmsTemplate(Context ctx) {
        return "尊敬的" + ctx.getString(R.string.sms_key_user_name) + ",您的【" + ctx.getString(R.string.sms_key_card_no) + "】号会员卡，于" + ctx.getString(R.string.sms_key_current_time) + "成功充值" + ctx.getString(R.string.sms_key_price) + "元，所剩余额为" + ctx.getString(R.string.sms_key_balance) + "元。" + ctx.getString(R.string.sms_key_store_name);
    }

    public static String getConsumeSmsTemplate(Context ctx) {
        return "尊敬的" + ctx.getString(R.string.sms_key_user_name) + ",您的【" + ctx.getString(R.string.sms_key_card_no) + "】号会员卡，于" + ctx.getString(R.string.sms_key_current_time) + "成功消费" + ctx.getString(R.string.sms_key_price) + "元，所剩余额为" + ctx.getString(R.string.sms_key_balance) + "元。" + ctx.getString(R.string.sms_key_store_name);
    }

    public static void sendRechargeSms(Context ctx, Card card, Recharge recharge) {
        String template = getRechargeSmsTemplate(ctx);
        template = template.replace(ctx.getString(R.string.sms_key_user_name), card.userName);
        template = template.replace(ctx.getString(R.string.sms_key_card_no), card.cardNo);
        template = template.replace(ctx.getString(R.string.sms_key_current_time), TimeUtils.getFormatByTimeStamp(recharge.chargeTime));
        template = template.replace(ctx.getString(R.string.sms_key_price), recharge.chargeMoney + "");
        template = template.replace(ctx.getString(R.string.sms_key_balance), card.cardBalance + "");
        template = template.replace(ctx.getString(R.string.sms_key_store_name), PreferencesUtils.getInstance(ctx).getStoreName(ctx));
        template = template + "-" + VersionUtils.getAppName(ctx);

        sendSMS(card.userPhone, template);
    }

    public static void sendConsumeSms(Context ctx, Card card, long consumeTime, float payMoney) {
        String template = getConsumeSmsTemplate(ctx);
        template = template.replace(ctx.getString(R.string.sms_key_user_name), card.userName);
        template = template.replace(ctx.getString(R.string.sms_key_card_no), card.cardNo);
        template = template.replace(ctx.getString(R.string.sms_key_current_time), TimeUtils.getFormatByTimeStamp(consumeTime));
        template = template.replace(ctx.getString(R.string.sms_key_price), payMoney + "");
        template = template.replace(ctx.getString(R.string.sms_key_balance), card.cardBalance + "");
        template = template.replace(ctx.getString(R.string.sms_key_store_name), PreferencesUtils.getInstance(ctx).getStoreName(ctx));
        template = template + "-" + VersionUtils.getAppName(ctx);

        sendSMS(card.userPhone, template);
    }

    private static void sendSMS(String phone, String content) {
        SmsManager manager = SmsManager.getDefault();
        if (content.length() > 70) {
            ArrayList<String> list = manager.divideMessage(content);
            manager.sendMultipartTextMessage(phone, null, list, null, null);
        } else {
            manager.sendTextMessage(phone, null, content, null, null);
        }
    }
}
