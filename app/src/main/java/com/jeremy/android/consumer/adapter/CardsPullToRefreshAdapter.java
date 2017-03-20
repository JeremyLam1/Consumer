package com.jeremy.android.consumer.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.view.CardItemView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Jeremy on 2017/2/7.
 */

public class CardsPullToRefreshAdapter extends BaseMultiItemQuickAdapter<CardItem, BaseViewHolder> {


    @Inject
    CardsPullToRefreshAdapter(List<CardItem> data) {
        super(data);
        addItemType(CardItem.ADD, R.layout.item_card_add);
        addItemType(CardItem.ITEM, R.layout.item_layout_carditemview);
    }

    @Override
    protected void convert(BaseViewHolder helper, CardItem item) {
        switch (helper.getItemViewType()) {
            case CardItem.ADD:
                break;
            case CardItem.ITEM:
                helper.addOnClickListener(R.id.img_delete);
                CardItemView itemView = (CardItemView) helper.getConvertView();
                itemView.setCardInfo(item);
                break;
        }
    }
}
