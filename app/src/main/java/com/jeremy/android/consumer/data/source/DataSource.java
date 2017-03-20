package com.jeremy.android.consumer.data.source;

import android.support.annotation.NonNull;

import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;
import com.jeremy.android.database.model.Recharge;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by Jeremy on 2017/1/23.
 */

public interface DataSource {

    /***************************************/
    Flowable<Card> getCard(long cardId);

    Flowable<List<Card>> getCards(long lastId, String search, int limit);

    Observable<Boolean> checkCardNoExist(String cardNo);

    void saveCard(Card card);

    void updateCard(Card card);

    void refreshCards();

    void deleteAllCards();

    void deleteCard(@NonNull long cardId);


    /***************************************/
    Flowable<Recharge> getRecharge(long rechargeId);

    Flowable<List<Recharge>> getRechargesByCardId(long cardId);

    void saveRecharge(Recharge recharge);

    void deleteRecharge(@NonNull long rechargeId);


    /***************************************/
    Flowable<Consumption> getConsumption(long consumptionId);

    Flowable<List<Consumption>> getConsumptionsByCardId(long cardId);

    void saveConsumption(Consumption consumption);

    void deleteConsumption(@NonNull long consumptionId);


    /***************************************/
    Flowable<Bom> getBom(long bomId);

    Flowable<List<Bom>> getBoms();

    Observable<Boolean> checkBomNameExist(String name);

    void saveBom(Bom bom);

    void deleteBom(@NonNull long bomId);

    void updateBom(Bom bom);
}
