package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerFragmentScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/3/11.
 */
@PerFragmentScoped
@Component(dependencies = AppComponent.class, modules = {ConsumptionRecordListModule.class, ConsumptionRecordPresenterModule.class})
public interface ConsumptionRecordComponent {

    void inject(ConsumptionRecordFragment fragment);
}
