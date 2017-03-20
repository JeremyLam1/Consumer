package com.jeremy.android.consumer.setting.BomSetting;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.adapter.BomSettingsAdapter;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.view.DividerItemDecoration;
import com.jeremy.android.consumer.view.MoneyTextWatcher;
import com.jeremy.android.consumer.view.XEditText;
import com.jeremy.android.database.model.Bom;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BomSettingActivity extends BaseActivity implements BomSettingContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rcV_list)
    RecyclerView mRecyclerView;

    private AlertDialog dlgAdd;
    private AlertDialog dlgEdit;

    @Inject
    BomSettingsAdapter mAdapter;

    @Inject
    BomSettingPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bom_setting);
        ButterKnife.bind(this);

        DaggerBomSettingComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .bomSettingPresenterModule(new BomSettingPresenterModule(this))
                .build()
                .inject(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        mPresenter.subscribe();
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(0);
        dividerItemDecoration.setDividerColor(ContextCompat.getColor(BomSettingActivity.this, R.color.colorPrimary));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bom edtBom = mAdapter.getData().get(position);
                showEditBomDialog(edtBom, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.cb_select:
                        Bom bom = mAdapter.getData().get(position);
                        mPresenter.changeBomEnable(bom, position, ((CheckBox) view).isChecked());
                        break;
                    case R.id.img_delete:
                        Bom delBom = mAdapter.getData().get(position);
                        showDeleteBomDialog(delBom);
                        break;
                }
            }
        });
    }

    @OnClick(R.id.layout_add)
    public void onAddItemClick(View v) {
        showAddBomDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showBoms(List<Bom> boms) {
        mAdapter.setNewData(boms);
    }


    @Override
    public void showBomAdded(Bom bom) {
        mAdapter.getData().add(bom);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBomDeleted(Bom bom) {
        mAdapter.getData().remove(bom);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBomEdited(Bom bom, int position) {
        mAdapter.getData().set(position, bom);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBomEnableUpdated(Bom bom, int position) {
        mAdapter.getData().set(position, bom);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void hideAddDialog() {
        if (dlgAdd != null) {
            dlgAdd.dismiss();
        }
    }

    @Override
    public void hideEditDialog() {
        if (dlgEdit != null) {
            dlgEdit.dismiss();
        }
    }

    @Override
    public void setPresenter(BomSettingContract.Presenter presenter) {
        mPresenter = (BomSettingPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    private void showAddBomDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.add_bom));

        final View view = getLayoutInflater().inflate(R.layout.dialog_bom_add, null, false);
        final XEditText edtName = ButterKnife.findById(view, R.id.edt_bom_name);
        final XEditText edtPrice = ButterKnife.findById(view, R.id.edt_unit_price);
        final EditText edtMemo = ButterKnife.findById(view, R.id.edt_memo);
        final RadioGroup rgUnit = ButterKnife.findById(view, R.id.rg_unit);
        edtPrice.addTextChangedListener(new MoneyTextWatcher(edtPrice));

        alert.setView(view);
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        alert.setPositiveButton(getString(R.string.ok), null);

        dlgAdd = alert.create();
        dlgAdd.show();
        dlgAdd.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            RadioButton rdChecked = ButterKnife.findById(view, rgUnit.getCheckedRadioButtonId());
            String name = edtName.getText().toString();
            String price = edtPrice.getText().toString();
            String unit = rdChecked.getText().toString();
            String memo = edtMemo.getText().toString();
            mPresenter.addBom(name, price, unit, memo);
        });
    }

    private void showDeleteBomDialog(Bom delBom) {
        AlertDialog.Builder alert = new AlertDialog.Builder(BomSettingActivity.this);
        alert.setMessage(getString(R.string.sure_to_delete));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            mPresenter.deleteBom(delBom);
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void showEditBomDialog(Bom bom, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BomSettingActivity.this);
        builder.setTitle(getString(R.string.edit_bom));

        final View view = LayoutInflater.from(BomSettingActivity.this)
                .inflate(R.layout.dialog_bom_add, null, false);
        final XEditText edtName = ButterKnife.findById(view, R.id.edt_bom_name);
        final XEditText edtPrice = ButterKnife.findById(view, R.id.edt_unit_price);
        final EditText edtMemo = ButterKnife.findById(view, R.id.edt_memo);
        final RadioGroup rgUnit = ButterKnife.findById(view, R.id.rg_unit);
        edtPrice.addTextChangedListener(new MoneyTextWatcher(edtPrice));
        builder.setView(view);

        edtName.setText(bom.name);
        edtPrice.setText(String.valueOf(bom.price));
        edtMemo.setText(bom.memo);
        for (int i = 0; i < rgUnit.getChildCount(); i++) {
            RadioButton rbChecked = (RadioButton) rgUnit.getChildAt(i);
            if (rbChecked.getText().toString().equals(bom.unit)) {
                rbChecked.setChecked(true);
                break;
            }
        }

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
        });
        builder.setPositiveButton(getString(R.string.ok), null);
        dlgEdit = builder.create();
        dlgEdit.show();
        dlgEdit.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String name = edtName.getText().toString();
            String price = edtPrice.getText().toString();
            RadioButton rdChecked = ButterKnife.findById(view, rgUnit.getCheckedRadioButtonId());
            String unit = rdChecked.getText().toString();
            String memo = edtMemo.getText().toString();
            mPresenter.updateBom(bom, position, name, price, unit, memo);
        });
    }

}
