package com.jeremy.android.consumer.cardDetail.tabs.detailInfo;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseFragment;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.view.XEditText;
import com.jeremy.android.database.model.Card;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailInfoFragment extends BaseFragment implements DetailInfoContract.View {

    @BindView(R.id.edt_user_name)
    XEditText edtUserName;

    @BindView(R.id.edt_card_no)
    XEditText edtCardNo;

    @BindView(R.id.edt_user_phone)
    XEditText edtUserPhone;

    @BindView(R.id.edt_user_addr)
    XEditText edtUserAddr;

    @BindView(R.id.tv_card_expired)
    TextView tvCardExpired;

    @BindView(R.id.edt_memo)
    EditText edtMemo;

    @BindView(R.id.tv_point)
    TextView tvPoint;

    @BindView(R.id.tv_current_price)
    TextView tvCurrPrice;

    private Card mCard;

    private DatePickerDialog dpDialog;

    @Inject
    DetailInfoPresenter mPresenter;

    public static Fragment newInstance() {
        return new DetailInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_info, container, false);
        ButterKnife.bind(this, view);

        DaggerDetailInfoComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .detailInfoPresenterModule(new DetailInfoPresenterModule(this))
                .build()
                .inject(this);
        
        mCard = (Card) getArguments().getSerializable(BundleKeys.CARD);
        assert mCard != null;

        edtUserName.setText(mCard.userName);
        edtCardNo.setText(mCard.cardNo);
        edtUserPhone.setText(mCard.userPhone);
        edtUserAddr.setText(mCard.userAddr);
        edtMemo.setText(mCard.memo);
        tvPoint.setText(String.valueOf(mCard.userPoints));
        tvCurrPrice.setText(String.valueOf(mCard.cardBalance));

        mPresenter.subscribe();
        return view;

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

    public void updateCard() {
        String userName = edtUserName.getText().toString();
        String cardNo = edtCardNo.getText().toString();
        String userPhone = edtUserPhone.getText().toString();
        String userAddr = edtUserAddr.getText().toString();
        String memo = edtMemo.getText().toString();

        mPresenter.updateCard(mCard, userName, cardNo, userPhone, userAddr, memo);
    }

    @Override
    public void showCardNoAfterAutoSet(String cardNo) {
        edtCardNo.setText(cardNo);
    }

    @Override
    public void showCardPointsUpdated(float points) {
        mCard.userPoints = points;
        tvPoint.setText(String.valueOf(mCard.userPoints));
    }

    @Override
    public void showCardBalanceUpdated(float balance) {
        mCard.cardBalance = balance;
        tvCurrPrice.setText(String.valueOf(mCard.cardBalance));
    }

    @Override
    public void showExpiredTimeUpdated(String expiredTime) {
        tvCardExpired.setText(expiredTime);
    }

    @Override
    public void showUpdateCardMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public long getCardExpirdTime() {
        return mCard.cardExpired;
    }

    @Override
    public void initDpDialog(int year, int month, int dayOfMonth) {
        dpDialog = new DatePickerDialog(getActivity(), mDateSetListener, year, month, dayOfMonth);
    }

    @Override
    public void showCardList(CardItem newCardItem) {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.CARD_ITEM, newCardItem);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public Context getCtx() {
        return getContext();
    }

    @Override
    public void setPresenter(DetailInfoContract.Presenter presenter) {
        mPresenter = (DetailInfoPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return ((RxAppCompatActivity) getActivity()).bindToLifecycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}
