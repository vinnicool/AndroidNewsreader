package com.newsreader.vincent.androidnewsreader;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFeed
{
    @SerializedName("Results")
    public List<NewsItem> results;
    @SerializedName("NextId")
    public int nextId;

    public NewsFeed(){}

    public NewsFeed(List<NewsItem> results, int nextId){
        this.results = results;
        this.nextId = nextId;
    }

}
