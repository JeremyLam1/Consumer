package com.jeremy.android.consumer.cardDetail.tabs.recharge;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/17.
 */
@Module
public class RechargePresenterModule {

    private final RechargeContract.View mView;

    public RechargePresenterModule(RechargeContract.View mView) {
        this.mView = mView;
    }

    @Provides
    RechargeContract.View providesRechargeContractView() {
        return mView;
    }
}
