package com.jeremy.android.consumer.setting.BomSetting;

import com.jeremy.android.consumer.BasePresenter;
import com.jeremy.android.consumer.BaseView;
import com.jeremy.android.database.model.Bom;

import java.util.List;

/**
 * Created by Jeremy on 2017/2/17.
 */

public class BomSettingContract {

    interface View extends BaseView<Presenter> {

        void showBoms(List<Bom> boms);

        void showBomAdded(Bom bom);

        void showBomDeleted(Bom bom);

        void showBomEdited(Bom bom, int position);

        void showBomEnableUpdated(Bom bom, int position);

        void showErrorMsg(String msg);

        void hideAddDialog();

        void hideEditDialog();
    }

    interface Presenter extends BasePresenter {

        void loadBoms();

        void addBom(String name, String price, String unit, String memo);

        void updateBom(Bom bom, int position, String nameNew, String priceNew, String unitNew, String memoNew);

        void deleteBom(Bom bom);

        void changeBomEnable(Bom bom, int position, boolean isEnable);
    }
}
