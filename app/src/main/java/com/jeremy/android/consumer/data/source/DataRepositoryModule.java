package com.jeremy.android.consumer.data.source;

import com.jeremy.android.consumer.data.source.local.LocalDataSource;
import com.jeremy.android.consumer.data.source.remote.RemoteDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/1/23.
 */

@Module
public class DataRepositoryModule {

    @Provides
    @Singleton
    @Remote
    DataSource provideCardsRemoteDataSource() {
        return new RemoteDataSource();
    }

    @Provides
    @Singleton
    @Local
    DataSource provideCardsLocalDataSource() {
        return new LocalDataSource();
    }
}
