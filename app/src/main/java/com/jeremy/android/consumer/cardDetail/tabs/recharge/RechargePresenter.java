package com.jeremy.android.consumer.cardDetail.tabs.recharge;

import android.content.Context;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.rxbus.RxBus;
import com.jeremy.android.consumer.rxbus.event.UpdateCardBalance;
import com.jeremy.android.consumer.rxbus.event.UpdateCardPoints;
import com.jeremy.android.consumer.utils.PreferencesUtils;
import com.jeremy.android.consumer.utils.SmsUtils;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Recharge;

import java.math.BigDecimal;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Jeremy on 2017/3/17.
 */

public class RechargePresenter implements RechargeContract.Presenter {

    private DataRepository mDataRepository;

    private RechargeContract.View mView;

    private CompositeDisposable compositeDisposable;

    private RxBus mRxBus;

    @Inject
    RechargePresenter(DataRepository mDataRepository, RechargeContract.View mView, RxBus rxBus) {
        this.mDataRepository = mDataRepository;
        this.mView = mView;
        this.mRxBus = rxBus;
        mView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        compositeDisposable.clear();
        Disposable disposable = mRxBus.toFlowable(UpdateCardBalance.class)
                .compose(mView.getBindToLifecycle())
                .subscribe(updateCardBalance -> {
                    mView.showCardBalanceUpdate(updateCardBalance.getBalance());
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void doRecharge(Card mCard, String rechargeMoney, String memo) {
        Context ctx = mView.getCtx();
        float fPrice = Float.valueOf(rechargeMoney);
        if (fPrice == 0f) {
            mView.showRechargeButtonError();
            String msg = ctx.getString(R.string.recharge_price) + ctx.getString(R.string.no_be_zero);
            mView.showPageMsg(msg);
            return;
        }

        Recharge recharge = new Recharge();
        recharge.cardId = mCard._id;
        recharge.memo = memo;
        recharge.chargeTime = System.currentTimeMillis();
        recharge.chargeMoney = fPrice;
        mDataRepository.saveRecharge(recharge);

        BigDecimal bdPrice = new BigDecimal(rechargeMoney);
        BigDecimal bdBalance = new BigDecimal(String.valueOf(mCard.cardBalance));
        BigDecimal bdPoints = new BigDecimal(String.valueOf(mCard.userPoints));

        bdBalance = bdBalance.add(bdPrice);
        bdPoints = bdPoints.add(bdPrice);

        mCard.cardBalance = bdBalance.floatValue();
        mCard.userPoints = bdPoints.floatValue();
        mDataRepository.updateCard(mCard);

        mView.showCardBalanceUpdate(mCard.cardBalance);

        RxBus.get().post(recharge).post(new UpdateCardBalance(mCard.cardBalance)).post(new UpdateCardPoints(mCard.userPoints));

        if (PreferencesUtils.getInstance(ctx).getSMSEnable()) {
            SmsUtils.sendRechargeSms(ctx, mCard, recharge);
            String msg = ctx.getString(R.string.sms_send_finish);
            mView.showPageMsg(msg);
        }

        mView.showRechargeSuccess();
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
