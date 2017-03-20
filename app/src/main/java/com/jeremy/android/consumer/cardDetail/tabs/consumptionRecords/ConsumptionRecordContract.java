package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.database.model.Consumption;

import java.util.List;

/**
 * Created by Jeremy on 2017/3/11.
 */

public class ConsumptionRecordContract {

    interface View extends BaseView<Presenter> {

        void showConsumptions(List<Consumption> consumptions);

        void showConsumptionDetail(Consumption consumption);

        void showConsumptionDeleted(int position);

        void showConsumptionAdded(Consumption consumption);

        void showPageMsg(String msg);
    }

    interface Presenter extends BasePresenter {

        void loadConsumptions(long cardId);

        void openConsumptionDetail(long consumptionId);

        void deleteConsumption(int position, long consumptionId);
    }
}
