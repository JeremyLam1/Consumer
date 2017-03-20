package com.jeremy.android.consumer.cardDetail.tabs.recharge;

import android.content.Context;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.database.model.Card;

/**
 * Created by Jeremy on 2017/3/17.
 */

public class RechargeContract {

    interface View extends BaseView<Presenter> {

        void showPageMsg(String msg);

        int getRechargeButtonProgress();

        void showRechargeSuccess();

        void showRechargeButtonError();

        void showRechargeButtonLoading();

        void showRechargeButtonNormal();

        void showRechargeConfirmDialog();

        void showCardBalanceUpdate(float balance);

        Context getCtx();
    }

    interface Presenter extends BasePresenter {

        void doRecharge(Card mCard, String rechargeMoney, String memo);
    }
}
