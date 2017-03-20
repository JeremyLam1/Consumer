package com.jeremy.android.consumer.cards;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/1/23.
 */
@PerActivityScoped
@Component(dependencies = AppComponent.class, modules = {CardItemListModule.class, CardsPresenterModule.class})
public interface CardsComponent {

    void inject(CardsActivity activity);

}
