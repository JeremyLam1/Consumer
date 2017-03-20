package com.jeremy.android.consumer.setting.BomSetting;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/2/17.
 */

@PerActivityScoped
@Component(dependencies = AppComponent.class, modules = BomSettingPresenterModule.class)
public interface BomSettingComponent {

    void inject(BomSettingActivity activity);
}
