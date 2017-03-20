package com.jeremy.android.consumer.data.source.remote.webApi;

import android.app.Application;

import com.jeremy.android.consumer.R;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jeremy on 2017/2/28.
 */

@Module
public class ApiServiceModule {

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(Application app, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(app.getString(R.string.service_base_url))//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加回调库，采用RxJava2
                .client(okHttpClient)
                .build();
    }

    @Singleton
    @Provides
    ApiService providesApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
