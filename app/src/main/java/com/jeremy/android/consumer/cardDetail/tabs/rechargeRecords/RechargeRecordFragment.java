package com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.adapter.RechargeRecordsAdapter;
import com.jeremy.android.consumer.base.BaseFragment;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.consumer.view.DividerItemDecoration;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Recharge;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RechargeRecordFragment extends BaseFragment implements RechargeRecordContract.View {

    @BindView(R.id.rcV_list)
    RecyclerView mRecyclerView;

    @Inject
    RechargeRecordsAdapter mAdapter;

    @Inject
    RechargeRecordPresenter mPresenter;

    private Card mCard;

    public static Fragment newInstance() {
        return new RechargeRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge_record, container, false);
        ButterKnife.bind(this, view);

        DaggerRechargeRecordComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .rechargeRecordListModule(new RechargeRecordListModule())
                .rechargeRecordPresenterModule(new RechargeRecordPresenterModule(this))
                .build()
                .inject(this);

        mCard = (Card) getArguments().getSerializable(BundleKeys.CARD);
        assert mCard != null;

        initRecyclerView();

        mPresenter.subscribe();
        mPresenter.loadRecharges(mCard._id);

        return view;
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(0);
        dividerItemDecoration.setDividerColor(getResources().getColor(R.color.colorPrimary));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Recharge recharge = mAdapter.getData().get(position);
                mPresenter.openRechargeDetail(recharge._id);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Recharge recharge = mAdapter.getData().get(position);
                showDeleteDialog(position, recharge);
            }
        });
    }

    @Override
    public void showRecharges(List<Recharge> recharges) {
        mAdapter.setNewData(recharges);
    }

    @Override
    public void showRechargeDetail(Recharge recharge) {
        showDetailDialog(recharge);
    }

    @Override
    public void showRechargeDeleted(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRechargeAdded(Recharge recharge) {
        mAdapter.getData().add(recharge);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void setPresenter(RechargeRecordContract.Presenter presenter) {
        mPresenter = (RechargeRecordPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return ((RxAppCompatActivity) getActivity()).bindToLifecycle();
    }

    private void showDetailDialog(Recharge item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.record_detail));
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_record_recharge_detail, null, false);
        alert.setView(view);

        TextView tvCardNo = ButterKnife.findById(view, R.id.tv_card_no);
        TextView tvRechargeTime = ButterKnife.findById(view, R.id.tv_recharge_time);
        TextView tvRechargePrice = ButterKnife.findById(view, R.id.tv_recharge_price);
        TextView tvMemo = ButterKnife.findById(view, R.id.tv_memo);

        tvCardNo.setText(mCard.cardNo);
        tvRechargeTime.setText(TimeUtils.getFormatByTimeStamp(item.chargeTime));
        tvRechargePrice.setText(String.valueOf(item.chargeMoney));
        tvMemo.setText(item.memo);

        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void showDeleteDialog(int position, Recharge recharge) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.sure_to_delete));
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> mPresenter.deleteRecharge(position, recharge._id));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}
