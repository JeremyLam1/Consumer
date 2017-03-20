package com.jeremy.android.consumer.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremry on 2015/8/12.
 */
public class JsonUtil {

    private final static String TAG = "JsonUtil";

    /**
     * 字符串转化为单个实体对象
     *
     * @param jsonString
     * @param clazz
     * @return
     * @throws JSONException
     */
    public static <T> T Json2Object(String jsonString, Class<T> clazz)
            throws JSONException {

        T t;

        try {
            Gson gson = new Gson();

            t = gson.fromJson(jsonString, clazz);

        } catch (Exception e) {
            Log.e(TAG, "json解析异常", e);
            throw new JSONException(e.getMessage());
        }
        return t;
    }

    /**
     * 字符串转为实体对象List
     *
     * @param jsonString
     * @param classOfT
     * @return
     */
    public static <T> ArrayList<T> Json2ObjectList(String jsonString,
                                                   Class<T> classOfT) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjs = new Gson().fromJson(jsonString, type);

        ArrayList<T> listOfT = new ArrayList<T>();
        for (JsonObject jsonObj : jsonObjs) {
            listOfT.add(new Gson().fromJson(jsonObj, classOfT));
        }
        return listOfT;
    }

    /**
     * 单个实体对象转为Json字符串
     *
     * @param t
     * @return
     */
    public static <T> String Object2Json(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }

    /**
     * 实体对象List转为Json字符串
     *
     * @param t_list
     * @return
     */
    public static <T> String ObjectList2Json(List<T> t_list) {
        Gson gson = new Gson();
        return gson.toJson(t_list);
    }

}
