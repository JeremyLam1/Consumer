package com.jeremy.android.consumer.addCard;

import android.content.Context;
import android.text.TextUtils;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.utils.DBHelper;
import com.jeremy.android.consumer.utils.PreferencesUtils;
import com.jeremy.android.consumer.utils.SmsUtils;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Recharge;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/2/7.
 */

public class AddCardPresenter implements AddCardsContract.Presenter {

    private DataRepository dataRepository;

    private AddCardsContract.View view;

    private Calendar mCalendar;
    private long expiredTime;

    @Inject
    AddCardPresenter(DataRepository dataRepository, AddCardsContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        setCalendarInfo();
        autoSetCardNo();
    }

    private void setCalendarInfo() {
        mCalendar = Calendar.getInstance();
        int iExpiredYear = mCalendar.get(Calendar.YEAR);
        int iExpiredMonth = mCalendar.get(Calendar.MONTH);
        int iExpiredDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        view.initDpDialog(iExpiredYear, iExpiredMonth, iExpiredDay);
        updateExpiredTime(iExpiredYear, iExpiredMonth, iExpiredDay);
    }

    @Override
    public void autoSetCardNo() {
        //// TODO: 2017/3/20  cardNo取数据待优化
        String cardNo = DBHelper.getVaildCardNo(1);
        view.showCardNoAfterAutoSet(cardNo);
    }

    @Override
    public void updateExpiredTime(int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);

        expiredTime = mCalendar.getTime().getTime();
        String expiredTimeStr = TimeUtils.getDateFormatByTimeStamp(expiredTime);
        view.showExpiredTimeUpdated(expiredTimeStr);
    }

    @Override
    public void saveCard(String userName, String cardNo, String userPhone, String userAddr, String memo, String price) {

        Context ctx = (Context) view;
        final String[] msg = new String[1];

        if (TextUtils.isEmpty(userName)) {
            msg[0] = ctx.getString(R.string.user_name) + ctx.getString(R.string.field_no_be_null);
            view.showSaveCardMsg(msg[0]);
            return;
        }

        if (TextUtils.isEmpty(cardNo)) {
            msg[0] = ctx.getString(R.string.card_no) + ctx.getString(R.string.field_no_be_null);
            view.showSaveCardMsg(msg[0]);
            return;
        }

        if (TextUtils.isEmpty(userPhone)) {
            msg[0] = ctx.getString(R.string.user_phone) + ctx.getString(R.string.field_no_be_null);
            view.showSaveCardMsg(msg[0]);
            return;
        }

        Float fPrice = Float.valueOf(price);
        if (fPrice == 0f) {
            msg[0] = ctx.getString(R.string.recharge_price) + ctx.getString(R.string.no_be_zero);
            view.showSaveCardMsg(msg[0]);
            return;
        }

        dataRepository.checkCardNoExist(cardNo)
                .compose(view.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        msg[0] = ctx.getString(R.string.card_no) + ctx.getString(R.string.is_exist);
                        view.showSaveCardMsg(msg[0]);
                        return;
                    }
                    long currTime = System.currentTimeMillis();

                    Card card = new Card();
                    card.userName = userName;
                    card.cardNo = cardNo;
                    card.userPhone = userPhone;
                    card.userAddr = userAddr;
                    card.memo = memo;
                    card.createDate = currTime;
                    card.cardExpired = expiredTime;
                    card.cardBalance = fPrice;
                    card.userPoints = fPrice;
                    card.userDelete = 0;
                    dataRepository.saveCard(card);

                    Recharge recharge = new Recharge();
                    recharge.cardId = card._id;
                    recharge.memo = card.memo;
                    recharge.chargeTime = currTime;
                    recharge.chargeMoney = fPrice;
                    dataRepository.saveRecharge(recharge);

                    if (PreferencesUtils.getInstance(ctx).getSMSEnable()) {
                        msg[0] = ctx.getString(R.string.add_success) + "," + ctx.getString(R.string.sms_send_finish);
                        view.showSaveCardMsg(msg[0]);
                        sendSmsMsg(card, recharge);
                    }

                    CardItem cardItem = CardItem.newItemInstance(card);
                    view.showCardList(cardItem);
                });
    }

    @Override
    public void sendSmsMsg(Card card, Recharge recharge) {
        SmsUtils.sendRechargeSms((Context) view, card, recharge);
    }

    @Override
    public void unsubscribe() {
        if (view != null) {
            view = null;
        }
    }
}
