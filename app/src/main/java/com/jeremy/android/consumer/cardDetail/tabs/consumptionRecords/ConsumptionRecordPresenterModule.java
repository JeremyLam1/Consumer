package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/11.
 */
@Module
public class ConsumptionRecordPresenterModule {

    private final ConsumptionRecordContract.View mView;

    ConsumptionRecordPresenterModule(ConsumptionRecordContract.View view) {
        mView = view;
    }

    @Provides
    ConsumptionRecordContract.View provideConsumptionRecordContractView() {
        return mView;
    }
}
