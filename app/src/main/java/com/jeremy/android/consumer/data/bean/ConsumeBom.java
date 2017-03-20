package com.jeremy.android.consumer.data.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jeremy.android.database.model.Bom;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Jeremy on 2016/4/14 0014.
 */
public class ConsumeBom implements MultiItemEntity, Serializable {

    public static final int ADD = 1;
    public static final int ITEM = 2;
    private int itemType;

    private Long bomId;
    private String bomName;
    private float unitPrice;
    private int count;
    private String time;
    private float price;
    private String unit;
    private String memo;

    private ConsumeBom(int itemType) {
        setItemType(itemType);
    }

    private ConsumeBom(int itemType, Bom bom) {
        setItemType(itemType);
        setBomId(bom._id);
        setBomName(bom.name);
        setUnitPrice(bom.price);
        setUnit(bom.unit);
        setMemo(bom.memo);
        setPrice(Float.valueOf(String.format(Locale.getDefault(), "%.2f", bom.price)));
        setCount(1);
        setTime("1");
    }

    public static ConsumeBom newAddInstance() {
        return new ConsumeBom(ADD);
    }

    public static ConsumeBom newItemInstance(Bom bom) {
        return new ConsumeBom(ITEM, bom);
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Long getBomId() {
        return bomId;
    }

    public void setBomId(Long bomId) {
        this.bomId = bomId;
    }

    public String getBomName() {
        return bomName;
    }

    public void setBomName(String bomName) {
        this.bomName = bomName;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
