package com.jeremy.android.consumer.view;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Jeremy on 2016/4/13 0013.
 */
public class MoneyTextWatcher implements TextWatcher {

    private EditText editText;

    public MoneyTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        if (s.length() == 0) {
            editText.setText(0.0 + "");
            return;
        }
        if (s.toString().equals("0.0")) {
            editText.setSelection(s.length());
            return;
        }
        if (s.toString().length() == 4 && s.toString().startsWith("0.0")) {
            s.toString().replace("0.0", "");
            editText.setText(s.subSequence(3, 4));
            editText.setSelection(1);
            return;
        }
        if (s.toString().length() == 4 && s.toString().endsWith("0.0") && editText.getSelectionStart() == 1) {
            s.toString().replace("0.0", "");
            editText.setText(s.subSequence(0, 1));
            editText.setSelection(1);
            return;
        }
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 1) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 2);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }
        if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(1, s.toString().trim().length()));
                editText.setSelection(1);
                return;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}