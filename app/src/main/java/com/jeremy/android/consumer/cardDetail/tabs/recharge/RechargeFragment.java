package com.jeremy.android.consumer.cardDetail.tabs.recharge;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseFragment;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.view.MoneyTextWatcher;
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
public class RechargeFragment extends BaseFragment implements RechargeContract.View {

    @BindView(R.id.tv_customer)
    TextView tvCustomer;

    @BindView(R.id.tv_card_no)
    TextView tvCardNo;

    @BindView(R.id.tv_current_price)
    TextView tvBalance;

    @BindView(R.id.edt_recharge_price)
    EditText edtPrice;

    @BindView(R.id.edt_memo)
    EditText edtMemo;

    @BindView(R.id.btn_recharge)
    CircularProgressButton btnRecharge;

    @Inject
    RechargePresenter mPresenter;

    private Card mCard;

    public static Fragment newInstance() {
        return new RechargeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge, container, false);
        ButterKnife.bind(this, view);

        DaggerRechargeComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .rechargePresenterModule(new RechargePresenterModule(this))
                .build()
                .inject(this);

        mCard = (Card) getArguments().getSerializable(BundleKeys.CARD);
        assert mCard != null;

        tvCustomer.setText(mCard.userName);
        tvCardNo.setText(mCard.cardNo);
        tvBalance.setText(String.valueOf(mCard.cardBalance));
        edtPrice.addTextChangedListener(new MoneyTextWatcher(edtPrice));

        mPresenter.subscribe();
        return view;
    }

    @OnClick(R.id.btn_recharge)
    public void addRecharge() {
        if (getRechargeButtonProgress() == 0) {
            showRechargeButtonLoading();
            showRechargeConfirmDialog();
        } else {
            showRechargeButtonNormal();
        }
    }

    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public int getRechargeButtonProgress() {
        return btnRecharge.getProgress();
    }

    @Override
    public void showRechargeSuccess() {
        edtPrice.setText("");
        edtMemo.setText("");
        btnRecharge.setProgress(100);
    }

    @Override
    public void showRechargeButtonError() {
        btnRecharge.setProgress(-1);
    }

    @Override
    public void showRechargeButtonLoading() {
        btnRecharge.setIndeterminateProgressMode(true);
        btnRecharge.setProgress(50);
    }

    @Override
    public void showRechargeButtonNormal() {
        btnRecharge.setProgress(0);
    }

    @Override
    public void showRechargeConfirmDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.recharge_or_not));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> btnRecharge.setProgress(0));
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String rechargeMoney = edtPrice.getText().toString();
            String memo = edtMemo.getText().toString();
            mPresenter.doRecharge(mCard, rechargeMoney, memo);
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void showCardBalanceUpdate(float balance) {
        mCard.cardBalance = balance;
        tvBalance.setText(String.valueOf(mCard.cardBalance));
    }

    @Override
    public Context getCtx() {
        return getContext();
    }

    @Override
    public void setPresenter(RechargeContract.Presenter presenter) {
        mPresenter = (RechargePresenter) presenter;
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
