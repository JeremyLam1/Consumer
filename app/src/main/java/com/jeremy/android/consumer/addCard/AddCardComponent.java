package com.jeremy.android.consumer.addCard;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/2/7.
 */
@PerActivityScoped
@Component(dependencies = AppComponent.class, modules = AddCardPresenterModule.class)
public interface AddCardComponent {

    void inject(AddCardActivity activity);

}
