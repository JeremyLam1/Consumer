package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import com.jeremy.android.database.model.Recharge;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/15.
 */
@Module
public class RechargeRecordListModule {

    @Provides
    List<Recharge> provideRechargeRecords() {
        return new ArrayList<>();
    }
}
