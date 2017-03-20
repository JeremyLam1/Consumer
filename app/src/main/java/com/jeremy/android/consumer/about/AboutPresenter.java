package com.jeremy.android.consumer.about;

import android.content.Context;
import android.text.TextUtils;

import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.VersionConfig;
import com.jeremy.android.consumer.data.source.remote.webApi.ApiService;
import com.jeremy.android.consumer.data.bean.FeedBack;
import com.jeremy.android.consumer.utils.NetworkUtils;
import com.jeremy.android.consumer.utils.VersionUtils;
import com.jeremy.android.consumer.data.source.remote.webApi.WebApiObserver;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/2/24.
 */

public class AboutPresenter implements AboutContract.Presenter {

    private ApiService apiService;

    private AboutContract.View view;

    private String[] pageMsg = new String[1];

    @Inject
    AboutPresenter(ApiService apiService, AboutContract.View view) {
        this.apiService = apiService;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void checkVersionUpdate(Context context) {

        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getVersionConfig()
                    .compose(view.getBindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> view.showCheckingVersion())
                    .subscribe(new DefaultObserver<VersionConfig>() {
                        @Override
                        public void onNext(VersionConfig versionCfg) {
                            int currVersion = MyApplication.get().getCurrAppVersion();
                            if (currVersion != -1 && versionCfg.getVerCode() > currVersion) {
                                view.showHasNewVersion(versionCfg);
                            } else {
                                view.showCurrVersionIsNew();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showPageMsg(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            pageMsg[0] = context.getString(R.string.please_check_network);
            view.showPageMsg(pageMsg[0]);
        }
    }

    @Override
    public void sendFeedBack(String content, String contact) {

        Context ctx = (Context) view;
        final String[] errorMsg = new String[1];
        if (view.getSentButtonProgress() == 0) {

            view.showSentButtonLoading();

            if (!NetworkUtils.isNetworkAvailable(ctx)) {
                errorMsg[0] = ctx.getString(R.string.please_check_network);
                view.showPageMsg(errorMsg[0]);
                view.showSentButtonError();
                return;
            }

            if (TextUtils.isEmpty(content)) {
                errorMsg[0] = ctx.getString(R.string.feedback_content) + ctx.getString(R.string.field_no_be_null);
                view.showPageMsg(errorMsg[0]);
                view.showSentButtonError();
                return;
            }

            if (TextUtils.isEmpty(contact)) {
                errorMsg[0] = ctx.getString(R.string.contact) + ctx.getString(R.string.field_no_be_null);
                view.showPageMsg(errorMsg[0]);
                view.showSentButtonError();
                return;
            }

            FeedBack feedBack = new FeedBack();
            feedBack.setContents(content);
            feedBack.setContact(contact);
            feedBack.setFeed_time(System.currentTimeMillis());
            feedBack.setVersion(VersionUtils.getAppAndVersionName(ctx));

            apiService.sendFeedBack(feedBack)
                    .compose(view.getBindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new WebApiObserver<String>() {

                        @Override
                        public void onSuccess(String s) {
                            errorMsg[0] = ctx.getString(R.string.thank_for_feedback);
                            view.showPageMsg(errorMsg[0]);
                            view.showSentButtonSuccess();
                            view.cleanInputData();
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            view.showPageMsg(errorMsg);
                            view.showSentButtonError();
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
        } else {
            view.showSentButtonNormal();
        }

    }

    @Override
    public void share() {
        view.showSharePage();
    }

    @Override
    public void contactUs() {
        view.showDialPage();
    }

    @Override
    public void visitCompanyWebsite() {
        view.showBrowse();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        if (view != null) {
            view = null;
        }
    }
}
