package com.jeremy.android.consumer.rxbus.event;

/**
 * Created by Jeremy on 2017/3/16.
 */

public class UpdateCardPoints {

    private float points;

    public UpdateCardPoints(float points) {
        this.points = points;
    }

    public float getPoints() {
        return points;
    }
}
