package com.jeremy.android.consumer.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.database.model.Recharge;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jeremy on 2016/04/05.
 */
public class RechargeRecordsAdapter extends BaseQuickAdapter<Recharge, BaseViewHolder> {

    @Inject
    RechargeRecordsAdapter(List<Recharge> data) {
        super(R.layout.item_recharge_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recharge item) {
        helper.setText(R.id.tv_time, TimeUtils.getFormatByTimeStamp(item.chargeTime));
        helper.setText(R.id.tv_per_price, String.valueOf(item.chargeMoney));
        helper.setText(R.id.tv_memo, item.memo);
    }
}