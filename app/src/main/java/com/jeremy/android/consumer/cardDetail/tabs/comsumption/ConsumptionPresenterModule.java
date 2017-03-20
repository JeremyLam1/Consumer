package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/7.
 */
@Module
public class ConsumptionPresenterModule {

    private final ConsumptionContract.View mView;

    public ConsumptionPresenterModule(ConsumptionContract.View view) {
        mView = view;
    }

    @Provides
    ConsumptionContract.View provideConsumptionContractView() {
        return mView;
    }

}
