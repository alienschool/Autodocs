package com.example.news.autodocs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIMyInterface {
    @FormUrlEncoded
    @POST("signin.php")
    Call<User> Login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("signup.php")
    Call<User> Login(@Field("name") String name,@Field("phone") String phone,@Field("address") String address,
                     @Field("billingAddress") String billingAddress,@Field("membership") String membership,
                     @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("mechanics-nearby.php")
    Call<List<Mechanic>> MechanicsNearBy(@Field("lat") String lat, @Field("lng") String lng);

    @FormUrlEncoded
    @POST("request-a-mechanic.php")
    Call<List<Mechanic>> RequestAMechanic(@Field("lat") String lat, @Field("lng") String lng,@Field("userId") String userId, @Field("mechanicId") String mechanicId);

    @FormUrlEncoded
    @POST("testing-service.php")
    Call<String> testingPhp(@Field("test") String test);
}
