package com.newsreader.vincent.androidnewsreader;

import java.util.Dictionary;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServices {
    @POST("Users/login")
    Call<String> login(@Body User user);

    @POST("Users/register")
    Call<CustomHttpResponse> register(@Body User user);
}
