package com.jeremy.android.consumer.cardDetail.tabs.detailInfo;

import android.content.Context;
import android.text.TextUtils;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.rxbus.RxBus;
import com.jeremy.android.consumer.rxbus.event.UpdateCardBalance;
import com.jeremy.android.consumer.rxbus.event.UpdateCardPoints;
import com.jeremy.android.consumer.utils.DBHelper;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.database.model.Card;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/3/17.
 */

public class DetailInfoPresenter implements DetailInfoContract.Presenter {

    private DetailInfoContract.View mView;

    private DataRepository mDataRepository;

    private RxBus mRxBus;

    private Calendar mCalendar;
    private long expiredTime;

    @Inject
    public DetailInfoPresenter(DetailInfoContract.View mView, DataRepository mDataRepository, RxBus rxBus) {
        this.mView = mView;
        this.mDataRepository = mDataRepository;
        this.mRxBus = rxBus;
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date(mView.getCardExpirdTime()));
        int iExpiredYear = mCalendar.get(Calendar.YEAR);
        int iExpiredMonth = mCalendar.get(Calendar.MONTH);
        int iExpiredDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mView.initDpDialog(iExpiredYear, iExpiredMonth, iExpiredDay);
        expiredTime = mCalendar.getTime().getTime();
        String expiredTimeStr = TimeUtils.getDateFormatByTimeStamp(expiredTime);
        mView.showExpiredTimeUpdated(expiredTimeStr);

        mRxBus.toFlowable(UpdateCardPoints.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(updateUserPoints -> {
                    mView.showCardPointsUpdated(updateUserPoints.getPoints());
                });
        mRxBus.toFlowable(UpdateCardBalance.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(updateCardBalance -> {
                    mView.showCardBalanceUpdated(updateCardBalance.getBalance());
                });
    }

    @Override
    public void autoSetCardNo() {
        //// TODO: 2017/3/20  cardNo取数据待优化
        String cardNo = DBHelper.getVaildCardNo(1);
        mView.showCardNoAfterAutoSet(cardNo);
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
        mView.showExpiredTimeUpdated(expiredTimeStr);
    }

    @Override
    public void updateCard(Card card, String newName, String newCardNo, String newPhone, String newAddress, String newMemo) {
        Context ctx = mView.getCtx();
        final String[] msg = new String[1];

        if (TextUtils.isEmpty(newName)) {
            msg[0] = ctx.getString(R.string.user_name) + ctx.getString(R.string.field_no_be_null);
            mView.showUpdateCardMsg(msg[0]);
            return;
        }

        if (TextUtils.isEmpty(newCardNo)) {
            msg[0] = ctx.getString(R.string.card_no) + ctx.getString(R.string.field_no_be_null);
            mView.showUpdateCardMsg(msg[0]);
            return;
        }

        if (TextUtils.isEmpty(newPhone)) {
            msg[0] = ctx.getString(R.string.user_phone) + ctx.getString(R.string.field_no_be_null);
            mView.showUpdateCardMsg(msg[0]);
            return;
        }

        mDataRepository.checkCardNoExist(newCardNo)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean && !card.cardNo.equals(newCardNo)) {
                        msg[0] = ctx.getString(R.string.card_no) + ctx.getString(R.string.is_exist);
                        mView.showUpdateCardMsg(msg[0]);
                        return;
                    }

                    long currTime = System.currentTimeMillis();

                    card.userName = newName;
                    card.cardNo = newCardNo;
                    card.userPhone = newPhone;
                    card.userAddr = newAddress;
                    card.memo = newMemo;
                    card.createDate = currTime;
                    card.cardExpired = expiredTime;

                    mDataRepository.saveCard(card);

                    CardItem cardItem = CardItem.newItemInstance(card);
                    mView.showCardList(cardItem);
                });
    }

    @Override
    public void unsubscribe() {
        if (mView != null) {
            mView = null;
        }
    }

}
