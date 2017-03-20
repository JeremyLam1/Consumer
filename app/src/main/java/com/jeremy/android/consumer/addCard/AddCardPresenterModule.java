package com.jeremy.android.consumer.addCard;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/2/7.
 */

@Module
public class AddCardPresenterModule {

    private final AddCardsContract.View view;

    public AddCardPresenterModule(AddCardsContract.View view) {
        this.view = view;
    }

    @Provides
    public AddCardsContract.View providesAddCardsContractView() {
        return this.view;
    }
}
