package com.newsreader.vincent.androidnewsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsDetailsPage extends AppCompatActivity {

    public static final String imageViewName = "news_text_detail";
    private NewsItem newsItem;
    private NewsDetailPageViewHolder vh;
    private Menu menu;
    public static final String likedKey = "news_liked";
    public static final String articleIdKey = "news_liked_article";
    public static final int requestCode = 676;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String gson = intent.getStringExtra(NewsOverviewActivity.newsGsonKey);
        newsItem = new Gson().fromJson(gson, NewsItem.class);
        vh = new NewsDetailPageViewHolder(this);
        vh.imageView.setImageResource(R.mipmap.ic_launcher);
        vh.titleView.setText(newsItem.title);
        vh.textView.setText(newsItem.summary);
        LocalDateTime date = LocalDateTime.parse(newsItem.publishDate);
        String formattedDate = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear()
                                + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
        vh.publishDateView.setText(formattedDate);
        //vh.sourceView.setText(Html.fromHtml("&lt;a href=" + '"' + newsItem.url + '"' + "&gt;" + newsItem.url + "&lt; /a &gt", 0));
        vh.sourceView.setText(newsItem.url);
        Linkify.addLinks(vh.sourceView, Linkify.WEB_URLS);
        //vh.sourceView.setMovementMethod(LinkMovementMethod.getInstance());

        //NewsReaderApplication.getImageLoader().displayImage(newsItem.image, vh.imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_details_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.action_like);
        if(newsItem.isLiked)
        {
            item.setIcon(R.drawable.ic_action_unlike_reversed);
        }
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_like:
                likeNewsItem();
                return true;
            case R.id.action_share:
                shareArticle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareArticle()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(intent.EXTRA_SUBJECT, newsItem.title);
        intent.putExtra(intent.EXTRA_TEXT, newsItem.summary + "\n" + newsItem.url);
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void likeNewsItem()
    {
        if(NewsReaderApplication.authToken == null || NewsReaderApplication.authToken == "")
        {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }
        if(newsItem.isLiked)
        {
            NewsReaderApplication.getApiService().unLikeArticle(newsItem.id, NewsReaderApplication.authToken).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    //change to like
                    newsItem.isLiked = false;
                    MenuItem item = menu.findItem(R.id.action_like);
                    item.setIcon(R.drawable.ic_action_like_reversed);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(NewsDetailsPage.this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            NewsReaderApplication.getApiService().likeArticle(newsItem.id, NewsReaderApplication.authToken).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    newsItem.isLiked = true;
                    MenuItem item = menu.findItem(R.id.action_like);
                    item.setIcon(R.drawable.ic_action_unlike_reversed);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(NewsDetailsPage.this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(likedKey, newsItem.isLiked);
        returnIntent.putExtra(articleIdKey, newsItem.id);
        setResult(requestCode, returnIntent);

        finish();
        return true;
    }

    private class NewsDetailPageViewHolder{
        public TextView titleView;
        public ImageView imageView;
        public TextView textView;
        public TextView sourceView;
        public TextView publishDateView;

        public NewsDetailPageViewHolder(NewsDetailsPage activity)
        {
            titleView = findViewById(R.id.news_title_detail);
            imageView = findViewById(R.id.news_image_detail);
            textView = findViewById(R.id.news_text_detail);
            sourceView = findViewById(R.id.news_source_detail);
            publishDateView = findViewById(R.id.news_publisonhDate_detail);
        }
    }
}
