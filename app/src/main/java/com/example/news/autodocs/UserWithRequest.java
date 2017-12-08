package com.example.news.autodocs;

import com.google.gson.annotations.SerializedName;


public class UserWithRequest {
    @SerializedName("id")
    public String id;

    @SerializedName("userId")
    public String userId;

    @SerializedName("mechanicId")
    public String mechanicId;

    @SerializedName("userLat")
    public String userLat;

    @SerializedName("userLng")
    public String userLng;

    @SerializedName("mechanicLat")
    public String mechanicLat;

    @SerializedName("mechanicLng")
    public String mechanicLng;

    @SerializedName("status")
    public String status;

    @SerializedName("helpType")
    public String helpType;

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("billingAddress")
    public String billingAddress;

    @SerializedName("membership")
    public String membership;

    @SerializedName("response")
    public String response;

}
