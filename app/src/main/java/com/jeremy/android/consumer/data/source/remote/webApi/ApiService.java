package com.jeremy.android.consumer.data.source.remote.webApi;

import com.jeremy.android.consumer.data.bean.FeedBack;
import com.jeremy.android.consumer.data.bean.VersionConfig;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Jeremy on 2017/2/28.
 */

public interface ApiService {

    /**
     * 问题反馈请求
     *
     * @param feedBack
     * @return
     */
    @Headers("Accept:application/json")
    @POST("/product_feedback.php")
    Observable<BaseResponse<String>> sendFeedBack(@Body FeedBack feedBack);


    /**
     * 上传数据库备份
     *
     * @return
     */
    @Multipart
    @POST("/upload_count.php")
    Observable<ResponseBody> uploadDbBackups(@Part("description") RequestBody description, @Part MultipartBody.Part file);


    /**
     * 下载数据库备份
     *
     * @return
     */
    @Streaming
    @GET("/.../db.zip")
    Observable<ResponseBody> downloadDbBackups();

    /**
     * 下载数据库备份
     *
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadDbBackups(@Url String fileUrl);


    /**
     * 版本更新信息请求
     *
     * @return
     */
    @GET("/update/update_consumer.json")
    Observable<VersionConfig> getVersionConfig();

}
