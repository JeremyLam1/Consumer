package com.jeremy.android.consumer.cards;

import com.jeremy.android.consumer.data.bean.CardItem;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jeremy on 2017/3/8.
 */
@Module
public class CardItemListModule {

    @Provides
    public List<CardItem> provideCardList() {
        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(0, CardItem.newAddInstance());
        return cardItems;
    }
}
