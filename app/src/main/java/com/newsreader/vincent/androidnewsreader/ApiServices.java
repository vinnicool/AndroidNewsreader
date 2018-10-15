package com.newsreader.vincent.androidnewsreader;

import java.util.Dictionary;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiServices {
    @POST("Users/login")
    Call<AuthTokenHttpResponse> login(@Body User user);

    @POST("Users/register")
    Call<CustomHttpResponse> register(@Body User user);

    @GET("Articles/{id}")
    Call<NewsFeed> getArticles(@Path("id") String fromId, @QueryMap Map<String,String> params, @Header("x-authtoken") String token);

    @PUT("Articles/{id}//like")
    Call<Void> likeArticle(@Path("id") int articleId, @Header("x-authtoken") String token);

    @DELETE("Articles/{id}//like")
    Call<Void> unLikeArticle(@Path("id") int articleId, @Header("x-authtoken") String token);

    @GET("Articles/liked")
    Call<NewsFeed> likedArticles(@Header("x-authtoken") String token);
}
