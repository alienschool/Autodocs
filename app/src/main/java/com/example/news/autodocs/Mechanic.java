package com.example.news.autodocs;

import com.google.gson.annotations.SerializedName;


public class Mechanic {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lng")
    public String lng;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("response")
    public String response;
}
