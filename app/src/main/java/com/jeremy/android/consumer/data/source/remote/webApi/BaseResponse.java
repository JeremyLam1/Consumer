package com.jeremy.android.consumer.data.source.remote.webApi;

/**
 * Created by Jeremy on 2017/2/28.
 */

public class BaseResponse<E> {

    private String code;

    private String message;

    private E data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
