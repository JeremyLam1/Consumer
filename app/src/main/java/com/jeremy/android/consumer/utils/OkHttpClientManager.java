package com.jeremy.android.consumer.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/11 0011.
 */
public class OkHttpClientManager {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream; charset=utf-8");


    private static OkHttpClientManager mInstance;

    private OkHttpClient mOkHttpClient;
    private OkHttpClient mOkHttpsClient;

    private Handler mHandler;

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();

        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        mOkHttpsClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();

        mHandler = new Handler(Looper.getMainLooper());
    }

    private static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _get(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 异步的post JSON
     *
     * @param url
     * @param callback
     */
    private void _postAsynForJson(String url, String json, final ResultCallback callback) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 异步的post 文件
     *
     * @param url
     * @param callback
     */
    private void _postAsynForFile(String url, File file, final ResultCallback callback) {
        RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, file);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 异步的post(Https) 文件
     *
     * @param url
     * @param callback
     */
    private void _postAsynForFileByHttps(String url, File file, final ResultCallback callback) {

        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_STREAM, file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        
        deliveryHttpsResult(callback, request);
    }

    public static Response get(String url) throws IOException {
        return getInstance()._get(url);
    }

    public static void getAsyn(String url, ResultCallback callback) {
        getInstance()._getAsyn(url, callback);
    }

    public static void postAsynForJson(String url, String json, final ResultCallback callback) {
        getInstance()._postAsynForJson(url, json, callback);
    }

    public static void postAsynForFile(String url, File file, final ResultCallback callback) {
        getInstance()._postAsynForFile(url, file, callback);
    }

    public static void postAsynForFileByHttps(String url, File file, final ResultCallback callback) {
        getInstance()._postAsynForFileByHttps(url, file, callback);
    }


    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String string = response.body().string();
                    sendSuccessResultCallback(call, string, callback);
                } catch (IOException e) {
                    sendFailedStringCallback(call, e, callback);
                }
            }
        });
    }

    private void deliveryHttpsResult(final ResultCallback callback, Request request) {
        mOkHttpsClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.e("OK", "OK");
                }
                try {
                    String string = response.body().string();
                    sendSuccessResultCallback(call, string, callback);
                } catch (IOException e) {
                    sendFailedStringCallback(call, e, callback);
                }
            }
        });
    }

    private void sendFailedStringCallback(final Call call, final Exception e, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(call, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Call call, final Object object, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(call, object);
                }
            }
        });
    }

    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Call call, Exception e);

        public abstract void onResponse(Call call, T response);
    }


}
