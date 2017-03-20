package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.database.model.Recharge;

import java.util.List;

/**
 * Created by Jeremy on 2017/3/15.
 */

public class RechargeRecordContract {

    interface View extends BaseView<Presenter> {

        void showRecharges(List<Recharge> recharges);

        void showRechargeDetail(Recharge recharge);

        void showRechargeDeleted(int position);

        void showRechargeAdded(Recharge recharge);

        void showPageMsg(String msg);
    }

    interface Presenter extends BasePresenter {

        void loadRecharges(long cardId);

        void openRechargeDetail(long rechargeId);

        void deleteRecharge(int position, long rechargeId);
    }
}
