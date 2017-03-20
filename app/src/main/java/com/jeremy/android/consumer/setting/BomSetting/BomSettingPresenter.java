package com.jeremy.android.consumer.setting.BomSetting;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.source.DataRepository;
import com.jeremy.android.consumer.utils.DBHelper;
import com.jeremy.android.database.model.Bom;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jeremy on 2017/2/17.
 */

public class BomSettingPresenter implements BomSettingContract.Presenter {

    private DataRepository dataRepository;

    private BomSettingContract.View view;

    private CompositeDisposable compositeDisposable;

    private String[] errorMsg = new String[1];

    @Inject
    BomSettingPresenter(DataRepository dataRepository, BomSettingContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadBoms() {
        compositeDisposable.clear();
        Disposable disposable = dataRepository.getBoms()
                .compose(view.getBindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(boms -> {
                            if (!boms.isEmpty()) {
                                view.showBoms(boms);
                            }
                        }
                        , throwable -> view.showErrorMsg(throwable.getMessage())
                        , () -> {

                        });
        compositeDisposable.add(disposable);
    }

    @Override
    public void addBom(String name, String price, String unit, String memo) {

        Context ctx = (Context) view;
        if (TextUtils.isEmpty(name)) {
            errorMsg[0] = ctx.getString(R.string.bom_name) + ctx.getString(R.string.field_no_be_null);
            view.showErrorMsg(errorMsg[0]);
            return;
        }

        float fPrice = Float.valueOf(price);
        if (fPrice == 0f) {
            errorMsg[0] = ctx.getString(R.string.unit_price) + ctx.getString(R.string.no_be_zero);
            view.showErrorMsg(errorMsg[0]);
            return;
        }

        dataRepository.checkBomNameExist(name)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isExist -> {
                    if (isExist) {
                        errorMsg[0] = ctx.getString(R.string.bom_name) + ctx.getString(R.string.is_exist);
                        view.showErrorMsg(errorMsg[0]);
                    } else {
                        Bom bom = new Bom();
                        bom.name = name;
                        bom.price = fPrice;
                        bom.unit = unit;
                        bom.memo = memo;
                        bom.selected = 1;

                        dataRepository.saveBom(bom);
                        view.showBomAdded(bom);
                        view.hideAddDialog();
                    }
                });
    }

    @Override
    public void updateBom(Bom bom, int position, String nameNew, String priceNew, String unitNew, String memoNew) {

        Context ctx = (Context) view;
        if (TextUtils.isEmpty(nameNew)) {
            errorMsg[0] = ctx.getString(R.string.bom_name) + ctx.getString(R.string.field_no_be_null);
            view.showErrorMsg(errorMsg[0]);
            return;
        }

        float fPrice = Float.valueOf(priceNew);
        if (fPrice == 0f) {
            errorMsg[0] = ctx.getString(R.string.unit_price) + ctx.getString(R.string.no_be_zero);
            view.showErrorMsg(errorMsg[0]);
            return;
        }

        if (!nameNew.equals(bom.name) && DBHelper.isBomNameExist(nameNew)) {
            Toast.makeText(ctx, ctx.getString(R.string.bom_name) + ctx.getString(R.string.is_exist), Toast.LENGTH_SHORT).show();
            return;
        }

        if (nameNew.equals(bom.name)) {
            bom.name = nameNew;
            bom.price = fPrice;
            bom.unit = unitNew;
            bom.memo = memoNew;

            dataRepository.updateBom(bom);
            view.showBomEdited(bom, position);
            view.hideEditDialog();
            return;
        }

        dataRepository.checkBomNameExist(nameNew)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isExist -> {
                    if (isExist) {
                        errorMsg[0] = ctx.getString(R.string.bom_name) + ctx.getString(R.string.is_exist);
                        view.showErrorMsg(errorMsg[0]);
                    } else {
                        bom.name = nameNew;
                        bom.price = fPrice;
                        bom.unit = unitNew;
                        bom.memo = memoNew;

                        dataRepository.updateBom(bom);
                        view.showBomEdited(bom, position);
                        view.hideEditDialog();
                    }
                });
    }

    @Override
    public void deleteBom(Bom bom) {
        dataRepository.deleteBom(bom._id);
        view.showBomDeleted(bom);
    }

    @Override
    public void changeBomEnable(Bom bom, int position, boolean isEnable) {
        if (isEnable) {
            bom.selected = 0;
        } else {
            bom.selected = 1;
        }
        dataRepository.updateBom(bom);
        view.showBomEnableUpdated(bom, position);
    }

    @Override
    public void subscribe() {
        loadBoms();
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
