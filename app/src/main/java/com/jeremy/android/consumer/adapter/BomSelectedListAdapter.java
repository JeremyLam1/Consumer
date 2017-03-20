package com.jeremy.android.consumer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeremy.android.consumer.R;
import com.jeremy.android.database.model.Bom;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jeremy on 2016/04/05.
 */
public class BomSelectedListAdapter extends RecyclerView.Adapter<BomSelectedListAdapter.ViewHolder> {

    private List<Bom> items;

    private Bom getValueAt(int position) {
        return items.get(position);
    }

    public BomSelectedListAdapter(List<Bom> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bom_selected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Bom item = getValueAt(position);

        holder.tvBomName.setText(item.name);
        holder.tvUnitPrice.setText(item.price + "");
        holder.tvUnit.setText(item.unit);

        if (position % 2 == 0) {
            holder.convertView.setBackgroundResource(R.drawable.dark_bg_selector);
        } else {
            holder.convertView.setBackgroundResource(R.drawable.selector_light_gray_bg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout)
        public View convertView;
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