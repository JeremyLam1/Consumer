package com.jeremy.android.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.jeremy.android.database.AppDatabase;

/**
 * 充值
 * Created by Jeremy on 2016/3/28.
 */
@Table(database = AppDatabase.class)
public class Recharge extends BaseModel {
    //自增Id
    @PrimaryKey(autoincrement = true)
    public Long _id;
    //卡号
    @Column
    public Long cardId;
    //备注信息
    @Column
    public String memo;
    //充值时间
    @Column
    public Long chargeTime;
    //充值金额
    @Column
    public float chargeMoney;

    public final static boolean CARD_VALID = true;
    public final static boolean CARD_INVALID = false;

    //判断该实例是否有效
    private boolean isValid = Card.CARD_VALID;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
