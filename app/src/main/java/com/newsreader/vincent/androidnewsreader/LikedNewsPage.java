package com.newsreader.vincent.androidnewsreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikedNewsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_news_page);
    }

//    private getLikedAsync()
//    {
//        NewsReaderApplication.getApiService().likedArticles(NewsReaderApplication.authToken).enqueue(new Callback<NewsFeed>() {
//            @Override
//            public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<NewsFeed> call, Throwable t) {
//
//            }
//        });
//    }
}
