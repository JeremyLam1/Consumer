package com.jeremy.android.consumer.cardDetail;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/3/7.
 */
@PerActivityScoped
@Component(dependencies = AppComponent.class, modules = CardDetailPresenterModule.class)
public interface CardDetailComponent {

    void inject(CardDetailActivity activity);
}
