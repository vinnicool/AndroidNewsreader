package com.newsreader.vincent.androidnewsreader;

import com.google.gson.annotations.SerializedName;

public class NewsItem
{
    @SerializedName("Id")
    public int id;
    @SerializedName("Feed")
    public int feed;
    @SerializedName("Title")
    public String title;
    @SerializedName("PublishDate")
    public String publishDate;
    @SerializedName("Summary")
    public String summary;
    @SerializedName("Image")
    public String image;
    @SerializedName("Url")
    public String url;
    @SerializedName("Categories")
    public NewsItemCategory[] categories;
    @SerializedName("IsLiked")
    public boolean isLiked;
}
