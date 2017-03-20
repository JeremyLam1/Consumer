package com.jeremy.android.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.jeremy.android.database.AppDatabase;

/**
 * 消费
 * Created by Jeremy on 2016/3/28.
 */
@Table(database = AppDatabase.class)
public class Consumption extends BaseModel {
    //自增Id
    @PrimaryKey(autoincrement = true)
    public Long _id;
    //卡号
    @Column
    public Long cardId;
    //消费项目名称
    @Column
    public String bomName;
    //消费项目单价
    @Column
    public Float bomUnitPrice;
    //消费项目单位
    @Column
    public String unit;
    //消费时间
    @Column
    public Long payTime;
    //消费金额
    @Column
    public float payMoney;
    //消费次数
    @Column
    public int payCount;
    //消费用时
    @Column
    public float payTimeLength;
    //备注信息
    @Column
    public String memo;

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

