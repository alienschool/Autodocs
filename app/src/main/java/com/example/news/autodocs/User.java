package com.example.news.autodocs;

import com.google.gson.annotations.SerializedName;


public class User {
    @SerializedName("id")
    public String id;

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

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("response")
    public String response;
}
