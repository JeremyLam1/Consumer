package com.jeremy.android.consumer.cardDetail;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/7.
 */
@Module
public class CardDetailPresenterModule {

    private final CardDetailContract.View mView;

    private final long mCardId;

    public CardDetailPresenterModule(CardDetailContract.View view, long cardId) {
        mView = view;
        mCardId = cardId;
    }

    @Provides
    CardDetailContract.View provideCardDetailContractView() {
        return mView;
    }

    @Provides
    long provideCardId() {
        return mCardId;
    }
}
