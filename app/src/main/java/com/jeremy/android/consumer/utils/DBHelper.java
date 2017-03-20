package com.jeremy.android.consumer.utils;

import android.database.Cursor;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Bom_Table;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Card_Table;
import com.jeremy.android.database.model.Consumption;
import com.jeremy.android.database.model.Consumption_Table;
import com.jeremy.android.database.model.Recharge;
import com.jeremy.android.database.model.Recharge_Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy on 2016/4/6.
 */
public class DBHelper {

    public static List<CardItem> getCardList(String search, long lastId, int limit) {
        List<CardItem> resultList = new ArrayList<>();
        List<Card> cardList;
        if (!TextUtils.isEmpty(search)) {//search by like
            List<SQLCondition> sqlConditionList = new ArrayList<>();
            sqlConditionList.add(Card_Table.userName.like("%" + search + "%"));
            sqlConditionList.add(Card_Table.userPhone.like("%" + search + "%"));
            sqlConditionList.add(Card_Table.cardNo.like("%" + search + "%"));
            cardList = SQLite.select().from(Card.class).where(Card_Table._id.lessThan(lastId)).and(Card_Table.userDelete.is(0)).and(ConditionGroup.clause().orAll(sqlConditionList)).limit(limit).orderBy(Card_Table._id, false).queryList();
        } else {
            cardList = SQLite.select().from(Card.class).where(Card_Table._id.lessThan(lastId)).and(Card_Table.userDelete.is(0)).limit(limit).orderBy(Card_Table._id, false).queryList();
        }
        for (Card card : cardList) {
            CardItem cardItem = CardItem.newItemInstance(card);
            resultList.add(cardItem);
        }
        return resultList;
    }

    public static void deleteCard(Long cardId) {
        Card card = SQLite.select().from(Card.class).where(Card_Table._id.eq(cardId)).querySingle();
        if (card != null) {
            card.userDelete = 1;
            card.update();
        }
    }

    public static List<Recharge> getRechargeListByCardId(Long cardId) {
        return SQLite.select().from(Recharge.class).where(Recharge_Table.cardId.eq(cardId)).orderBy(Recharge_Table._id, false).queryList();
    }

    public static List<Consumption> getConsumeListByCardId(Long cardId) {
        return SQLite.select().from(Consumption.class).where(Consumption_Table.cardId.eq(cardId)).orderBy(Consumption_Table._id, false).queryList();
    }

    public static Cursor getCardCursor(String search) {
        List<SQLCondition> sqlConditionList = new ArrayList<>();
        sqlConditionList.add(Card_Table.userName.like("%" + search + "%"));
        sqlConditionList.add(Card_Table.userPhone.like("%" + search + "%"));
        sqlConditionList.add(Card_Table.cardNo.like("%" + search + "%"));
        return SQLite.select().from(Card.class).where(Card_Table.userDelete.is(0)).and(ConditionGroup.clause().orAll(sqlConditionList)).orderBy(Card_Table._id, false).query();
    }

    public static String getCardNoByCardId(Long id) {
        Card card = SQLite.select().from(Card.class).where(Card_Table._id.eq(id)).querySingle();
        if (card != null) {
            return card.cardNo;
        }
        return "";
    }

    public static Card getCardByCardId(Long id) {
        return SQLite.select().from(Card.class).where(Card_Table._id.eq(id)).querySingle();
    }

    public static String getVaildCardNo(int offset) {
        String sMaxId = String.valueOf(getMaxCardId() + offset);
        StringBuilder sb = new StringBuilder();
        sb.append(sMaxId);
        for (int i = sMaxId.length(); i < 5; i++) {
            sb.insert(0, 0);
        }
        String cardNo = sb.toString();

        if (isCardNoExist(cardNo)) {
            return getVaildCardNo(offset + 1);
        } else {
            return cardNo;
        }
    }

    private static int getMaxCardId() {
        Card card = SQLite.select().from(Card.class).orderBy(Card_Table._id, false).querySingle();
        return card == null ? 1 : card._id.intValue();
    }

    public static boolean isCardNoExist(String cardNo) {
        Card card = SQLite.select().from(Card.class).where(Card_Table.cardNo.eq(cardNo)).querySingle();
        return card != null;
    }

    public static List<Bom> getBomSelectedList() {
        return SQLite.select().from(Bom.class).where(Bom_Table.selected.eq(1)).orderBy(Bom_Table._id, true).queryList();
    }

    public static List<Bom> getBomList() {
        return SQLite.select().from(Bom.class).orderBy(Bom_Table._id, true).queryList();
    }

    public static void updateBomList(List<Bom> items) {
        Delete.table(Bom.class);
        for (Bom bom : items) {
            if (bom != null) {
                bom.save();
            }
        }
    }

    public static Bom getBomById(Long bomId) {
        return SQLite.select().from(Bom.class).where(Bom_Table._id.eq(bomId)).querySingle();
    }

    public static boolean isBomNameExist(String bomName) {
        Bom bom = SQLite.select().from(Bom.class).where(Bom_Table.name.eq(bomName)).querySingle();
        return bom != null;
    }

    public static boolean isHasBom() {
        return SQLite.select().from(Bom.class).where(Bom_Table.selected.eq(1)).count() > 0;
    }

}
