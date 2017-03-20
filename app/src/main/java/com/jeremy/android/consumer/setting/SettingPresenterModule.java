package com.jeremy.android.consumer.setting;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/2/17.
 */
@Module
public class SettingPresenterModule {

    private final SettingContract.View view;

    public SettingPresenterModule(SettingContract.View view) {
        this.view = view;
    }

    @Provides
    public SettingContract.View provideSettingContractView() {
        return this.view;
    }
}
