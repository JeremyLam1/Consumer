package com.jeremy.android.consumer.about;

import com.jeremy.android.consumer.AppComponent;
import com.jeremy.android.consumer.utils.PerActivityScoped;

import dagger.Component;

/**
 * Created by Jeremy on 2017/2/24.
 */
@PerActivityScoped
@Component(dependencies = AppComponent.class, modules = AboutPresenterModule.class)
public interface AboutComponent {

    void inject(AboutActivity activity);

}
