package com.jeremy.android.consumer;

import com.trello.rxlifecycle2.LifecycleTransformer;

public interface BaseView<T> {

    void setPresenter(T presenter);

    <T> LifecycleTransformer<T> getBindToLifecycle();

}
