package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import com.jeremy.android.database.model.Consumption;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/11.
 */
@Module
public class ConsumptionRecordListModule {

    @Provides
    List<Consumption> provideConsumptionRecords() {
        return new ArrayList<>();
    }
}
