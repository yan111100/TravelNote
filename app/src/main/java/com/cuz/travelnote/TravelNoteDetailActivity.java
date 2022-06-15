package com.cuz.travelnote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cuz.travelnote.adapter.ImageAdapter;
import com.cuz.travelnote.model.ImageModel;
import com.cuz.travelnote.model.TravelNote;
import com.cuz.travelnote.utils.ACache;
import com.cuz.travelnote.utils.BaseClient;
import com.cuz.travelnote.view.NoScrollGridView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class TravelNoteDetailActivity extends AppCompatActivity {
    private TextView brief_title;
    private TextView publish_time;
    private TextView collection_info;
    private WebView artical_detail;
    private ScrollView content;
    private ImageView back;
    private ImageView collect_img;
    private MediaStore.Audio audios;
    private MediaStore.Video videos;
    private RelativeLayout relativeLayout;
    private ImageView share_image;
    private RelativeLayout header;
    private TextView travelnote_title;
    private TextView travelnote_content;
    private Long id;
    private int width = 0;
    private ACache aCache;
    private ImageView praise;
    private boolean is_liked;
    private HorizontalScrollView imge_container;
    private NoScrollGridView image;

    //TravelNoteDetailActivity 获得父 Activity 传过来的文章编号 travelNote_id和点赞了的笔记id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelnote_detail);
        id = getIntent().getExtras().getLong("traveLnote_id");
        is_liked = getIntent().getExtras().getBoolean("is_liked");

        aCache = ACache.get(this);
        init();
        getData();
    }
    //保证无法连接网络时依然能从缓存中获取数据
    public void init() {
        share_image = (ImageView) findViewById(R.id.travelnote_share);
        travelnote_title=(TextView)findViewById(R.id.travelnote_title);
        relativeLayout = (RelativeLayout) findViewById(R.id.travelnote_detail_collect);
        content = (ScrollView) findViewById(R.id.travelnote_detail_content);
        back = (ImageView) findViewById(R.id.travelnote_detail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        width = windowManager.getDefaultDisplay().getWidth();
        brief_title = (TextView) findViewById(R.id.brief_title);
        travelnote_content= (TextView) findViewById(R.id.travelnote_content);
        publish_time = (TextView) findViewById(R.id.public_time);
        header=(RelativeLayout)findViewById(R.id.header);

        image = (NoScrollGridView)findViewById(R.id.image_grid_view);
        imge_container = (HorizontalScrollView)findViewById(R.id.image_container);
        praise = (ImageView)findViewById(R.id.travelnote_praise_label);//点赞
//判断是否点赞
        if(is_liked == false){
            praise.setImageResource(R.drawable.prise_detail);
        }else{
            praise.setImageResource(R.drawable.prise_active_detail);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    final JSONObject value = (JSONObject)msg.obj;
                    try {
                        brief_title.setText(value.getString("title"));
                        travelnote_title.setText(value.getString("title"));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                        publish_time.setText(formatter.format(new Date(value.getLong("date"))));
                        travelnote_content.setText(value.getString("content"));
                        imge_container.setVisibility(View.VISIBLE);

                        final TravelNote travelNote = new TravelNote();
                        travelNote.setId(value.getLong("id"));

                        final List<ImageModel> comImages = new ArrayList<ImageModel>();
                        JSONArray images = value.getJSONArray("images");
                        for (int j = 0; j < images.length(); j++) {
                            //ImageModel coImage = new ImageModel();
                            JSONObject jsonObject = images.getJSONObject(j);
                            ImageModel coImage = new ImageModel();
                            coImage.setImagePath(jsonObject.getString("imagePath"));
                            comImages.add(coImage);
                        }
                        travelNote.setImages(comImages);

                        DisplayMetrics dm = TravelNoteDetailActivity.this.getResources().getDisplayMetrics();
                        float density = dm.density;
                        int allWidth = (int) (110 * travelNote.getImages().size() * density);
                        int itemWidth = (int) (100 * density);
                        LinearLayout.LayoutParams params = new LinearLayout
                                .LayoutParams(allWidth, LinearLayout.LayoutParams.FILL_PARENT);
                        image.setLayoutParams(params);
                        image.setColumnWidth(itemWidth);
                        image.setHorizontalSpacing(10);
                        image.setStretchMode(GridView.NO_STRETCH);
                        image.setNumColumns(travelNote.getImages().size());
                        image.setAdapter(new ImageAdapter(TravelNoteDetailActivity.this, travelNote.getImages()));
                        praise.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                                formBodyBuilder.add("travelNoteId", getIntent()
                                        .getExtras().getLong("travelNote_id") + "");
                                BaseClient.post("travelNote/favourite/", formBodyBuilder.build(), new okhttp3.Callback(){
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        Log.e("error", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        is_liked = !is_liked;
                                        setLikedResult(is_liked);
                                        getData();
                                        String result = response.body().string();
                                        Log.e("success", "success");
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(result);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                });
                            }
                        });

                        if(is_liked == false){
                            praise.setImageResource(R.drawable.prise_detail);
                        }else{
                            praise.setImageResource(R.drawable.prise_active_detail);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //根据id查询某一篇美食日志
    public void getData(){
        BaseClient.get("travelNote/findOne?id=" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("success", "success");
                if (response.isSuccessful()) {
                    try {
                        final String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        aCache.put("TravelNoteDetail" + id, jsonObject);
                        Log.e("onSuccess", jsonObject.toString());
                        if (jsonObject.getInt("code") == 0) {
                            JSONObject value = jsonObject.getJSONObject("value");
                            Message message = new Message();
                            message.what = 0;
                            message.obj = value;
                            handler.sendMessage(message);
                        }
                        if(is_liked == false){
                            praise.setImageResource(R.drawable.prise_detail);
                        }else{                           praise.setImageResource(R.drawable.prise_active_detail);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setLikedResult(boolean isLiked){
        Intent data = new Intent();
        data.putExtra("is_liked", isLiked);
        setResult(RESULT_OK, data);
    }
    public static boolean wasLikedShown(Intent result){
        return result.getBooleanExtra("is_liked", false);
    }
}