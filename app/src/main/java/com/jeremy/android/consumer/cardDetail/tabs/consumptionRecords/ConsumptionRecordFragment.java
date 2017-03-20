package com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.adapter.ConsumeRecordsAdapter;
import com.jeremy.android.consumer.base.BaseFragment;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.consumer.view.DividerItemDecoration;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumptionRecordFragment extends BaseFragment implements ConsumptionRecordContract.View {

    @BindView(R.id.rcV_list)
    RecyclerView mRecyclerView;

    @Inject
    ConsumeRecordsAdapter mAdapter;

    @Inject
    ConsumptionRecordPresenter mPresenter;

    private Card mCard;

    public static Fragment newInstance() {
        return new ConsumptionRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumption_record, container, false);
        ButterKnife.bind(this, view);

        DaggerConsumptionRecordComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .consumptionRecordListModule(new ConsumptionRecordListModule())
                .consumptionRecordPresenterModule(new ConsumptionRecordPresenterModule(this))
                .build()
                .inject(this);

        mCard = (Card) getArguments().getSerializable(BundleKeys.CARD);
        assert mCard != null;

        initRecyclerView();

        mPresenter.subscribe();
        mPresenter.loadConsumptions(mCard._id);

        return view;
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(0);
        dividerItemDecoration.setDividerColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Consumption consumption = mAdapter.getData().get(position);
                mPresenter.openConsumptionDetail(consumption._id);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Consumption consumption = mAdapter.getData().get(position);
                showDeleteDialog(position, consumption);
            }
        });
    }


    @Override
    public void showConsumptions(List<Consumption> consumptions) {
        mAdapter.setNewData(consumptions);
    }

    @Override
    public void showConsumptionDetail(Consumption consumption) {
        showDetailDialog(consumption);
    }

    @Override
    public void showConsumptionDeleted(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showConsumptionAdded(Consumption consumption) {
        mAdapter.getData().add(consumption);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void setPresenter(ConsumptionRecordContract.Presenter presenter) {
        mPresenter = (ConsumptionRecordPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return ((RxAppCompatActivity) getActivity()).bindToLifecycle();
    }

    private void showDetailDialog(Consumption item) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.record_detail));
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_record_consume_detail, null, false);
        alert.setView(view);

        TextView tvCardNo = ButterKnife.findById(view, R.id.tv_card_no);
        TextView tvBomName = ButterKnife.findById(view, R.id.tv_bom_name);
        TextView tvUnitPrice = ButterKnife.findById(view, R.id.tv_bom_unitPrice);
        TextView tvConsumeTime = ButterKnife.findById(view, R.id.tv_consume_time);
        LinearLayout layoutCount = ButterKnife.findById(view, R.id.layout_count);
        LinearLayout layoutUseTime = ButterKnife.findById(view, R.id.layout_useTime);
        TextView tvConsumeCount = ButterKnife.findById(view, R.id.tv_consume_count);
        TextView tvConsumeUseTime = ButterKnife.findById(view, R.id.tv_consume_useTime);
        TextView tvConsumePrice = ButterKnife.findById(view, R.id.tv_consume_price);
        TextView tvMemo = ButterKnife.findById(view, R.id.tv_memo);

        tvCardNo.setText(mCard.cardNo);
        tvBomName.setText(item.bomName);
        tvUnitPrice.setText(String.valueOf(item.bomUnitPrice));
        if (item.unit.equals(getString(R.string.count))) {
            layoutCount.setVisibility(View.VISIBLE);
            layoutUseTime.setVisibility(View.GONE);
            tvConsumeCount.setText(String.valueOf(item.payCount));
        } else {
            layoutCount.setVisibility(View.GONE);
            layoutUseTime.setVisibility(View.VISIBLE);
            tvConsumeUseTime.setText(String.valueOf(item.payTimeLength));
        }
        tvConsumeTime.setText(TimeUtils.getFormatByTimeStamp(item.payTime));
        tvConsumePrice.setText(String.valueOf(item.payMoney));
        tvMemo.setText(item.memo);

        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
        });
        android.app.AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void showDeleteDialog(int position, Consumption consumption) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        alert.setMessage(getString(R.string.sure_to_delete));
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> mPresenter.deleteConsumption(position, consumption._id));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        android.app.AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}
