package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerFragmentScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/3/7.
 */
@PerFragmentScoped
@Component(dependencies = AppComponent.class, modules = {ConsumeBomListModule.class, ConsumptionPresenterModule.class})
public interface ConsumptionComponent {

    void inject(ConsumptionFragment fragment);
}
