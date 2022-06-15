package com.cuz.travelnote.utils.web;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class Request {
    public OkHttpClient client = new OkHttpClient();

    private static final String HOST = "http://api.reactorlive.com/";

    private static Context context;

    private boolean save = true;

    public Request(Context context){
        this.context = context;
    }

    public static String getHost(){
        return HOST;
    }

    public void get(final String url, final Response response, final boolean cache){
        final Storage s = new Storage(context);

        if( cache && !Helper.getInstance().getNetWorkStatus(context)){
            try {
                JSONObject object = s.getJson(url);

                if( object.has("code") ) {
                    response.onSuccess(200, object);
                    response.onFinally();

                    return;
                }

            }catch (JSONException e){

            }
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(getAbsoluteUrl(url))
                .get()
                .addHeader("token", "null")
                .addHeader("appid", "cb_7jcelkr9yr82b")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response resp) throws IOException {
                Log.e("success", "success");
                if (resp.isSuccessful()) {
                    try {
                        final String result = resp.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        response.onSuccess(resp.code(), jsonObject);
                        response.onFinally();
                        if (save)
                            s.setJson(url, jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        final String result = resp.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        response.onFailure(resp.code(), jsonObject, null);
                        response.onFinally();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getToken(){
        Storage s = new Storage(context);

        String token = s.get("token", "");
        if( token == null || token.length() < 1 ){
            String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            token = Helper.getInstance().md5(uuid);
        }

        return token;
    }

    public static String getAbsoluteUrl(String url) {
        return HOST + url;
    }

    public void setSave(boolean save){
        this.save = save;
    }
}
