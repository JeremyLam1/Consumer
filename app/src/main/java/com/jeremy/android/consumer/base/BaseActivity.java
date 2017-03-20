package com.jeremy.android.consumer.base;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.os.Bundle;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showMsg(String text) {
        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT).show();
    }

    protected void showMsg(int resId) {
        Snackbar.make(getWindow().getDecorView(), resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
