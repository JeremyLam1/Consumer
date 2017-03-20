package com.jeremy.android.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.jeremy.android.database.AppDatabase;

import java.io.Serializable;

/**
 * 卡片
 * Created by Jeremy on 2016/3/28.
 */
@Table(database = AppDatabase.class)
public class Card extends BaseModel implements Serializable {

    //自增Id
    @PrimaryKey(autoincrement = true)
    public Long _id;
    //卡号
    @Column
    public String cardNo;
    //用户名
    @Column
    public String userName;
    //用户联系号码
    @Column
    public String userPhone;
    //用户地址
    @Column
    public String userAddr;
    //备注信息
    @Column
    public String memo;
    //创建时间
    @Column
    public long createDate;
    //卡过期日期
    @Column
    public long cardExpired;
    //卡当前余额
    @Column
    public float cardBalance;
    //用户总积分
    @Column
    public float userPoints;
    //删除标记
    @Column
    public int userDelete;

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
