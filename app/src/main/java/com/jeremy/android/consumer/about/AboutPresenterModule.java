package com.jeremy.android.consumer.about;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/2/24.
 */

@Module
public class AboutPresenterModule {

    private final AboutContract.View view;

    public AboutPresenterModule(AboutContract.View view) {
        this.view = view;
    }

    @Provides
    public AboutContract.View ProvidesAboutContractView() {
        return this.view;
    }
}
