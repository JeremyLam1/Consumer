package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import android.content.Context;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.consumer.data.bean.ConsumeBom;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;

import java.util.List;

/**
 * Created by Jeremy on 2017/3/7.
 */
public class ConsumptionContract {

    interface View extends BaseView<Presenter> {

        void showNoEnableBomsDialog();

        void showBoms(List<Bom> boms);

        void showPageMsg(String msg);

        int getConsumeButtonProgress();

        void showConsumeSuccess();

        void showConsumeButtonError();

        void showConsumeButtonLoading();

        void showConsumeButtonNormal();

        void showConsumeConfirmDialog(float total);

        void showBomsDeleted(int position);

        void showBomsAdd();

        void showBomsSettingPage();

        void showCardBalanceUpdate(float balance);

        Context getCtx();

        List<ConsumeBom> getConsumeBoms();

    }

    interface Presenter extends BasePresenter {

        void loadEnableBoms();

        void addBoms(List<Bom> boms, List<Boolean> checks);

        void deleteBom(int position);

        void setBoms();

        void calculatePrice();

        void doConsume(float needPayPrice, Card mCard, String memo);

    }
}
