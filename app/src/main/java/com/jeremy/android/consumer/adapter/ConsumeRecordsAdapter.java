package com.jeremy.android.consumer.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.utils.TimeUtils;
import com.jeremy.android.database.model.Consumption;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jeremy on 2016/04/05.
 */
public class ConsumeRecordsAdapter extends BaseQuickAdapter<Consumption, BaseViewHolder> {

    @Inject
    ConsumeRecordsAdapter(List<Consumption> data) {
        super(R.layout.item_consumption_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Consumption item) {
        helper.setText(R.id.tv_time, TimeUtils.getFormatByTimeStamp(item.payTime));
        helper.setText(R.id.tv_per_price, String.valueOf(item.payMoney));
        helper.setText(R.id.tv_bom_name, item.bomName);
    }
}