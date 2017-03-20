package com.jeremy.android.consumer.about;

import android.content.Context;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.consumer.data.bean.VersionConfig;

/**
 * Created by Jeremy on 2017/2/24.
 */

public class AboutContract {

    interface View extends BaseView<Presenter> {

        void showCheckingVersion();

        void showHasNewVersion(VersionConfig config);

        void showCurrVersionIsNew();

        void showSharePage();

        void showDialPage();

        void showBrowse();

        int getSentButtonProgress();

        void showSentButtonSuccess();

        void showSentButtonError();

        void showSentButtonLoading();

        void showSentButtonNormal();

        void showPageMsg(String txt);

        void cleanInputData();
    }

    interface Presenter extends BasePresenter {

        void checkVersionUpdate(Context context);

        void share();

        void contactUs();

        void visitCompanyWebsite();

        void sendFeedBack(String content, String contact);
    }
}
