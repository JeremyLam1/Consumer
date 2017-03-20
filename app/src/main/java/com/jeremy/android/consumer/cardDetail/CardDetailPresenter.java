package com.jeremy.android.consumer.cardDetail;

import android.support.annotation.Nullable;

import com.jeremy.android.consumer.data.source.DataRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/3/7.
 */

public class CardDetailPresenter implements CardDetailContract.Presenter {

    private DataRepository mDataRepository;

    private CardDetailContract.View mView;

    @Nullable
    private long mCardId;

    @Inject
    CardDetailPresenter(@Nullable long cardId, DataRepository dataRepository, CardDetailContract.View view) {
        this.mCardId = cardId;
        this.mDataRepository = dataRepository;
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadCard();
    }

    private void loadCard() {
        mDataRepository.getCard(mCardId)
                .compose(mView.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(card -> {
                            mView.setTitle(card.cardNo);
                            mView.initViewPage(card);
                        }
                        , throwable -> mView.showPageMsg(throwable.getMessage())
                );
    }

    @Override
    public void saveDetailInfo() {

    }

    @Override
    public void unsubscribe() {
        if (mView != null) {
            mView = null;
        }
    }
}
