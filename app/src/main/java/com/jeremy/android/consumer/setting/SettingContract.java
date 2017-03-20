package com.jeremy.android.consumer.setting;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;

/**
 * Created by Jeremy on 2017/2/17.
 */

public class SettingContract {

    interface View extends BaseView<Presenter> {

        void showStoreName(String name);

        void showSmsEnableStatus(boolean status);

        void showRechargeTemplate(String template);

        void showConsumerTemplate(String template);

        void showBomSettingsPage();

        void showPageMsg(String msg);

        void showCardListPage();

        void showLoading(String content);

        void hideLoading();

    }

    interface Presenter extends BasePresenter {

        void openBomSetting();

        void uploadLocalDatas();

        void downloadRemoteDatas();

        void saveSetting(String storeName, boolean smsEnable);
    }
}
