package com.newsreader.vincent.androidnewsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewsOverviewActivity extends AppCompatActivity implements Callback<NewsFeed>
{
    private NewsOverviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_overview);
    }


    private void getNewsAsync()
    {
        Map<String,String> map = new HashMap<>();
        map.put("count", "2");
        MainActivity.service.getArticles(map).enqueue(this);
    }

    @Override
    public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response)
    {
        if(response.body() != null && response.body().results != null && response.body().results.size() > 0)
        {
            adapter = new NewsOverviewAdapter(response.body(), this);
        }
    }

    @Override
    public void onFailure(Call<NewsFeed> call, Throwable t)
    {
        Toast.makeText(this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
    }


    private class NewsOverviewAdapter extends android.support.v7.widget.RecyclerView.Adapter<NewsItemViewHolder>
    {
        private NewsFeed newsFeed;
        private NewsOverviewActivity activity;

        public NewsOverviewAdapter(NewsFeed newsFeed, NewsOverviewActivity activity)
        {
            this.newsFeed = newsFeed;
            this.activity = activity;
        }

        @NonNull
        @Override
        public NewsItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(activity).inflate(R.layout.activity_news_overview, viewGroup, false);
            return new NewsItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsItemViewHolder newsItemViewHolder, int i)
        {
            newsItemViewHolder.image = null;
            newsItemViewHolder.title = null;

            NewsItem item = newsFeed.results.get(i);

            //Load image

            newsItemViewHolder.title.setText(item.title);
        }

        @Override
        public int getItemCount()
        {
            return newsFeed.results.size();
        }
    }

    private class NewsItemViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public ImageView image;
        public TextView title;

        public NewsItemViewHolder(@NonNull View itemView)
        {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.news_image_thumbnail);
            title = (TextView) itemView.findViewById(R.id.news_title);
        }
    }
}
