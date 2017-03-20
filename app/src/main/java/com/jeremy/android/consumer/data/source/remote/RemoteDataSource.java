package com.jeremy.android.consumer.data.source.remote;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.jeremy.android.consumer.data.source.DataSource;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;
import com.jeremy.android.database.model.Recharge;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * 网络数据层
 * Created by Jeremy on 2017/1/23.
 */
public class RemoteDataSource implements DataSource {

    private ArrayMap<Long, Card> cardsMap;

    private ArrayMap<Long, Recharge> rechargesMap;

    private ArrayMap<Long, Consumption> consumptionsMap;

    private ArrayMap<Long, Bom> bomsMap;

    @Inject
    public RemoteDataSource() {
        cardsMap = new ArrayMap<>();
        rechargesMap = new ArrayMap<>();
        consumptionsMap = new ArrayMap<>();
        bomsMap = new ArrayMap<>();
    }

    @Override
    public Flowable<Card> getCard(long cardId) {
        Card card = cardsMap.get(cardId);
        if (card == null) {
            card = new Card();
            card.setValid(Card.CARD_INVALID);
        }
        return Flowable.just(card);
    }

    @Override
    public Flowable<List<Card>> getCards(long lastId, String search, int limit) {
        return Flowable.just(new ArrayList<>(cardsMap.values()));
    }

    @Override
    public Observable<Boolean> checkCardNoExist(String cardNo) {
        return Observable.create(e -> {
            e.onNext(true);
            e.onComplete();
        });
    }

    @Override
    public void saveCard(Card card) {
        cardsMap.put(card._id, card);
    }

    @Override
    public void updateCard(Card card) {
        cardsMap.put(card._id, card);
    }

    @Override
    public void refreshCards() {

    }

    @Override
    public void deleteAllCards() {
        cardsMap.clear();
    }

    @Override
    public void deleteCard(@NonNull long cardId) {
        cardsMap.remove(cardId);
    }

    @Override
    public Flowable<Recharge> getRecharge(long rechargeId) {
        return Flowable.just(rechargesMap.get(rechargeId));
    }

    @Override
    public Flowable<List<Recharge>> getRechargesByCardId(long cardId) {
        return Flowable.just(new ArrayList<>(rechargesMap.values()));
    }

    @Override
    public void saveRecharge(Recharge recharge) {
        rechargesMap.put(recharge._id, recharge);
    }

    @Override
    public void deleteRecharge(@NonNull long rechargeId) {
        rechargesMap.remove(rechargeId);
    }

    @Override
    public Flowable<Consumption> getConsumption(long consumptionId) {
        return Flowable.just(consumptionsMap.get(consumptionId));
    }

    @Override
    public Flowable<List<Consumption>> getConsumptionsByCardId(long cardId) {
        return Flowable.just(new ArrayList<>(consumptionsMap.values()));
    }

    @Override
    public void saveConsumption(Consumption consumption) {
        consumptionsMap.put(consumption._id, consumption);
    }

    @Override
    public void deleteConsumption(@NonNull long consumptionId) {
        consumptionsMap.remove(consumptionId);
    }

    @Override
    public Flowable<Bom> getBom(long bomId) {
        return Flowable.just(bomsMap.get(bomId));
    }

    @Override
    public Flowable<List<Bom>> getBoms() {
        return Flowable.just(new ArrayList<>(bomsMap.values()));
    }

    @Override
    public Observable<Boolean> checkBomNameExist(String name) {
        return Observable.create(e -> {
            e.onNext(true);
            e.onComplete();
        });
    }

    @Override
    public void saveBom(Bom bom) {
        bomsMap.put(bom._id, bom);
    }

    @Override
    public void deleteBom(@NonNull long bomId) {
        bomsMap.remove(bomId);
    }

    @Override
    public void updateBom(Bom bom) {
        bomsMap.put(bom._id, bom);
    }
}
