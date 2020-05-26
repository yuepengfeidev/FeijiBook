package com.example.feijibook.http;

import android.util.ArrayMap;

import com.example.feijibook.entity.gson_bean.UserInfoBean;
import com.example.feijibook.http.upload_file.UpLoadFileType;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * HttpApi
 *
 * @author PengFei Yue
 * @date 2019/10/15
 * @description
 */
public interface HttpApi {
    /**
     * Login.
     */
    @FormUrlEncoded
    @POST("login")
    Observable<String> login(@Field("account") String account, @Field("password") String password);

    /**
     * Register account.
     */
    @FormUrlEncoded
    @POST("register")
    Observable<String> register(@Field("account") String account, @Field("password") String password);

    /**
     * Get this user's information.
     */
    @FormUrlEncoded
    @POST("getUserInfo")
    Observable<UserInfoBean> getUserInfo(@Field("account") String account);

    /**
     * Upload user's record with id.
     */
    @FormUrlEncoded
    @POST("addDetailRecord")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<String> addDetailRecord(@Field("id") String id, @Field("account") String account, @Field("year") String year,
                                       @Field("month") String month, @Field("day") String day,
                                       @Field("week") String week, @Field("woy") String woy,
                                       @Field("ioe") String ioe, @Field("icon_url") int icon_url,
                                       @Field("detail_type") String detail_type, @Field("money") String money,
                                       @Field("remark") String remark, @Field("record_order") int order);

    /**
     * Change user's record with resId.
     */
    @FormUrlEncoded
    @POST("changeDetailRecord")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<String> changeDetailRecord(@Field("resId") String resId, @Field("id") String id, @Field("account") String account,
                                          @Field("year") String year, @Field("month") String month, @Field("day") String day,
                                          @Field("week") String week, @Field("woy") String woy,
                                          @Field("ioe") String ioe, @Field("icon_url") int icon_url,
                                          @Field("detail_type") String detail_type, @Field("money") String money,
                                          @Field("remark") String remark, @Field("record_order") int order);

    /**
     * Upload user's setting of type.
     */
    @POST("upLoadTypeSetting")
    Observable<String> upLoadTypeSetting(@Body RequestBody body);

    /**
     * Delete user's record with id.
     */
    @FormUrlEncoded
    @POST("deleteDetailRecord")
    Observable<String> deleteDetailRecord(@Field("id") String id, @Field("account") String account);

    /**
     * Change User's NickName.
     */
    @FormUrlEncoded
    @POST("changeNickName")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<String> changeNickName(@Field("account") String account, @Field("nickname") String nickName);

    /**
     * Change this user's Sex.
     */
    @FormUrlEncoded
    @POST("changeSex")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<String> changeSex(@Field("account") String account, @Field("sex") String sex);

    /**
     * Change this user's password.
     */
    @FormUrlEncoded
    @POST("changePassword")
    Observable<String> changePassword(@Field("account") String account,
                                      @Field("resPw") String resPw, @Field("newPw") String newPw);

    /**
     * Upload this user's portrait.
     */
    @Multipart
    @POST("uploadPortrait")
    Observable<String> uploadPortrait(@Part MultipartBody.Part portrait);

    /**
     * Upload this user's video of records.
     */
    @UpLoadFileType
    @POST("uploadRecordVideos")
    Observable<String> uploadRecordVideos(@Query("account") String account, @Body ArrayMap<String, Object> videos);

    /**
     * Upload this user's photos of records.
     */
    @UpLoadFileType
    @POST("uploadRecordPhotos")
    Observable<String> uploadRecordPhotos(@Query("account") String account, @Body ArrayMap<String, Object> photos);

    /**
     * Download this user's portrait.
     *
     * @param portraitUrl The url of user's portrait in Web Server.
     */
    @Streaming
    @GET("downLoadPortrait")
    Observable<ResponseBody> downloadPortrait(@Query("url") String portraitUrl);

    /*下面是 获取城市接口 和 天气接口*/

    /**
     * 获取省
     *
     * @return 省
     */
    @GET("china")
    Observable<ResponseBody> getProvince();

    /**
     * 获取该省的市
     *
     * @param provinceId 省id
     * @return 市
     */
    @GET("china/{provinceId}")
    Observable<ResponseBody> getCity(@Path("provinceId") int provinceId);

    /**
     * 获取该省该市的区
     *
     * @param provinceId 省id
     * @param cityId     市id
     * @return 区
     */
    @GET("china/{provinceId}/{cityId}")
    Observable<ResponseBody> getCounty(@Path("provinceId") int provinceId, @Path("cityId") int cityId);

    /**
     * 获取Bing的每日图片
     *
     * @return 每日图片
     */
    @GET("bing_pic")
    Observable<ResponseBody> getBingPic();

    /**
     * 获取该城市的天气
     *
     * @param city 城市
     * @param key  聚合天气的主键
     * @return 天气接口
     */
    @GET("query")
    Observable<ResponseBody> getWeather(@Query("city") String city, @Query("key") String key);


}
