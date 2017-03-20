package com.jeremy.android.consumer.rxbus;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/10.
 */
@Module
public class RxBusModule {

    @Singleton
    @Provides
    public RxBus provideRxBus() {
        return RxBus.get();
    }
}
