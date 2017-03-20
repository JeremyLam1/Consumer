package com.jeremy.android.consumer.addCard;


import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Recharge;

/**
 * Created by Jeremy on 2017/2/7.
 */

public class AddCardsContract {

    interface View extends BaseView<Presenter> {

        void showCardNoAfterAutoSet(String cardNo);

        void showSaveCardMsg(String msg);

        void showExpiredTimeUpdated(String expiredTime);

        void initDpDialog(int year, int month, int dayOfMonth);

        void showCardList(CardItem newCardItem);

    }

    interface Presenter extends BasePresenter {

        void autoSetCardNo();

        void updateExpiredTime(int year, int monthOfYear, int dayOfMonth);

        void saveCard(String userName, String cardNo, String userPhone, String userAddr, String memo, String price);

        void sendSmsMsg(Card card, Recharge recharge);

    }
}
