package com.jeremy.android.consumer.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.data.bean.VersionConfig;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.utils.NetworkUtils;
import com.jeremy.android.consumer.utils.RequestCode;
import com.jeremy.android.consumer.data.source.remote.webApi.ApiService;
import com.jeremy.android.database.model.Card;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/1/21.
 */

public class CardsPresenter implements CardsContract.Presenter {

    private final static int PAGE_SIZE = 11;

    private static int INVALID_CARD_ID = Integer.MAX_VALUE;

    private DataRepository dataRepository;

    private ApiService apiService;

    private CardsContract.View view;

    private long lastId = INVALID_CARD_ID;

    private CompositeDisposable compositeDisposable;

    private boolean isFirstLoading = true;

    @Inject
    CardsPresenter(DataRepository dataRepository, ApiService apiService, CardsContract.View view) {
        this.dataRepository = dataRepository;
        this.apiService = apiService;
        this.view = view;
        compositeDisposable = new CompositeDisposable();
        view.setPresenter(this);
    }

    @Override
    public void checkVersionUpdate() {
        apiService.getVersionConfig()
                .delay(3000, TimeUnit.MILLISECONDS)
                .filter(o -> NetworkUtils.isNetworkAvailable((Context) view))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<VersionConfig>() {
                    @Override
                    public void onNext(VersionConfig versionConfig) {
                        int currVersion = MyApplication.get().getCurrAppVersion();
                        if (currVersion != -1 && versionConfig.getVerCode() > currVersion) {
                            view.showHasNewVersion(versionConfig);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.CODE_ADD_CARD: {
                    CardItem cardItem = (CardItem) data.getSerializableExtra(BundleKeys.CARD_ITEM);
                    if (view.getCardItems().size() == 1) {
                        lastId = cardItem.getCardId();
                    }
                    view.getCardItems().add(1, cardItem);
                    view.showCardsUpdated();
                    view.showAddCardSuccessMsg();
                }
                break;
                case RequestCode.CODE_SETTING:
                    view.showSettingSavedMsg();
                    break;
                case RequestCode.CODE_ABOUT:
                    break;
                case RequestCode.CODE_CARD_DETAIL: {
                    CardItem updateItem = (CardItem) data.getSerializableExtra(BundleKeys.CARD_ITEM);
                    for (CardItem item : view.getCardItems()) {
                        if (item.getItemType() == CardItem.ITEM && item.getCardId().equals(updateItem.getCardId())) {
                            item.setUserName(updateItem.getUserName());
                            item.setUserPhone(updateItem.getUserPhone());
                            item.setCardNo(updateItem.getCardNo());
                            item.setCardExpired(updateItem.getCardExpired());
                            view.showCardsUpdated();
                            break;
                        }
                    }
                    view.showUpdateCardSuccessMsg();
                }
                break;
            }
        }
    }

    /**
     * 判断新数据是否为空
     */
    private boolean isNewCardsEmpty = false;

    @Override
    public void loadCards(boolean isRefresh, String search) {
        if (isRefresh) {
            lastId = INVALID_CARD_ID;
            dataRepository.refreshCards();
            if (isFirstLoading) {
                view.showRefreshLoadingIndicator();
                isFirstLoading = false;
            }
        }
        compositeDisposable.clear();
        Disposable disposable = dataRepository.getCards(lastId, search, PAGE_SIZE)
                .compose(view.getBindToLifecycle())
                .delay(3000, TimeUnit.MILLISECONDS)
                .doOnNext(cards -> {
                    if (!cards.isEmpty()) {
                        lastId = cards.get(cards.size() - 1)._id;
                    } else {
                        lastId = INVALID_CARD_ID;
                    }
                })
                .flatMap(new Function<List<Card>, Publisher<List<CardItem>>>() {
                    @Override
                    public Publisher<List<CardItem>> apply(List<Card> cards) throws Exception {
                        List<CardItem> cardItems = new ArrayList<>();
                        for (Card card : cards) {
                            cardItems.add(CardItem.newItemInstance(card));
                        }
                        return Flowable.just(cardItems);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cards -> {
                            isNewCardsEmpty = cards.isEmpty();
                            if (isNewCardsEmpty) {
                                if (isRefresh) {
                                    view.getCardItems().clear();
                                    view.getCardItems().add(0, CardItem.newAddInstance());
                                    view.showCardsUpdated();
                                    view.showNoDataMsg();
                                } else {
                                    view.showIsAllDataMsg();
                                }
                            } else {
                                if (isRefresh) {
                                    view.getCardItems().clear();
                                    view.getCardItems().add(0, CardItem.newAddInstance());
                                }
                                view.getCardItems().addAll(cards);
                                view.showCardsUpdated();
                            }
                        }
                        , throwable -> view.showLoadingCardsError(throwable.getMessage())
                        , () -> {
                            if (isRefresh) {
                                if (view.getCardItems().size() - 1 < PAGE_SIZE) {
                                    view.hideRefreshLoadingIndicator(true);
                                } else {
                                    view.hideRefreshLoadingIndicator(false);
                                }
                            } else {
                                view.hideMoreLoadingIndicator(isNewCardsEmpty);
                            }
                        });
        compositeDisposable.add(disposable);
    }

    @Override
    public void addNewCard() {
        view.showAddCard();
    }

    @Override
    public void openCardDetail(long cardId) {
        view.showCardDetail(cardId);
    }

    @Override
    public void deleteCard(int pos, long cardId) {
        view.showCardDeleted(pos);
        dataRepository.deleteCard(cardId);
    }

    @Override
    public void subscribe() {
        view.showRefreshToGetCards();
    }

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (view != null) {
            view = null;
        }
    }
}
