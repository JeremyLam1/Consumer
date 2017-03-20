package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.ConsumeBom;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.utils.PreferencesUtils;
import com.jeremy.android.consumer.utils.SmsUtils;
import com.jeremy.android.consumer.rxbus.RxBus;
import com.jeremy.android.consumer.rxbus.event.UpdateCardBalance;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;

import org.reactivestreams.Publisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/3/7.
 */

public class ConsumptionPresenter implements ConsumptionContract.Presenter {

    private DataRepository mDataRepository;

    private ConsumptionContract.View mView;

    private CompositeDisposable compositeDisposable;

    private RxBus mRxBus;

    @Inject
    ConsumptionPresenter(DataRepository mDataRepository, ConsumptionContract.View mView, RxBus rxBus) {
        this.mDataRepository = mDataRepository;
        this.mView = mView;
        this.mRxBus = rxBus;
        mView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        mRxBus.toFlowable(UpdateCardBalance.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(updateCardBalance -> {
                    mView.showCardBalanceUpdate(updateCardBalance.getBalance());
                });
    }

    @Override
    public void loadEnableBoms() {
        compositeDisposable.clear();
        Disposable disposable = mDataRepository.getBoms()
                .compose(mView.getBindToLifecycle())
                .flatMap(new Function<List<Bom>, Publisher<Bom>>() {
                    @Override
                    public Publisher<Bom> apply(List<Bom> boms) throws Exception {
                        return Flowable.fromIterable(boms);
                    }
                })
                .filter(bom -> bom.selected == 1)
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(boms -> {
                    if (boms.isEmpty()) {
                        mView.showNoEnableBomsDialog();
                    } else {
                        mView.showBoms(boms);
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void addBoms(List<Bom> boms, List<Boolean> checks) {
        for (int i = 0; i < boms.size(); i++) {
            if (checks.get(i)) {
                Bom checkBom = boms.get(i);
                if (!checkListHasInclude(checkBom._id)) {
                    ConsumeBom consumeBom = ConsumeBom.newItemInstance(checkBom);
                    mView.getConsumeBoms().add(consumeBom);
                }
            }
        }
        mView.showBomsAdd();
    }

    @NonNull
    private Boolean checkListHasInclude(Long id) {
        for (ConsumeBom consumeBom : mView.getConsumeBoms()) {
            if (consumeBom.getItemType() == ConsumeBom.ITEM && id.equals(consumeBom.getBomId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteBom(int position) {
        mView.showBomsDeleted(position);
    }

    @Override
    public void setBoms() {
        mView.showBomsSettingPage();
    }

    @Override
    public void calculatePrice() {
        if (mView.getConsumeButtonProgress() == 0) {
            mView.showConsumeButtonLoading();

            Context ctx = mView.getCtx();
            String pageMsg;
            if (mView.getConsumeBoms().size() == 1) {
                pageMsg = ctx.getString(R.string.please_add_bom);
                mView.showPageMsg(pageMsg);
                return;
            }

            float consumePrice = 0;
            BigDecimal bd = new BigDecimal(String.valueOf(consumePrice));
            for (ConsumeBom consumeBom : mView.getConsumeBoms()) {
                if (consumeBom.getItemType() == ConsumeBom.ITEM) {
                    if (consumeBom.getUnit().equals(ctx.getString(R.string.hour))) {
                        String time = consumeBom.getTime();
                        if (Float.valueOf(time) == 0f) {
                            mView.showConsumeButtonError();
                            pageMsg = ctx.getString(R.string.consumption_time) + ctx.getString(R.string.no_be_zero);
                            mView.showPageMsg(pageMsg);
                            return;
                        }
                    }
                    bd = bd.add(new BigDecimal(consumeBom.getPrice()));
                }
            }

            float needPayPrice = Float.valueOf(String.format(Locale.getDefault(), "%.1f", bd.floatValue()));
            mView.showConsumeConfirmDialog(needPayPrice);
        } else {
            mView.showConsumeButtonNormal();
        }
    }

    @Override
    public void doConsume(float needPayPrice, Card mCard, String memo) {

        Context ctx = mView.getCtx();
        String pageMsg;
        if (mCard.cardBalance < needPayPrice) {
            mView.showConsumeButtonError();
            pageMsg = ctx.getString(R.string.curr_balance_no_enough);
            mView.showPageMsg(pageMsg);
            return;
        }

        BigDecimal b0 = new BigDecimal(String.valueOf(mCard.cardBalance));
        BigDecimal b1 = new BigDecimal(String.valueOf(needPayPrice));
        mCard.cardBalance = b0.subtract(b1).floatValue();
        mDataRepository.updateCard(mCard);

        long currTime = System.currentTimeMillis();
        for (ConsumeBom consumeBom : mView.getConsumeBoms()) {
            if (consumeBom.getItemType() == ConsumeBom.ITEM) {
                Consumption consumption = new Consumption();
                consumption.cardId = mCard._id;
                consumption.memo = memo;
                consumption.payTime = currTime;
                consumption.payCount = consumeBom.getCount();
                consumption.payMoney = consumeBom.getPrice();
                consumption.payTimeLength = Float.valueOf(consumeBom.getTime());
                consumption.bomName = consumeBom.getBomName();
                consumption.bomUnitPrice = consumeBom.getUnitPrice();
                consumption.unit = consumeBom.getUnit();
                mDataRepository.saveConsumption(consumption);

                mRxBus.post(consumption).post(new UpdateCardBalance(mCard.cardBalance));
            }
        }

        boolean smsEnable = PreferencesUtils.getInstance(ctx).getSMSEnable();
        if (smsEnable) {
            SmsUtils.sendConsumeSms(ctx, mCard, currTime, needPayPrice);
            pageMsg = ctx.getString(R.string.sms_send_finish);
            mView.showPageMsg(pageMsg);
        }

        mView.showConsumeSuccess();
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
