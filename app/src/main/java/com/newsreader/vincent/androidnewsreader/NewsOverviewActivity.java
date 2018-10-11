package com.newsreader.vincent.androidnewsreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
    private NewsOverviewViewholder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_overview);
        vh = new NewsOverviewViewholder(this);
        getNewsAsync();
    }

    private void getNewsAsync()
    {
        Map<String,String> map = new HashMap<>();
        map.put("count", "10");
        MainActivity.service.getArticles(map).enqueue(this);
    }

    @Override
    public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response)
    {
        if(response.body() != null && response.body().results != null)
        {
            if(response.body().results.length > 0) {
                adapter = new NewsOverviewAdapter(response.body(), this);
                vh.newsItemsList.setAdapter(adapter);
                vh.newsItemsList.setLayoutManager(new LinearLayoutManager(this));
            }
            else
            {
                vh.newsItemsList.setVisibility(View.GONE);
                vh.resultsView.setVisibility(View.VISIBLE);
            }
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
            View v = LayoutInflater.from(activity).inflate(R.layout.newsoverviewlistitem, viewGroup, false);
            return new NewsItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final NewsItemViewHolder newsItemViewHolder, int i)
        {
            newsItemViewHolder.image.setImageBitmap(null);
            newsItemViewHolder.title.setText(null);

            NewsItem item = newsFeed.results[i];

            newsItemViewHolder.title.setText(item.title);
            MainActivity.imageLoader.displayImage(item.image, newsItemViewHolder.image);
        }

        @Override
        public int getItemCount()
        {
            return newsFeed.results.length;
        }
    }

    private class NewsOverviewViewholder
    {
        public RelativeLayout resultsView;
        public RecyclerView newsItemsList;
        private NewsOverviewActivity activity;

        public NewsOverviewViewholder(NewsOverviewActivity activity)
        {
            this.activity = activity;
            this.resultsView = findViewById(R.id.noResultsLayout);
            this.newsItemsList = findViewById(R.id.newsoverview_list);
        }
    }

    private class NewsItemViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public ImageView image;
        public TextView title;

        public NewsItemViewHolder(@NonNull View itemView)
        {
            super(itemView);

            image = itemView.findViewById(R.id.news_image_thumbnail);
            title = itemView.findViewById(R.id.news_title);
        }
    }
}
