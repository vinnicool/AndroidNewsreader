package com.newsreader.vincent.androidnewsreader;

public class CustomHttpResponse {
    public boolean Success;
    public String Message;

    public  CustomHttpResponse(){}

    public CustomHttpResponse(boolean sucess, String message)
    {
        this.Success = sucess;
        this.Message = message;
    }
}
