package com.jeremy.android.consumer.cards;

import android.content.Intent;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.data.bean.VersionConfig;

import java.util.List;

/**
 * Created by Jeremy on 2017/1/21.
 */

public class CardsContract {

    interface View extends BaseView<Presenter> {

        void showHasNewVersion(VersionConfig config);

        void showRefreshLoadingIndicator();

        void hideRefreshLoadingIndicator(boolean loadMoreEnd);

        void hideMoreLoadingIndicator(boolean loadMoreEnd);

        void showRefreshToGetCards();

        void showAddCard();

        void showCardDetail(long cardId);

        void showLoadingCardsError(String errorMsg);

        void showCardDeleted(int pos);

        void showCardsUpdated();

        void showNoDataMsg();

        void showIsAllDataMsg();

        void showSettingSavedMsg();

        void showUpdateCardSuccessMsg();

        void showAddCardSuccessMsg();

        List<CardItem> getCardItems();

    }

    interface Presenter extends BasePresenter {

        void checkVersionUpdate();

        void result(int requestCode, int resultCode, Intent data);

        void loadCards(boolean isRefresh, String search);

        void addNewCard();

        void openCardDetail(long cardId);

        void deleteCard(int pos, long cardId);
    }
}
