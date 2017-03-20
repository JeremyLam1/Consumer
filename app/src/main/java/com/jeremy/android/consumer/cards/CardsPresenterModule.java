package com.jeremy.android.consumer.cards;

import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/1/23.
 */
@PerActivityScoped
@Module
public class CardsPresenterModule {

    private final CardsContract.View view;

    public CardsPresenterModule(CardsContract.View view) {
        this.view = view;
    }

    @Provides
    public CardsContract.View provideCardsContractView() {
        return this.view;
    }

}
