package com.jeremy.android.consumer.data.source;

import android.support.annotation.NonNull;

import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;
import com.jeremy.android.database.model.Recharge;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by Jeremy on 2017/1/23.
 */

public class DataRepository implements DataSource {

    private DataSource remoteDataSource;
    private DataSource localDataSource;

    private boolean mCacheIsDirty = false;

    @Inject
    DataRepository(@Remote DataSource remoteDataSource, @Local DataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }

    @Override
    public Flowable<Card> getCard(long cardId) {

        Flowable<Card> localCard = getLocalCardWithId(cardId);
        Flowable<Card> remoteCard = getRemoteCardWithId(cardId);

        return Flowable.concat(localCard, remoteCard)
                .firstElement()
                .toFlowable()
                .map(card -> {
                    if (!card.isValid()) {
                        throw new NoSuchElementException("No card found with cardId " + cardId);
                    }
                    return card;
                });
    }

    private Flowable<Card> getLocalCardWithId(long cardId) {
        return localDataSource.getCard(cardId);
    }

    private Flowable<Card> getRemoteCardWithId(long cardId) {
        return remoteDataSource.getCard(cardId)
                .filter(Card::isValid)
                .doOnNext(card -> localDataSource.saveCard(card));
    }

    @Override
    public Flowable<List<Card>> getCards(long lastId, String search, int limit) {

        Flowable<List<Card>> remoteCards = getAndSaveRemoteCards(lastId, search, limit);
        if (!mCacheIsDirty) {
            //取网络数据
            return remoteCards;
        } else {
            //取本地数据
            Flowable<List<Card>> localCards = getLocalCards(lastId, search, limit);
            return Flowable.concat(localCards, remoteCards)
//                    .filter(cards -> !cards.isEmpty())
                    .firstElement()
                    .toFlowable();
        }
    }

    @Override
    public Observable<Boolean> checkCardNoExist(String cardNo) {
//        Observable<Boolean> localCheck = checkLocalCardNoExist(name);
//        Observable<Boolean> remoteCheck = checkRemoteCardNoExist(name);

//        return Observable.concat(localCheck, remoteCheck)
//                .filter(aBoolean -> aBoolean)
//                .firstElement()
//                .toObservable();
        return checkLocalCardNoExist(cardNo);
    }

    private Observable<Boolean> checkLocalCardNoExist(String cardNo) {
        return localDataSource.checkCardNoExist(cardNo);
    }

    private Observable<Boolean> checkRemoteCardNoExist(String cardNo) {
        return remoteDataSource.checkCardNoExist(cardNo);
    }


    private Flowable<List<Card>> getLocalCards(long lastId, String search, int limit) {
        return localDataSource.getCards(lastId, search, limit)
                .doOnComplete(() -> mCacheIsDirty = false);
    }

    private Flowable<List<Card>> getAndSaveRemoteCards(long lastId, String search, int limit) {
        return remoteDataSource.getCards(lastId, search, limit)
                .flatMap(new Function<List<Card>, Flowable<List<Card>>>() {
                    @Override
                    public Flowable<List<Card>> apply(List<Card> cards) throws Exception {
                        return Flowable.fromIterable(cards)
                                .doOnNext(card -> localDataSource.saveCard(card))
                                .toList()
                                .toFlowable();
                    }
                }).doOnComplete(() -> mCacheIsDirty = false);
    }

    @Override
    public void saveCard(Card card) {
        localDataSource.saveCard(card);
        remoteDataSource.saveCard(card);
    }

    @Override
    public void updateCard(Card card) {
        card.update();
    }

    @Override
    public void refreshCards() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllCards() {
        localDataSource.deleteAllCards();
        remoteDataSource.deleteAllCards();
    }

    @Override
    public void deleteCard(@NonNull long cardId) {
        localDataSource.deleteCard(cardId);
        remoteDataSource.deleteCard(cardId);
    }

    /***************************************************/
    @Override
    public Flowable<Recharge> getRecharge(long rechargeId) {
        Flowable<Recharge> localRecharge = getLocalRechargeWithId(rechargeId);
        Flowable<Recharge> remoteRecharge = getRemoteRechargeWithId(rechargeId);

        return Flowable.concat(localRecharge, remoteRecharge)
                .firstElement()
                .toFlowable()
                .map(recharge -> {
                    if (recharge == null) {
                        throw new NoSuchElementException("No recharge found with recharge " + rechargeId);
                    }
                    return recharge;
                });
    }

    private Flowable<Recharge> getLocalRechargeWithId(long rechargeId) {
        return localDataSource.getRecharge(rechargeId);
    }

    private Flowable<Recharge> getRemoteRechargeWithId(long rechargeId) {
        return remoteDataSource.getRecharge(rechargeId)
                .doOnNext(recharge -> localDataSource.saveRecharge(recharge));
    }

    @Override
    public Flowable<List<Recharge>> getRechargesByCardId(long cardId) {
        Flowable<List<Recharge>> localRecharges = getLocalRechargesByCardId(cardId);
        Flowable<List<Recharge>> remoteRecharges = getAndSaveRemoteRechargesByCardId(cardId);

        return Flowable.concat(localRecharges, remoteRecharges)
                .filter(recharges -> !recharges.isEmpty())
                .firstElement()
                .toFlowable();
    }

    private Flowable<List<Recharge>> getLocalRechargesByCardId(long cardId) {
        return localDataSource.getRechargesByCardId(cardId);
    }

    private Flowable<List<Recharge>> getAndSaveRemoteRechargesByCardId(long cardId) {
        return remoteDataSource.getRechargesByCardId(cardId)
                .flatMap(new Function<List<Recharge>, Publisher<List<Recharge>>>() {
                    @Override
                    public Publisher<List<Recharge>> apply(List<Recharge> recharges) throws Exception {
                        return Flowable.fromIterable(recharges)
                                .doOnNext(recharge -> localDataSource.saveRecharge(recharge))
                                .toList()
                                .toFlowable();
                    }
                });
    }

    @Override
    public void saveRecharge(Recharge recharge) {
        localDataSource.saveRecharge(recharge);
        remoteDataSource.saveRecharge(recharge);
    }

    @Override
    public void deleteRecharge(@NonNull long rechargeId) {
        localDataSource.deleteRecharge(rechargeId);
        remoteDataSource.deleteRecharge(rechargeId);
    }

    /***************************************************/
    @Override
    public Flowable<Consumption> getConsumption(long consumptionId) {
        Flowable<Consumption> localConsumption = getLocalConsumptionWithId(consumptionId);
        Flowable<Consumption> remoteConsumption = getRemoteConsumptionWithId(consumptionId);

        return Flowable.concat(localConsumption, remoteConsumption)
                .firstElement()
                .toFlowable()
                .map(consumption -> {
                    if (consumption == null) {
                        throw new NoSuchElementException("No consumption found with consumption " + consumptionId);
                    }
                    return consumption;
                });
    }

    private Flowable<Consumption> getLocalConsumptionWithId(long consumptionId) {
        return localDataSource.getConsumption(consumptionId);
    }

    private Flowable<Consumption> getRemoteConsumptionWithId(long consumptionId) {
        return remoteDataSource.getConsumption(consumptionId)
                .doOnNext(consumption -> localDataSource.saveConsumption(consumption));
    }

    @Override
    public Flowable<List<Consumption>> getConsumptionsByCardId(long cardId) {
        Flowable<List<Consumption>> localConsumptions = getLocalConsumptionsByCardId(cardId);
        Flowable<List<Consumption>> remoteConsumptions = getAndSaveRemoteConsumptionsByCardId(cardId);

        return Flowable.concat(localConsumptions, remoteConsumptions)
//                .filter(consumptions -> !consumptions.isEmpty())
                .firstElement()
                .toFlowable();
    }

    private Flowable<List<Consumption>> getLocalConsumptionsByCardId(long cardId) {
        return localDataSource.getConsumptionsByCardId(cardId);
    }

    private Flowable<List<Consumption>> getAndSaveRemoteConsumptionsByCardId(long cardId) {
        return remoteDataSource.getConsumptionsByCardId(cardId)
                .flatMap(new Function<List<Consumption>, Publisher<List<Consumption>>>() {
                    @Override
                    public Publisher<List<Consumption>> apply(List<Consumption> consumptions) throws Exception {
                        return Flowable.fromIterable(consumptions)
                                .doOnNext(consumption -> localDataSource.saveConsumption(consumption))
                                .toList()
                                .toFlowable();
                    }
                });
    }

    @Override
    public void saveConsumption(Consumption consumption) {
        localDataSource.saveConsumption(consumption);
        remoteDataSource.saveConsumption(consumption);
    }

    @Override
    public void deleteConsumption(@NonNull long consumptionId) {
        localDataSource.deleteConsumption(consumptionId);
        remoteDataSource.deleteConsumption(consumptionId);
    }

    /***************************************************/
    @Override
    public Flowable<Bom> getBom(long bomId) {
        Flowable<Bom> localBom = getLocalBomWithId(bomId);
        Flowable<Bom> remoteBom = getRemoteBomWithId(bomId);

        return Flowable.concat(localBom, remoteBom)
                .firstElement()
                .toFlowable()
                .map(bom -> {
                    if (bom == null) {
                        throw new NoSuchElementException("No bom found with bom " + bomId);
                    }
                    return bom;
                });
    }

    private Flowable<Bom> getLocalBomWithId(long bomId) {
        return localDataSource.getBom(bomId);
    }

    private Flowable<Bom> getRemoteBomWithId(long bomId) {
        return remoteDataSource.getBom(bomId)
                .doOnNext(bom -> localDataSource.saveBom(bom));
    }

    @Override
    public Flowable<List<Bom>> getBoms() {
        Flowable<List<Bom>> localBoms = getLocalBoms();
        Flowable<List<Bom>> remoteBoms = getAndSaveRemoteBoms();

        return Flowable.concat(localBoms, remoteBoms)
                .filter(boms -> !boms.isEmpty())
                .firstElement()
                .toFlowable();
    }

    private Flowable<List<Bom>> getLocalBoms() {
        return localDataSource.getBoms();
    }

    private Flowable<List<Bom>> getAndSaveRemoteBoms() {
        return remoteDataSource.getBoms()
                .flatMap(new Function<List<Bom>, Publisher<List<Bom>>>() {
                    @Override
                    public Publisher<List<Bom>> apply(List<Bom> boms) throws Exception {
                        return Flowable.fromIterable(boms)
                                .doOnNext(bom -> localDataSource.saveBom(bom))
                                .toList()
                                .toFlowable();
                    }
                });
    }

    @Override
    public Observable<Boolean> checkBomNameExist(String name) {
//        Observable<Boolean> localCheck = checkLocalBomNameExist(name);
//        Observable<Boolean> remoteCheck = checkRemoteBomNameExist(name);

//        return Observable.concat(localCheck, remoteCheck)
//                .filter(aBoolean -> aBoolean)
//                .firstElement()
//                .toObservable();
        return checkLocalBomNameExist(name);
    }

    private Observable<Boolean> checkLocalBomNameExist(String name) {
        return localDataSource.checkBomNameExist(name);
    }

    private Observable<Boolean> checkRemoteBomNameExist(String name) {
        return remoteDataSource.checkBomNameExist(name);
    }

    @Override
    public void saveBom(Bom bom) {
        localDataSource.saveBom(bom);
        remoteDataSource.saveBom(bom);
    }

    @Override
    public void deleteBom(@NonNull long bomId) {
        localDataSource.deleteBom(bomId);
        remoteDataSource.deleteBom(bomId);
    }

    @Override
    public void updateBom(Bom bom) {
        localDataSource.updateBom(bom);
        remoteDataSource.updateBom(bom);
    }
}
