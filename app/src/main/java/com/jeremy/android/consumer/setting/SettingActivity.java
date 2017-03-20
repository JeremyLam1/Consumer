package com.jeremy.android.consumer.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremy.android.consumer.MyApplication;
import com.kyleduo.switchbutton.SwitchButton;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.setting.BomSetting.BomSettingActivity;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity implements SettingContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edt_storeName)
    EditText edtStoreName;

    @BindView(R.id.switch_sms)
    SwitchButton switchSms;

    @BindView(R.id.tv_recharge_template)
    TextView tvRechargeTemplate;

    @BindView(R.id.tv_consumer_template)
    TextView tvConsumerTemplate;

    private ProgressDialog progDialog;

    @Inject
    SettingPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        DaggerSettingComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .settingPresenterModule(new SettingPresenterModule(this))
                .build()
                .inject(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initProgDialog();

        mPresenter.subscribe();
    }

    private void initProgDialog() {
        progDialog = new ProgressDialog(this);
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @OnClick(R.id.img_recharge)
    public void showRechargeHelp() {
        showHelpDialog();
    }

    @OnClick(R.id.img_consumer)
    public void showConsumerHelp() {
        showHelpDialog();
    }

    @OnClick(R.id.layout_bom_setting)
    public void onBomSettingOnclick() {
        mPresenter.openBomSetting();
    }

    @OnClick(R.id.layout_data_backup)
    public void onDataBackupOnclick() {
        mPresenter.uploadLocalDatas();
    }

    @OnClick(R.id.layout_data_restore)
    public void onDataRestoreOnclick() {
        mPresenter.downloadRemoteDatas();
    }

    private void showHelpDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
        alert.setMessage(getString(R.string.setting_dialog_hint));
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                String storeName = edtStoreName.getText().toString();
                boolean smsEnable = switchSms.isChecked();
                mPresenter.saveSetting(storeName, smsEnable);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showStoreName(String name) {
        edtStoreName.setText(name);
    }

    @Override
    public void showSmsEnableStatus(boolean status) {
        switchSms.setCheckedImmediately(status);
    }

    @Override
    public void showRechargeTemplate(String template) {
        tvRechargeTemplate.setText(template);
    }

    @Override
    public void showConsumerTemplate(String template) {
        tvConsumerTemplate.setText(template);
    }

    @Override
    public void showBomSettingsPage() {
        Intent intent = new Intent(this, BomSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void showCardListPage() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showLoading(String content) {
        progDialog.setMessage(content);
        progDialog.show();
    }

    @Override
    public void hideLoading() {
        progDialog.hide();
    }

    @Override
    public void setPresenter(SettingContract.Presenter presenter) {
        mPresenter = (SettingPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return this.bindToLifecycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}
