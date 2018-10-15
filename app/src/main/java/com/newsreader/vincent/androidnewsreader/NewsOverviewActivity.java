package com.newsreader.vincent.androidnewsreader;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
    private NewsOverviewViewHolder vh;
    public static final String newsGsonKey = "newsItem";
    public static final String newsImageGsonKey = "newsImage";
    public static final int resultCode = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_overview);
        vh = new NewsOverviewViewHolder(this);
        adapter = new NewsOverviewAdapter(null, this);
        vh.newsItemsList.setAdapter(adapter);
        vh.newsItemsList.setLayoutManager(new LinearLayoutManager(this));

        vh.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(adapter == null || adapter.isLoading) return;
                getNewsAsync(0);
            }
        });
        vh.newsItemsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(adapter != null || !adapter.isLoading && !recyclerView.canScrollVertically(1)){
                    getNewsAsync(adapter.getLastItemId());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_liked:
                likedArticles(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void likedArticles(MenuItem item)
    {
        item.setTitle(R.string.action_undo_filter);
        getLikedNewsAsync();
    }

    private void getLikedNewsAsync() {
        if(adapter != null) adapter.isLoading = true;
        NewsReaderApplication.getApiService().likedArticles(NewsReaderApplication.authToken).enqueue(new Callback<NewsFeed>() {
            @Override
            public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response) {
                if(response.body() != null && response.body().results != null)
                {
                    if(response.body().results.size() > 0)
                    {
                        adapter.setNewsFeed(response.body(), true);
                    }
                    else if(adapter.newsFeed != null && adapter.newsFeed.results.size() == 0)
                    {
                        vh.newsItemsList.setVisibility(View.GONE);
                        vh.resultsView.setVisibility(View.VISIBLE);
                    }
                }
                else if(adapter != null)
                    adapter.isLoading = false;
            }

            @Override
            public void onFailure(Call<NewsFeed> call, Throwable t) {
                adapter.isLoading = false;
                Toast.makeText(NewsOverviewActivity.this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout()
    {
        NewsReaderApplication.authToken = null;
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void getNewsAsync(int fromId)
    {
        if(adapter != null) adapter.isLoading = true;
        Map<String,String> map = new HashMap<>();
        map.put("count", "18");
        NewsReaderApplication.getApiService().getArticles(fromId != 0 ? Integer.toString(fromId) : "", map, NewsReaderApplication.authToken).enqueue(this);
    }

    @Override
    public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response)
    {
        if(response.body() != null && response.body().results != null)
        {
            if(response.body().results.size() > 0)
            {
                adapter.setNewsFeed(response.body(), false);
            }
            else if(adapter.newsFeed != null && adapter.newsFeed.results.size() == 0)
            {
                vh.newsItemsList.setVisibility(View.GONE);
                vh.resultsView.setVisibility(View.VISIBLE);
            }
        }
        else if(adapter != null)
            adapter.isLoading = false;
    }

    @Override
    public void onFailure(Call<NewsFeed> call, Throwable t)
    {
        adapter.isLoading = false;
        Toast.makeText(this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        switch (resultCode)
        {
            case (NewsDetailsPage.requestCode):
            {
                if (data.hasExtra(NewsDetailsPage.likedKey) && data.hasExtra(NewsDetailsPage.articleIdKey)) {
                    int id = data.getIntExtra(NewsDetailsPage.articleIdKey, 0);
                    boolean liked = data.getBooleanExtra(NewsDetailsPage.likedKey, false);
                    for (NewsItem item : adapter.newsFeed.results)
                    {
                        if (item.id == id)
                            item.isLiked = liked;
                    }
                }
                break;
            }
        }
    }

    private class NewsOverviewAdapter extends android.support.v7.widget.RecyclerView.Adapter<NewsItemViewHolder>
    {
        private NewsFeed newsFeed;
        private NewsOverviewActivity activity;
        protected boolean isLoading = false;

        public NewsOverviewAdapter(NewsFeed newsFeed, NewsOverviewActivity activity)
        {
            this.newsFeed = newsFeed;
            this.activity = activity;
            activity.getNewsAsync(0);
        }

        @NonNull
        @Override
        public NewsItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(activity).inflate(R.layout.newsoverviewlistitem, viewGroup, false);
            return new NewsItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final NewsItemViewHolder newsItemViewHolder, final int i)
        {
            newsItemViewHolder.image.setImageBitmap(null);
            newsItemViewHolder.title.setText(null);

            NewsItem item = newsFeed.results.get(i);

            newsItemViewHolder.title.setText(item.title);
            NewsReaderApplication.getImageLoader().displayImage(item.image, newsItemViewHolder.image);
            newsItemViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewsDetailPage(i, v);
                }
            });
        }

        private void openNewsDetailPage(int i, View view)
        {
//            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(NewsOverviewActivity.this,
//                    new Pair<>(view.findViewById(R.id.news_image_thumbnail), NewsDetailsPage.imageViewName));
            Intent intent = new Intent(NewsOverviewActivity.this, NewsDetailsPage.class);
            String gson = new Gson().toJson(newsFeed.results.get(i));
            intent.putExtra(newsGsonKey, gson);

            startActivityForResult(intent, resultCode);
        }

        @Override
        public int getItemCount()
        {
            return newsFeed == null ? 0 : newsFeed.results.size();
        }

        public int getLastItemId() { return  newsFeed.nextId; }

        public void Clear()
        {
            this.newsFeed.results.clear();
        }

        public void setNewsFeed(NewsFeed newsFeed, boolean clearCurrent)
        {
            if (this.newsFeed != null) {
                this.newsFeed.nextId = newsFeed.nextId;

                if(clearCurrent)
                    this.newsFeed.results.clear();
                this.newsFeed.results.addAll(newsFeed.results);
            }
            else
                this.newsFeed = newsFeed;

            notifyDataSetChanged();
            if (vh.swipeRefreshLayout.isRefreshing())
            {
                vh.swipeRefreshLayout.setRefreshing(false);
            }
            isLoading = false;

        }
    }

    private class NewsOverviewViewHolder
    {
        public RelativeLayout resultsView;
        public RecyclerView newsItemsList;
        public SwipeRefreshLayout swipeRefreshLayout;
        private NewsOverviewActivity activity;

        public NewsOverviewViewHolder(NewsOverviewActivity activity)
        {
            this.activity = activity;
            this.swipeRefreshLayout = findViewById(R.id.news_overview_swipeContainer);
            this.resultsView = findViewById(R.id.noResultsLayout);
            this.newsItemsList = findViewById(R.id.newsoverview_list);
        }
    }

    private class NewsItemViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
    {
        public RelativeLayout layout;
        public ImageView image;
        public TextView title;

        public NewsItemViewHolder(@NonNull View itemView)
        {
            super(itemView);

            layout = itemView.findViewById(R.id.news_view);
            image = itemView.findViewById(R.id.news_image_thumbnail);
            title = itemView.findViewById(R.id.news_title);
        }
    }
}
