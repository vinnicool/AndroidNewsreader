package com.newsreader.vincent.androidnewsreader;

import com.google.gson.annotations.SerializedName;

public class AuthTokenHttpResponse{
    @SerializedName("AuthToken")
    public String authToken;

    public  AuthTokenHttpResponse(){}

    public  AuthTokenHttpResponse(String authToken){
        this.authToken = authToken;
    }
}
