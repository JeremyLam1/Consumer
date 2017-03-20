package com.jeremy.android.consumer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jeremy.android.consumer.R;
import com.jeremy.android.database.model.Bom;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jeremy on 2016/04/05.
 */
public class BomDialogListAdapter extends RecyclerView.Adapter<BomDialogListAdapter.ViewHolder> {

    private List<Bom> items;

    private List<Boolean> checks;

    private Bom getValueAt(int position) {
        return items.get(position);
    }

    public BomDialogListAdapter(List<Bom> items) {
        this.items = items;
        checks = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            checks.add(false);
        }
    }

    public List<Boolean> getChecks() {
        return this.checks;
    }

    @Override
    public BomDialogListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bom_dialog, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BomDialogListAdapter.ViewHolder holder, final int position) {

        final Bom item = getValueAt(position);
        holder.cb_select.setChecked(checks.get(position));
        holder.tvBomName.setText(item.name);
        holder.tvUnitPrice.setText(item.price + "");
        holder.tvUnit.setText(item.unit);

        if (position % 2 == 0) {
            holder.convertView.setBackgroundResource(R.drawable.dark_bg_selector);
        } else {
            holder.convertView.setBackgroundResource(R.drawable.selector_light_gray_bg);
        }

        holder.cb_select.setOnCheckedChangeListener((buttonView, isChecked) -> checks.set(position, isChecked));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout)
        public View convertView;
        @BindView(R.id.cb_select)
        public CheckBox cb_select;
        @BindView(R.id.tv_bom_name)
        public TextView tvBomName;
        @BindView(R.id.tv_unit_price)
        public TextView tvUnitPrice;
        @BindView(R.id.tv_unit)
        public TextView tvUnit;

        public ViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }
    }
}