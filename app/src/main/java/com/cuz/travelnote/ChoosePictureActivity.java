package com.cuz.travelnote;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cuz.travelnote.utils.BaseClient;
import com.cuz.travelnote.utils.StringUtils;
import com.cuz.travelnote.view.album.AlbumViewPager;
import com.cuz.travelnote.view.album.FilterImageView;
import com.cuz.travelnote.view.album.MatrixImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChoosePictureActivity extends AppCompatActivity implements
        OnClickListener, MatrixImageView.OnSingleTapListener {
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

    private TextView mBack;// ?????????
    private TextView mSend;// ??????
    private EditText mContent;// ?????????????????????
    private EditText mTitle;
    private InputMethodManager imm;// ???????????????
    private TextView textRemain;// ????????????
    private TextView picRemain;// ??????????????????
    private ImageView add;// ??????????????????
    private LinearLayout picContainer;// ????????????
    HorizontalScrollView scrollView;// ?????????????????????
    View editContainer;// ??????????????????
    View pagerContainer;// ??????????????????
    private ProgressBar uploadProgressBar;

    // ???????????????viewpager ????????????Actvity??? ????????????viewpager???????????????
    AlbumViewPager viewpager;// ????????????pager
    ImageView mBackView;// ??????/????????????
    TextView mCountView;// ??????????????????
    View mHeaderBar;// ???????????????
    ImageView delete;// ????????????
    private String art_id;
    int size;// ????????????
    int padding;// ????????????
    DisplayImageOptions options;
    private int comments_sort;

    private TextView title;
    private List<String> filePaths = new ArrayList<>();       // ??????????????????
    private List<String> networkPaths = new ArrayList<>();    // ??????????????????


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_dynamic);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // ??????ImageLoader??????
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.dangkr_no_picture_small)
                .showImageOnFail(R.drawable.dangkr_no_picture_small)
                .showImageOnLoading(R.drawable.dangkr_no_picture_small)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        initViews();
        initData();
    }

    /**
     * @Description??? ?????????Views
     */
    private void initViews() {
        // TODO Auto-generated method stub
        title = (TextView) findViewById(R.id.publish_title);
        title.setText(getIntent().getExtras().getString("publish_title"));
        comments_sort = getIntent().getExtras().getInt("publish_sort");
        mBack = (TextView) findViewById(R.id.post_back);
        mSend = (TextView) findViewById(R.id.post_send);
        uploadProgressBar = (ProgressBar) findViewById(R.id.upload_progress);
        mContent = (EditText) findViewById(R.id.post_content);
        mTitle = (EditText) findViewById(R.id.post_title);
        textRemain = (TextView) findViewById(R.id.post_text_remain);
        picRemain = (TextView) findViewById(R.id.post_pic_remain);
        add = (ImageView) findViewById(R.id.post_add_pic);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
        scrollView = (HorizontalScrollView)
                findViewById(R.id.post_scrollview);
        art_id = getIntent().getExtras().getString("art_id");
        viewpager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        delete = (ImageView) findViewById(R.id.header_bar_photo_delete);
        editContainer = findViewById(R.id.post_edit_container);
        pagerContainer = findViewById(R.id.pagerview);
        delete.setVisibility(View.VISIBLE);

        viewpager.setOnPageChangeListener(pageChangeListener);
        viewpager.setOnSingleTapListener(this);
        mBackView.setOnClickListener(this);
        mCountView.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mSend.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);

        mContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable content) {
                textRemain.setText(content.toString().length() + "/140");
            }
        });
    }

    private void initData() {
        size = (int) getResources().getDimension(R.dimen.size_80);
        padding = (int) getResources().getDimension(R.dimen.padding_10);
    }

    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() != View.VISIBLE) {
//			 showSaveDialog();
        } else {
            hideViewPager();
        }
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.post_back:
                finish();
                break;
            case R.id.header_bar_photo_back:
            case R.id.header_bar_photo_count:
                hideViewPager();
                break;
            case R.id.header_bar_photo_delete:
                final int index = viewpager.getCurrentItem();
                Builder builder = new Builder(
                        ChoosePictureActivity.this);
                builder.setMessage("?????????????????????????");
                builder.setTitle("??????");
                builder.setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                filePaths.remove(index);
                                if (filePaths.size() == 6) {
                                    add.setVisibility(View.GONE);
                                } else {
                                    add.setVisibility(View.VISIBLE);
                                }
                                if (filePaths.size() == 0) {
                                    hideViewPager();
                                }
                                picContainer.removeView(picContainer.getChildAt(index));

                                picRemain.setText(filePaths.size() + "/6");
                                mCountView.setText((viewpager.getCurrentItem() + 1)
                                        + "/" + filePaths.size());
                                viewpager.getAdapter().notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.post_send:
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String content = mContent.getText().toString();
                if (StringUtils.isEmpty(content) && filePaths.isEmpty()) {
                    Toast.makeText(this, "????????????????????????????????????????????????", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    // ??????????????????????????????????????????
                    view.setEnabled(false);
                    try {
                        if (filePaths.size() == 0) {
                            publish_blog();
                        } else {
                            uploadImg(new File(filePaths.get(0)), 0);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.post_add_pic:
                Matisse.from(this)
                        .choose(MimeType.ofImage(), false)
                        .countable(true)
                        .maxSelectable(6 - filePaths.size())
                        .thumbnailScale(0.8f)
                        .theme(R.style.Matisse_Zhihu)
                        .imageEngine(new GlideEngine())
                        .setOnSelectedListener(new OnSelectedListener() {
                            @Override
                            public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                                int length = pathList.size();
                                if (pathList.size() > 0 && filePaths.contains(pathList.get(length - 1))) {
                                    Log.e("selected", pathList.get(length - 1));
                                    pathList.remove(length - 1);
                                    uriList.remove(length - 1);
                                }
                            }
                        })
                        .showSingleMediaType(true)
                        .forResult(REQUEST_CODE_GETIMAGE_BYCROP);
                break;
            default:
                if (view instanceof FilterImageView) {
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        if (view == picContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
                break;
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (viewpager.getAdapter() != null) {
                String text = (position + 1) + "/"
                        + viewpager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
    };

    // ????????????pager
    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);
        viewpager.setAdapter(viewpager.new LocalViewPagerAdapter(filePaths));
        viewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + filePaths.size());
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1,
                (float) 0.9, 1, pagerContainer.getWidth() / 2,
                pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    // ??????????????????
    private void hideViewPager() {
        pagerContainer.setVisibility(View.GONE);
        editContainer.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1,
                (float) 0.9, pagerContainer.getWidth() / 2,
                pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GETIMAGE_BYCROP:
                if (resultCode == RESULT_OK) {
                    List<String> files = Matisse.obtainPathResult(data);
                    for (int i = 0; i < files.size(); i++) {
                        LayoutParams params = new LayoutParams(size, size);
                        params.rightMargin = padding;
                        FilterImageView imageView = new FilterImageView(this);

                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(
                                "file://" + new File(files.get(i)),
                                new ImageViewAware(imageView),
                                options, null, null);
                        imageView.setOnClickListener(this);

                        filePaths.add(files.get(i));
                        if (filePaths.size() == 6) {
                            add.setVisibility(View.GONE);
                        } else {
                            add.setVisibility(View.VISIBLE);
                        }
                        picContainer.addView(imageView, picContainer.getChildCount() - 1);
                        picRemain.setText(filePaths.size() + "/6");
                    }
                    // ????????????????????????
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Log.e("image files", " " + filePaths.size());
            if (msg.what + 1 < filePaths.size()) {
                try {
                    uploadImg(new File(filePaths.get(msg.what + 1)), (msg.what + 1));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if(msg.what + 1 == filePaths.size()){
                publish_blog();
            } else {
                Toast.makeText(ChoosePictureActivity.this, "????????????",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    public void publish_blog() {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("title", mContent.getText().toString().trim());
        formBodyBuilder.add("content", mTitle.getText().toString().trim());
        String imagePaths = "";
        for(String str : networkPaths){
            if(imagePaths.length()>0)
                imagePaths += ",";
            imagePaths+=str;
        }
        formBodyBuilder.add("images",imagePaths);

		BaseClient.post("travelnote/create", formBodyBuilder.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("publish blog failure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("success", "success");
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getString("code").equals("0")){
                            handler.sendEmptyMessage(filePaths.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
		});
    }

    public void uploadImg(final File img, final int i)
            throws FileNotFoundException {
        Log.e("start upload image", i + "");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", img.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), img))
                .build();
        BaseClient.post("fileUpload", requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("onFailure", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("success", "success");
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String value = jsonObject.getString("value");
                                networkPaths.add(value);
                                Log.e("image path", value);
                                Message message = new Message();
                                message.what = i;
                                handler.sendMessage(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }

}
