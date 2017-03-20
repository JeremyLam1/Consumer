package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import com.jeremy.android.consumer.data.bean.ConsumeBom;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/8.
 */
@Module
public class ConsumeBomListModule {

    @Provides
    List<ConsumeBom> provideConsumeBoms() {
        List<ConsumeBom> consumeBoms = new ArrayList<>();
        consumeBoms.add(0, ConsumeBom.newAddInstance());
        return consumeBoms;
    }
}
