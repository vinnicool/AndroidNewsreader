package com.newsreader.vincent.androidnewsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static Retrofit retrofit;
    public static ApiServices service;
    public static String authToken;
    public static SharedPreferences preferences;
    public static final String usernameKey = "UsernameToken";
    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://inhollandbackend.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiServices.class);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("UsernameToken", null);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        if(authToken != null)
        {
            Intent intent = new Intent(this, NewsOverviewActivity.class);
            startActivity(intent);
        }
        else if(username != null)
        {
            Intent intent = new Intent(this, NewsOverviewActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


}
