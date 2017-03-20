package com.jeremy.android.consumer.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.ConsumeBom;
import com.jeremy.android.consumer.view.AddAndSubView;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Jeremy on 2016/04/05.
 */
public class ConsumeBomsAdapter extends BaseMultiItemQuickAdapter<ConsumeBom, BaseViewHolder> {

    @Inject
    ConsumeBomsAdapter(List<ConsumeBom> data) {
        super(data);
        addItemType(ConsumeBom.ADD, R.layout.item_bom_add);
        addItemType(ConsumeBom.ITEM, R.layout.item_bom_consume);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConsumeBom item) {
        switch (helper.getItemViewType()) {
            case ConsumeBom.ADD:
                break;
            case ConsumeBom.ITEM:
                helper.setTag(R.id.layout, helper.getLayoutPosition());
                helper.setText(R.id.tv_bom_name, item.getBomName());
                helper.setText(R.id.tv_unit_price, String.valueOf(item.getUnitPrice()));
                helper.setText(R.id.tv_unit, String.valueOf(item.getUnit()));

                if (item.getUnit().equals(helper.convertView.getContext().getString(R.string.count))) {
                    helper.setVisible(R.id.asv, true);
                    helper.setVisible(R.id.edt_time, false);

                    ((AddAndSubView) helper.getView(R.id.asv)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                ((AddAndSubView) helper.getView(R.id.asv)).setEdtCount(1);
                                ((AddAndSubView) helper.getView(R.id.asv)).setEdtSelection(1);
                            } else {
                                int c = Integer.valueOf(s.toString());
                                float price = item.getUnitPrice() * c;
                                helper.setText(R.id.tv_price, String.format(Locale.getDefault(), "%.1f", price));
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            int iTag = (Integer) helper.getView(R.id.layout).getTag();
                            if (iTag == helper.getLayoutPosition()) {
                                String str = s.toString();
                                if (s.length() > 0) {
                                    float price = item.getUnitPrice() * Float.valueOf(str);
                                    item.setCount(Integer.valueOf(str));
                                    item.setPrice(Float.valueOf(String.format(Locale.getDefault(), "%.1f", price)));
                                }
                            }
                        }
                    });
                    ((AddAndSubView) helper.getView(R.id.asv)).setEdtCount(item.getCount());
                } else {
                    helper.setVisible(R.id.asv, false);
                    helper.setVisible(R.id.edt_time, true);

                    ((EditText) helper.getView(R.id.edt_time)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                helper.setText(R.id.edt_time, String.valueOf(0));
                                ((EditText) helper.getView(R.id.edt_time)).setSelection(1);
                            } else {
                                if (s.toString().contains(".")) {
                                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                                        s = s.toString().subSequence(0,
                                                s.toString().indexOf(".") + 3);
                                        helper.setText(R.id.edt_time, s);
                                        ((EditText) helper.getView(R.id.edt_time)).setSelection(s.length());
                                    }
                                }
                                if (s.toString().trim().equals(".")) {
                                    s = "0" + s;
                                    helper.setText(R.id.edt_time, s);
                                    ((EditText) helper.getView(R.id.edt_time)).setSelection(2);
                                } else {
                                    float price = item.getUnitPrice() * Float.valueOf(s.toString());
                                    helper.setText(R.id.tv_price, String.format(Locale.getDefault(), "%.1f", price));
                                }
                                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                                    if (!s.toString().substring(1, 2).equals(".")) {
                                        helper.setText(R.id.edt_time, s.subSequence(1, 2));
                                        ((EditText) helper.getView(R.id.edt_time)).setSelection(1);
                                    }
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            int iTag = (Integer) helper.getView(R.id.layout).getTag();
                            if (iTag == helper.getLayoutPosition()) {
                                String str = s.toString();
                                if (s.length() > 0) {
                                    float price = item.getUnitPrice() * Float.valueOf(str);
                                    item.setPrice(Float.valueOf(String.format(Locale.getDefault(), "%.1f", price)));
                                    item.setTime(str);
                                }
                            }
                        }
                    });

                    helper.setText(R.id.edt_time, item.getTime());
                }

                helper.setText(R.id.tv_price, String.valueOf(item.getPrice()));

                helper.addOnClickListener(R.id.tv_delete);
                break;
        }
    }
}