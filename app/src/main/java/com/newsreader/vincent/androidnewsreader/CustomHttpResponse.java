package com.newsreader.vincent.androidnewsreader;

public class CustomHttpResponse {
    public boolean Sucess;
    public String Message;

    public  CustomHttpResponse(){}

    public CustomHttpResponse(boolean sucess, String message)
    {
        this.Sucess = sucess;
        this.Message = message;
    }
}
