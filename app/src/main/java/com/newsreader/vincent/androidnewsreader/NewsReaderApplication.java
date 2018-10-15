package com.newsreader.vincent.androidnewsreader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsReaderApplication extends Application
{
    private static Context context;
    public static Retrofit retrofit;
    public static ApiServices service;
    public static String authToken;
    public static SharedPreferences preferences;
    public static final String authKey = "AuthToken";
    public static ImageLoader imageLoader;

    public static NewsReaderApplication application;

    public NewsReaderApplication(){
        application = this;
    }

    public static NewsReaderApplication getInstance()
    {
        return application;
    }

    public void onCreate()
    {
        super.onCreate();
        NewsReaderApplication.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return NewsReaderApplication.context;
    }

    public static Retrofit getRetrofit()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://inhollandbackend.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiServices getApiService()
    {
        if(service == null)
        {
            if(retrofit == null)
                getRetrofit();
            service = retrofit.create(ApiServices.class);
        }
        return service;
    }

    public static SharedPreferences getPreferences()
    {
        if(preferences == null)
        {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences;
    }

    public SharedPreferences getPreferences(Context context)
    {
        if(preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences;
    }

    public static ImageLoader getImageLoader()
    {
        if(imageLoader == null)
        {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        return imageLoader;
    }
}
