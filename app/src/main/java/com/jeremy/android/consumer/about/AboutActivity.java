package com.jeremy.android.consumer.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.data.bean.VersionConfig;
import com.jeremy.android.consumer.utils.BroadcastUtils;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.utils.VersionUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity implements AboutContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_version)
    TextView tvVersion;

    @BindView(R.id.layout_version_update)
    RelativeLayout rlVersionUpdate;

    @BindView(R.id.edt_feedback)
    EditText edtFeedBack;

    @BindView(R.id.edt_contact)
    EditText edtContact;

    @BindView(R.id.btn_send)
    CircularProgressButton btnSend;

    @Inject
    AboutPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        DaggerAboutComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .aboutPresenterModule(new AboutPresenterModule(this))
                .build()
                .inject(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String versionName = VersionUtils.getVersionName(this);
        tvVersion.setText(getString(R.string.current_version) + versionName);

    }

    @OnClick(R.id.layout_version_update)
    public void onVersionUpdateClick() {
        mPresenter.checkVersionUpdate(this);
    }

    @OnClick(R.id.layout_contact)
    public void onContactClick() {
        mPresenter.contactUs();
    }

    @OnClick(R.id.layout_website)
    public void onWebsiteClick() {
        mPresenter.visitCompanyWebsite();
    }

    @OnClick(R.id.btn_send)
    public void onSendFeedBackClick() {
        String content = edtFeedBack.getText().toString();
        String contact = edtContact.getText().toString();
        mPresenter.sendFeedBack(content, contact);
    }

    @Override
    public void showCheckingVersion() {
        rlVersionUpdate.setEnabled(false);
        tvVersion.setText("正在检测更新");
    }

    @Override
    public void showHasNewVersion(VersionConfig config) {
        rlVersionUpdate.setEnabled(true);
        tvVersion.setText("检测到新版本");
        Intent broadcast = new Intent(BroadcastUtils.ACTION_SHOW_VERSION_DIALOG);
        broadcast.putExtra(BundleKeys.VERSION_CONFIG, config);
        sendBroadcast(broadcast);
    }

    @Override
    public void showCurrVersionIsNew() {
        rlVersionUpdate.setEnabled(true);
        tvVersion.setText("已是最新版本");
    }

    @Override
    public void showDialPage() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.value_contact)));
        startActivity(intent);
    }

    @Override
    public void showBrowse() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + getString(R.string.value_website)));
        startActivity(intent);
    }

    @Override
    public int getSentButtonProgress() {
        return btnSend.getProgress();
    }

    @Override
    public void showSentButtonSuccess() {
        btnSend.setProgress(100);
    }

    @Override
    public void showSentButtonError() {
        btnSend.setProgress(-1);
    }

    @Override
    public void showSentButtonLoading() {
        btnSend.setIndeterminateProgressMode(true);
        btnSend.setProgress(50);
    }

    @Override
    public void showSentButtonNormal() {
        btnSend.setProgress(0);
    }

    @Override
    public void showPageMsg(String txt) {
        showMsg(txt);
    }

    @Override
    public void cleanInputData() {
        edtFeedBack.setText("");
        edtContact.setText("");
    }

    @Override
    public void setPresenter(AboutContract.Presenter presenter) {
        mPresenter = (AboutPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                mPresenter.share();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSharePage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}