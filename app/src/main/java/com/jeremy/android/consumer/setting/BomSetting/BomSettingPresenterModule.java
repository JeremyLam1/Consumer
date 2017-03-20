package com.jeremy.android.consumer.setting.BomSetting;

import com.jeremy.android.database.model.Bom;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/2/17.
 */

@Module
public class BomSettingPresenterModule {

    private final BomSettingContract.View view;

    public BomSettingPresenterModule(BomSettingContract.View view) {
        this.view = view;
    }

    @Provides
    public BomSettingContract.View provideBomSettingContractView() {
        return this.view;
    }

    @Provides
    public List<Bom> provideBomList() {
        return new ArrayList<>();
    }
}
