package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.rxbus.RxBus;
import com.jeremy.android.database.model.Consumption;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/3/11.
 */
public class ConsumptionRecordPresenter implements ConsumptionRecordContract.Presenter {

    private DataRepository mDataRepository;

    private ConsumptionRecordContract.View mView;

    private RxBus mRxBus;

    private CompositeDisposable compositeDisposable;

    @Inject
    ConsumptionRecordPresenter(DataRepository dataRepository, ConsumptionRecordContract.View view, RxBus rxBus) {
        this.mDataRepository = dataRepository;
        this.mView = view;
        this.mRxBus = rxBus;
        mView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        compositeDisposable.clear();
        Disposable disposable = mRxBus.toFlowable(Consumption.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(consumption -> {
                    mView.showConsumptionAdded(consumption);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void loadConsumptions(long cardId) {
        mDataRepository.getConsumptionsByCardId(cardId)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumptions -> mView.showConsumptions(consumptions)
                        , throwable -> mView.showPageMsg(throwable.getMessage()));
    }

    @Override
    public void openConsumptionDetail(long consumptionId) {
        mDataRepository.getConsumption(consumptionId)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumption -> mView.showConsumptionDetail(consumption)
                        , throwable -> mView.showPageMsg(throwable.getMessage()));
    }

    @Override
    public void deleteConsumption(int position, long consumptionId) {
        mDataRepository.deleteConsumption(consumptionId);
        mView.showConsumptionDeleted(position);
    }

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (mView != null) {
            mView = null;
        }
    }
}
