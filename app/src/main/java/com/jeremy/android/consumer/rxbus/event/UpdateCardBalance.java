package com.jeremy.android.consumer.rxbus.event;

/**
 * Created by Jeremy on 2017/3/16.
 */

public class UpdateCardBalance {

    private float balance;

    public UpdateCardBalance(float balance) {
        this.balance = balance;
    }

    public float getBalance() {
        return balance;
    }
}
