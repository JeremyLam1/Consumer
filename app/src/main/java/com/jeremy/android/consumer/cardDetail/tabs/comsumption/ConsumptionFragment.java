package com.jeremy.android.consumer.cardDetail.tabs.comsumption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dd.CircularProgressButton;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.adapter.BomDialogListAdapter;
import com.jeremy.android.consumer.adapter.ConsumeBomsAdapter;
import com.jeremy.android.consumer.base.BaseFragment;
import com.jeremy.android.consumer.data.bean.ConsumeBom;
import com.jeremy.android.consumer.setting.BomSetting.BomSettingActivity;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.view.DividerItemDecoration;
import com.jeremy.android.consumer.view.FullyLinearLayoutManager;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumptionFragment extends BaseFragment implements ConsumptionContract.View {

    @BindView(R.id.tv_customer)
    TextView tvCustomer;

    @BindView(R.id.tv_card_no)
    TextView tvCardNo;

    @BindView(R.id.tv_current_price)
    TextView tvBalance;

    @BindView(R.id.edt_memo)
    EditText edtMemo;

    @BindView(R.id.rcV_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_consume)
    CircularProgressButton btnConsume;

    @Inject
    ConsumeBomsAdapter mAdapter;

    @Inject
    ConsumptionPresenter mPresenter;

    private Card mCard;

    public static Fragment newInstance() {
        return new ConsumptionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumption, container, false);
        ButterKnife.bind(this, view);

        DaggerConsumptionComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .consumeBomListModule(new ConsumeBomListModule())
                .consumptionPresenterModule(new ConsumptionPresenterModule(this))
                .build()
                .inject(this);

        mCard = (Card) getArguments().getSerializable(BundleKeys.CARD);
        assert mCard != null;

        tvCustomer.setText(mCard.userName);
        tvCardNo.setText(mCard.cardNo);
        tvBalance.setText(String.valueOf(mCard.cardBalance));
        initRecyclerView();

        mPresenter.subscribe();
        return view;
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(0);
        dividerItemDecoration.setDividerColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == ConsumeBom.ADD) {
                    mPresenter.loadEnableBoms();
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_delete:
                        showDeleteBomDialog(position);
                        break;
                }
            }
        });
    }

    private void showDeleteBomDialog(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.sure_to_delete));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> mPresenter.deleteBom(position));
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @OnClick(R.id.btn_consume)
    public void addConsumption() {
        mPresenter.calculatePrice();
    }

    @Override
    public void showNoEnableBomsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.hint));
        alert.setMessage(getString(R.string.invalid_boms_and_go_to_setting));
        alert.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });
        alert.setPositiveButton(R.string.go, (dialog, which) -> mPresenter.setBoms());
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void showBoms(List<Bom> boms) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.select_bom));
        final View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_bom_list, null, false);
        RecyclerView mRecyclerView = ButterKnife.findById(view, R.id.rcV_list);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(0);
        dividerItemDecoration.setDividerColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        final BomDialogListAdapter _mAdapter = new BomDialogListAdapter(boms);

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(_mAdapter);
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> mPresenter.addBoms(boms, _mAdapter.getChecks()));
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.show();
    }


    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public int getConsumeButtonProgress() {
        return btnConsume.getProgress();
    }

    @Override
    public void showConsumeSuccess() {
        mAdapter.getData().clear();
        mAdapter.getData().add(0, ConsumeBom.newAddInstance());
        mAdapter.notifyDataSetChanged();
        edtMemo.setText("");
        btnConsume.setProgress(100);
    }

    @Override
    public void showConsumeButtonError() {
        btnConsume.setProgress(-1);
    }

    @Override
    public void showConsumeButtonLoading() {
        btnConsume.setIndeterminateProgressMode(true);
        btnConsume.setProgress(50);
    }

    @Override
    public void showConsumeButtonNormal() {
        btnConsume.setProgress(0);
    }

    @Override
    public void showConsumeConfirmDialog(float needPayPrice) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.the_sum_money_of_consumption_are) + needPayPrice + getString(R.string.yuan) + "，" + getString(R.string.consume_or_not));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> showConsumeButtonNormal());
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String memo = edtMemo.getText().toString();
            mPresenter.doConsume(needPayPrice, mCard, memo);
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void showBomsDeleted(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBomsAdd() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBomsSettingPage() {
        Intent intent = new Intent(getActivity(), BomSettingActivity.class);
        startActivity(intent);
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
    public List<ConsumeBom> getConsumeBoms() {
        return mAdapter.getData();
    }

    @Override
    public void setPresenter(ConsumptionContract.Presenter presenter) {
        mPresenter = (ConsumptionPresenter) presenter;
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
