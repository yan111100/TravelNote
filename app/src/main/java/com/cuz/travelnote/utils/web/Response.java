package com.cuz.travelnote.utils.web;

import org.json.JSONObject;

/**
 * Created by Hawking on 2015/11/7.
 */
public abstract class Response{

    public abstract void onSuccess(int statusCode, JSONObject jsonObject);
    public void onSuccess(int statusCode, String responseBody){}

    public abstract void onFailure(int statusCode, JSONObject jsonObject, Throwable error);
    public void onFailure(int statusCode, String responseBody, Throwable error){}

    public void onFinally(){}
}
