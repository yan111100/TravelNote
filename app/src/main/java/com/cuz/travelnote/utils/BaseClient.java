package com.cuz.travelnote.utils;

import android.content.Context;
import android.preference.PreferenceManager;


import com.cuz.travelnote.MyApplication;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseClient {
    private static final String BASE_URL = "http://121.199.40.253:98/";

    private static OkHttpClient client = new OkHttpClient();

    static Context context = MyApplication.getInstance();

    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .get()
                .addHeader("authorization", PreferenceManager
                        .getDefaultSharedPreferences(context).getString("token", "null"))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void post(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .post(body)
                .addHeader("authorization", PreferenceManager
                        .getDefaultSharedPreferences(context).getString("token", "null"))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void put(String url, RequestBody body, Callback callback){
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .put(body)
                .addHeader("authorization", PreferenceManager
                        .getDefaultSharedPreferences(context).getString("token", "null"))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void delete(String url, RequestBody body, Callback callback){
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .delete(body)
                .addHeader("authorization", PreferenceManager
                        .getDefaultSharedPreferences(context).getString("token", "null"))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
