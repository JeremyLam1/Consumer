package com.jeremy.android.consumer.cardDetail.tabs.detailInfo;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerFragmentScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/3/17.
 */
@PerFragmentScoped
@Component(dependencies = AppComponent.class, modules = DetailInfoPresenterModule.class)
public interface DetailInfoComponent {

    void inject(DetailInfoFragment fragment);
}
