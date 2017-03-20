package com.jeremy.android.consumer.cardDetail.tabs.detailInfo;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/17.
 */
@Module
public class DetailInfoPresenterModule {

    private final DetailInfoContract.View mView;

    public DetailInfoPresenterModule(DetailInfoContract.View mView) {
        this.mView = mView;
    }

    @Provides
    DetailInfoContract.View providesDetailInfoContractView() {
        return this.mView;
    }
}
