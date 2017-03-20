package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.rxbus.RxBus;
import com.jeremy.android.database.model.Recharge;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/3/15.
 */

public class RechargeRecordPresenter implements RechargeRecordContract.Presenter {

    private DataRepository mDataRepository;

    private RechargeRecordContract.View mView;

    private RxBus mRxBus;

    private CompositeDisposable compositeDisposable;

    @Inject
    RechargeRecordPresenter(DataRepository dataRepository, RechargeRecordContract.View view, RxBus rxBus) {
        this.mDataRepository = dataRepository;
        this.mView = view;
        this.mRxBus = rxBus;
        mView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        compositeDisposable.clear();
        Disposable disposable = mRxBus.toFlowable(Recharge.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(recharge -> {
                    mView.showRechargeAdded(recharge);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void loadRecharges(long cardId) {
        mDataRepository.getRechargesByCardId(cardId)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recharges -> mView.showRecharges(recharges)
                        , throwable -> mView.showPageMsg(throwable.getMessage()));
    }

    @Override
    public void openRechargeDetail(long rechargeId) {
        mDataRepository.getRecharge(rechargeId)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recharge -> mView.showRechargeDetail(recharge)
                        , throwable -> mView.showPageMsg(throwable.getMessage()));
    }

    @Override
    public void deleteRecharge(int position, long rechargeId) {
        mDataRepository.deleteConsumption(rechargeId);
        mView.showRechargeDeleted(position);
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
