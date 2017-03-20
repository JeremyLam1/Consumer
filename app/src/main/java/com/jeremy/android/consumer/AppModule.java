package com.jeremy.android.consumer;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/2/28.
 */

@Module
public class AppModule {

    private final Application application;

    AppModule(Application application) {
        this.application = application;
    }

    @Provides
    Application providesApplication() {
        return this.application;
    }
}
