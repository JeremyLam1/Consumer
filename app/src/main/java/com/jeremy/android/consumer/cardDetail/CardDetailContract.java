package com.jeremy.android.consumer.cardDetail;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.database.model.Card;

/**
 * Created by Jeremy on 2017/3/7.
 */

public class CardDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void initViewPage(Card card);

        void showPageMsg(String msg);

        void showCardListPage();
    }

    interface Presenter extends BasePresenter {

        void saveDetailInfo();
    }

}
