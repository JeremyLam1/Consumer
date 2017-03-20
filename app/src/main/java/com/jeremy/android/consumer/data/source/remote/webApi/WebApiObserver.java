package com.jeremy.android.consumer.data.source.remote.webApi;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Jeremy on 2017/2/28.
 */

public abstract class WebApiObserver<T> implements Observer<BaseResponse<T>> {

    private Disposable mDisposable;

    private static final String API_CODE_SUCCESS = "1";

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }


    @Override
    public void onNext(BaseResponse<T> t) {
        if (t.getCode().equals(API_CODE_SUCCESS)) {
            onSuccess(t.getData());
        } else {
            onFailed(t.getCode(), t.getMessage());
        }
    }

    @Override
    public void onError(Throwable error) {
        onFailed("", error.getMessage());
        onFinish();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        onFinish();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFailed(String errorCode, String errorMsg);

    public abstract void onFinish();
}
