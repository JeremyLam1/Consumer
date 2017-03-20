package com.jeremy.android.consumer.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jeremy.android.consumer.R;
import com.jeremy.android.database.model.Bom;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jeremy on 2017/2/17.
 */

public class BomSettingsAdapter extends BaseQuickAdapter<Bom, BaseViewHolder> {

    @Inject
    BomSettingsAdapter(List<Bom> data) {
        super(R.layout.item_bom_setting, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Bom item) {
        helper.setChecked(R.id.cb_select, item.selected == 1);

        helper.setText(R.id.tv_bom_name, item.name);
        helper.setText(R.id.tv_unit_price, item.price + "");
        helper.setText(R.id.tv_unit, item.unit);

        helper.addOnClickListener(R.id.cb_select);
        helper.addOnClickListener(R.id.img_delete);
    }
}
