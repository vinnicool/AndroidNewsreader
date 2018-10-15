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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        String prefAuthToken = NewsReaderApplication.getPreferences().getString(NewsReaderApplication.authKey, null);
        if(NewsReaderApplication.authToken != null)
        {
            Intent intent = new Intent(this, NewsOverviewActivity.class);
            startActivity(intent);
        }
        else if(prefAuthToken != null)
        {
            NewsReaderApplication.authToken = prefAuthToken;
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
