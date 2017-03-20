package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerFragmentScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/3/15.
 */
@PerFragmentScoped
@Component(dependencies = AppComponent.class, modules = {RechargeRecordListModule.class, RechargeRecordPresenterModule.class})
public interface RechargeRecordComponent {

    void inject(RechargeRecordFragment fragment);
}
