package com.jeremy.android.consumer.base;


import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {


    public BaseFragment() {
        // Required empty public constructor
    }

    protected void showMsg(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
    }

    protected void showMsg(int resId) {
        Snackbar.make(getView(), resId, Snackbar.LENGTH_SHORT).show();
    }


}
