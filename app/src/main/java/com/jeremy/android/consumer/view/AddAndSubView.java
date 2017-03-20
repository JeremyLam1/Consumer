package com.jeremy.android.consumer.view;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jeremy.android.consumer.R;

import butterknife.ButterKnife;

/**
 * Created by Jeremy on 2016/4/20 0020.
 */
public class AddAndSubView extends LinearLayout {

    private EditText edtCount;
    private Button btnAdd;
    private Button btnReduce;

    public AddAndSubView(Context context) {
        this(context, null);
    }

    public AddAndSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddAndSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_add_sub, this);
        edtCount = ButterKnife.findById(v, R.id.edt_count);
        btnAdd = ButterKnife.findById(v, R.id.btn_add);
        btnReduce = ButterKnife.findById(v, R.id.btn_sub);

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.valueOf(edtCount.getText().toString());
                if (count < 99999999) {
                    count++;
                    edtCount.setText(count + "");
                }
            }
        });

        btnReduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.valueOf(edtCount.getText().toString());
                if (count > 1) {
                    count--;
                    edtCount.setText(count + "");
                }
            }
        });
    }

    public void setEdtCount(int count) {
        edtCount.setText(count + "");
    }

    public void setEdtSelection(int i) {
        edtCount.setSelection(i);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        edtCount.addTextChangedListener(textWatcher);
    }
}
