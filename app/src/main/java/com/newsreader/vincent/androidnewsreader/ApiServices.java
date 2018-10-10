package com.newsreader.vincent.androidnewsreader;

import java.util.Dictionary;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiServices {
    @POST("Users/login")
    Call<String> login(@Body User user);

    @POST("Users/register")
    Call<CustomHttpResponse> register(@Body User user);

    @GET("Articles")
    Call<NewsFeed> getArticles(@QueryMap Map<String,String> params);
}
