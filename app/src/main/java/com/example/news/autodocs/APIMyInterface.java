package com.example.news.autodocs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIMyInterface {
    @FormUrlEncoded
    @POST("signin.php")
    Call<User> SignIn(@Field("email") String email, @Field("password") String password, @Field("type") String type);

    @FormUrlEncoded
    @POST("signup.php")
    Call<User> SignUpUser(@Field("name") String name,@Field("phone") String phone,@Field("address") String address,
                     @Field("billingAddress") String billingAddress,@Field("membership") String membership,
                     @Field("email") String email, @Field("password") String password);
    @FormUrlEncoded
    @POST("signup.php")
    Call<User> SignUpMechanic(@Field("name") String name,@Field("phone") String phone,
                          @Field("lat") String lat,@Field("lng") String lng,
                          @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("mechanics-nearby.php")
    Call<List<Mechanic>> MechanicsNearBy(@Field("lat") String lat, @Field("lng") String lng);
    @FormUrlEncoded
    @POST("request-nearest-mechanic.php")
    Call<UserWithRequest> FindNearestMechanic( @Field("lat") String lat, @Field("lng") String lng);

    @FormUrlEncoded
    @POST("request-a-mechanic.php")
    Call<Mechanic> RequestAMechanic(@Field("lat") String lat, @Field("lng") String lng,
                                    @Field("userId") String userId,
                                    @Field("mechanicId") String mechanicId,
                                    @Field("helpType") String helpType);
    @FormUrlEncoded
    @POST("share-mechanic-location.php")
    Call<UserWithRequest> shareMechanicLocation(@Field("lat") String lat, @Field("lng") String lng, @Field("requestId") String requestId);
    @FormUrlEncoded
    @POST("get-mechanic-location.php")
    Call<UserWithRequest> getMechanicLocation( @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("check-for-request.php")
    Call<UserWithRequest> CheckForRequest( @Field("mechanicId") String mechanicId);

    @FormUrlEncoded
    @POST("accept-request.php")
    Call<UserWithRequest> AcceptRequest( @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("reject-request.php")
    Call<UserWithRequest> RejectRequest( @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("testing-service.php")
    Call<String> testingPhp(@Field("test") String test);
//
    @FormUrlEncoded
    @POST("cancel-request.php")
    Call<UserWithRequest> cancelRequest(@Field("requestId")String requestId);

    @FormUrlEncoded
    @POST("finish-request.php")
    Call<UserWithRequest> finishRequest(@Field("requestId")String requestId);
}
