package com.jeremy.android.consumer.cardDetail.tabs.detailInfo;

import android.content.Context;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.database.model.Card;

/**
 * Created by Jeremy on 2017/3/17.
 */

public class DetailInfoContract {

    interface View extends BaseView<Presenter> {

        void showCardNoAfterAutoSet(String cardNo);

        void showCardPointsUpdated(float points);

        void showCardBalanceUpdated(float balance);

        void showExpiredTimeUpdated(String expiredTime);

        void showUpdateCardMsg(String msg);

        long getCardExpirdTime();

        void initDpDialog(int year, int month, int dayOfMonth);

        void showCardList(CardItem newCardItem);

        Context getCtx();
    }

    interface Presenter extends BasePresenter {

        void autoSetCardNo();

        void updateExpiredTime(int year, int monthOfYear, int dayOfMonth);

        void updateCard(Card card, String newName, String newCardNo, String newPhone, String newAddress, String newMemo);
    }
}
