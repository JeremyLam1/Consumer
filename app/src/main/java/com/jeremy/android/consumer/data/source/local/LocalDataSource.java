package com.jeremy.android.consumer.data.source.local;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.jeremy.android.consumer.data.source.DataSource;
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

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 本地缓存层
 * Created by Jeremy on 2017/1/23.
 */

public class LocalDataSource implements DataSource {

    @Inject
    public LocalDataSource() {
    }

    @Override
    public Flowable<Card> getCard(long cardId) {
        Card card = SQLite.select().from(Card.class).where(Card_Table._id.eq(cardId)).querySingle();
        if (card == null) {
            card = new Card();
            card.setValid(Card.CARD_INVALID);
        }
        return Flowable.just(card);
    }

    @Override
    public Flowable<List<Card>> getCards(long lastId, String search, int limit) {
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
        return Flowable.fromArray(cardList);
    }

    @Override
    public Observable<Boolean> checkCardNoExist(String cardNo) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Card card = SQLite.select().from(Card.class).where(Card_Table.cardNo.eq(cardNo)).querySingle();
                e.onNext(card != null);
                e.onComplete();
            }
        });
    }

    @Override
    public void saveCard(Card card) {
        card.save();
    }

    @Override
    public void updateCard(Card card) {
        card.update();
    }

    @Override
    public void refreshCards() {

    }

    @Override
    public void deleteAllCards() {
        List<Card> cards = SQLite.select().from(Card.class).queryList();
        for (Card card : cards) {
            card.userDelete = 1;
            card.update();
        }
    }

    @Override
    public void deleteCard(@NonNull long cardId) {
        Card card = SQLite.select().from(Card.class).where(Card_Table._id.eq(cardId)).querySingle();
        if (card != null) {
            card.userDelete = 1;
            card.update();
        }
    }

    @Override
    public Flowable<Recharge> getRecharge(long rechargeId) {
        Recharge recharge = SQLite.select().from(Recharge.class).where(Recharge_Table._id.eq(rechargeId)).querySingle();
        if (recharge == null) {
            recharge = new Recharge();
            recharge.setValid(Recharge.CARD_INVALID);
        }
        return Flowable.just(recharge);
    }

    @Override
    public Flowable<List<Recharge>> getRechargesByCardId(long cardId) {
        List<Recharge> rechargeList = SQLite.select().from(Recharge.class).where(Recharge_Table.cardId.eq(cardId)).orderBy(Recharge_Table._id, false).queryList();
        return Flowable.fromArray(rechargeList);
    }

    @Override
    public void saveRecharge(Recharge recharge) {
        recharge.save();
    }

    @Override
    public void deleteRecharge(@NonNull long rechargeId) {
        Recharge recharge = SQLite.select().from(Recharge.class).where(Recharge_Table._id.eq(rechargeId)).querySingle();
        if (recharge != null) {
            recharge.delete();
        }
    }

    @Override
    public Flowable<Consumption> getConsumption(long consumptionId) {
        Consumption consumption = SQLite.select().from(Consumption.class).where(Consumption_Table._id.eq(consumptionId)).querySingle();
        if (consumption == null) {
            consumption = new Consumption();
            consumption.setValid(Consumption.CARD_INVALID);
        }
        return Flowable.just(consumption);
    }

    @Override
    public Flowable<List<Consumption>> getConsumptionsByCardId(long cardId) {
        List<Consumption> consumptionList = SQLite.select().from(Consumption.class).where(Consumption_Table.cardId.eq(cardId)).orderBy(Consumption_Table._id, false).queryList();
        return Flowable.fromArray(consumptionList);
    }

    @Override
    public void saveConsumption(Consumption consumption) {
        consumption.save();
    }

    @Override
    public void deleteConsumption(@NonNull long consumptionId) {
        Consumption consumption = SQLite.select().from(Consumption.class).where(Consumption_Table._id.eq(consumptionId)).querySingle();
        if (consumption != null) {
            consumption.delete();
        }
    }

    @Override
    public Flowable<Bom> getBom(long bomId) {
        Bom bom = SQLite.select().from(Bom.class).where(Bom_Table._id.eq(bomId)).querySingle();
        if (bom == null) {
            bom = new Bom();
            bom.setValid(Bom.CARD_INVALID);
        }
        return Flowable.just(bom);
    }

    @Override
    public Flowable<List<Bom>> getBoms() {
        List<Bom> bomList = SQLite.select().from(Bom.class).orderBy(Bom_Table._id, true).queryList();
        return Flowable.fromArray(bomList);
    }

    @Override
    public Observable<Boolean> checkBomNameExist(String name) {
        return Observable.create(e -> {
            Bom bom = SQLite.select().from(Bom.class).where(Bom_Table.name.eq(name)).querySingle();
            e.onNext(bom != null);
            e.onComplete();
        });
    }

    @Override
    public void saveBom(Bom bom) {
        bom.save();
    }

    @Override
    public void deleteBom(@NonNull long bomId) {
        Bom bom = SQLite.select().from(Bom.class).where(Bom_Table._id.eq(bomId)).querySingle();
        if (bom != null) {
            bom.delete();
        }
    }

    @Override
    public void updateBom(Bom bom) {
        bom.update();
    }
}
