package com.jeremy.android.consumer.addCard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.view.MoneyTextWatcher;
import com.jeremy.android.consumer.view.XEditText;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jeremy on 2016/3/28.
 */
public class AddCardActivity extends BaseActivity implements AddCardsContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edt_user_name)
    XEditText edtUserName;

    @BindView(R.id.edt_cardNo)
    XEditText edtCardNo;

    @BindView(R.id.edt_user_phone)
    XEditText edtUserPhone;

    @BindView(R.id.edt_userAddr)
    XEditText edtUserAddr;

    @BindView(R.id.tv_card_expired)
    TextView tvCardExpired;

    @BindView(R.id.edt_memo)
    EditText edtMemo;

    @BindView(R.id.edt_recharge_price)
    EditText edtPrice;

    private DatePickerDialog dpDialog;

    @Inject
    AddCardPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        DaggerAddCardComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .addCardPresenterModule(new AddCardPresenterModule(this))
                .build()
                .inject(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtPrice.addTextChangedListener(new MoneyTextWatcher(edtPrice));

        mPresenter.subscribe();
    }


    DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, monthOfYear, dayOfMonth) -> mPresenter.updateExpiredTime(year, monthOfYear, dayOfMonth);


    @OnClick(R.id.img_card_no)
    public void setCardNo() {
        mPresenter.autoSetCardNo();
    }

    @OnClick(R.id.btn_show_dp_dialog)
    public void showDpDialog() {
        dpDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                String userName = edtUserName.getText().toString();
                String cardNo = edtCardNo.getText().toString();
                String userPhone = edtUserPhone.getText().toString();
                String userAddr = edtUserAddr.getText().toString();
                String memo = edtMemo.getText().toString();
                String price = edtPrice.getText().toString();
                mPresenter.saveCard(userName, cardNo, userPhone, userAddr, memo, price);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(AddCardsContract.Presenter presenter) {
        mPresenter = (AddCardPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public void showCardNoAfterAutoSet(String cardNo) {
        edtCardNo.setText(cardNo);
    }

    @Override
    public void showSaveCardMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void showExpiredTimeUpdated(String expiredTime) {
        tvCardExpired.setText(expiredTime);
    }

    @Override
    public void initDpDialog(int year, int month, int dayOfMonth) {
        dpDialog = new DatePickerDialog(this, mDateSetListener, year, month, dayOfMonth);
    }

    @Override
    public void showCardList(CardItem newCardItem) {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.CARD_ITEM, newCardItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}