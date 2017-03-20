package com.jeremy.android.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.jeremy.android.database.AppDatabase;

/**
 * 消费项目
 * Created by Jeremy on 2016/4/13 0013.
 */
@Table(database = AppDatabase.class)
public class Bom extends BaseModel {
    //自增Id
    @PrimaryKey(autoincrement = true)
    public Long _id;
    //项目名称
    @Column
    public String name;
    //项目单价
    @Column
    public Float price;
    //项目单位
    @Column
    public String unit;
    //项目备注
    @Column
    public String memo;
    //项目是否可用 （0：不可用，1：可用）
    @Column
    public int selected;

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
