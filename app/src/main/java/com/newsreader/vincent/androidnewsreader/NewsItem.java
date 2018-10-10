package com.newsreader.vincent.androidnewsreader;

import java.util.List;

public class NewsItem
{
    public int id;
    public int feed;
    public String title;
    public String publishDate;
    public String image;
    public String url;
    public NewsItemCategory[] categories;
    public boolean isLiked;
}
