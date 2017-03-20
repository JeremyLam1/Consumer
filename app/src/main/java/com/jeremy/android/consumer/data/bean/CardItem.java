package com.jeremy.android.consumer.data.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jeremy.android.database.model.Card;

import java.io.Serializable;

/**
 * Created by Jeremy on 2017/2/7.
 */

public class CardItem implements MultiItemEntity, Serializable {

    public static final int ADD = 1;
    public static final int ITEM = 2;
    private int itemType;

    private Long cardId;
    private String userName;
    private String userPhone;
    private String cardNo;
    private Long cardExpired;

    private boolean isShowDeletes;

    private CardItem(int itemType) {
        setItemType(itemType);
    }

    private CardItem(int itemType, Card card) {
        setItemType(itemType);
        setCardId(card._id);
        setUserName(card.userName);
        setUserPhone(card.userPhone);
        setCardNo(card.cardNo);
        setCardExpired(card.cardExpired);
        setShowDeletes(false);
    }

    public static CardItem newAddInstance() {
        return new CardItem(ADD);
    }

    public static CardItem newItemInstance(Card card) {
        return new CardItem(ITEM, card);
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Long getCardExpired() {
        return cardExpired;
    }

    public void setCardExpired(Long cardExpired) {
        this.cardExpired = cardExpired;
    }

    public boolean isShowDeletes() {
        return isShowDeletes;
    }

    public void setShowDeletes(boolean showDeletes) {
        isShowDeletes = showDeletes;
    }
}
