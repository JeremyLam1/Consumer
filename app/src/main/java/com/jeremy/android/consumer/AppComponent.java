package com.jeremy.android.consumer;

import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.data.source.DataRepositoryModule;
import com.jeremy.android.consumer.data.source.remote.webApi.ApiService;
import com.jeremy.android.consumer.data.source.remote.webApi.ApiServiceModule;
import com.jeremy.android.consumer.rxbus.RxBusModule;
import com.jeremy.android.consumer.rxbus.RxBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Jeremy on 2017/1/23.
 */
@Singleton
@Component(modules = {AppModule.class, ApiServiceModule.class, DataRepositoryModule.class, RxBusModule.class})
public interface AppComponent {

    RxBus getRxBus();

    ApiService getApiService();

    DataRepository getCardsRepository();

}
