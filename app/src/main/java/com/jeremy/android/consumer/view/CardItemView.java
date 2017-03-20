package com.jeremy.android.consumer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.utils.TimeUtils;

/**
 * 用户卡片布局
 */
public class CardItemView extends LinearLayout {

    private TextView tvCardNo, tvUserName, tvUserPhone, tvCardExpired;
    private ImageView imgDelete;

    public CardItemView(Context context) {
        this(context, null);
    }

    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_item_card, this);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        tvCardNo = (TextView) findViewById(R.id.tv_card_no);
        tvCardExpired = (TextView) findViewById(R.id.tv_card_expired);
        imgDelete = (ImageView) findViewById(R.id.img_delete);
    }

    public ImageView getImgDelete() {
        return this.imgDelete;
    }

    public void setCardInfo(CardItem item) {
        tvUserName.setText(item.getUserName());
        tvUserPhone.setText(item.getUserPhone());
        tvCardNo.setText(item.getCardNo());
        tvCardExpired.setText(TimeUtils.getDateFormatByTimeStamp(item.getCardExpired()));
        if (item.isShowDeletes()) {
            imgDelete.setVisibility(VISIBLE);
        } else {
            imgDelete.setVisibility(GONE);
        }
    }
}
