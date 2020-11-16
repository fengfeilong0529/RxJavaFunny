package com.example.rxjavafunny;

import com.axaet.rxhttp.base.BaseResponse;
import com.example.rxjavafunny.bean.LoginBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface HttpService {

    @POST("/user/login")
    @FormUrlEncoded
    Observable<BaseResponse<LoginBean>> login(@Field("username") String username, @Field("password") String password);

    @GET("/user/logout/json")
    Observable<BaseResponse> logout();
}