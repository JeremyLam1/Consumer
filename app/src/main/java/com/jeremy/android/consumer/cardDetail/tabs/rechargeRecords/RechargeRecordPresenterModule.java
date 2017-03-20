package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/15.
 */
@Module
public class RechargeRecordPresenterModule {

    private final RechargeRecordContract.View mView;

    public RechargeRecordPresenterModule(RechargeRecordContract.View view) {
        this.mView = view;
    }

    @Provides
    RechargeRecordContract.View provideRechargeRecordContractView() {
        return mView;
    }
}
