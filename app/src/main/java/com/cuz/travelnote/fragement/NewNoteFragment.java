package com.cuz.travelnote.fragement;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cuz.travelnote.ChoosePictureActivity;
import com.cuz.travelnote.ImageActivity;
import com.cuz.travelnote.R;
import com.cuz.travelnote.adapter.ImageAdapter;
import com.cuz.travelnote.model.NoteModel;
import com.cuz.travelnote.model.ImageModel;
import com.cuz.travelnote.utils.ACache;
import com.cuz.travelnote.utils.BaseClient;
import com.cuz.travelnote.utils.PublicUtils;
import com.cuz.travelnote.utils.TimeDiffer;
import com.cuz.travelnote.view.CircleImageView;
import com.cuz.travelnote.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shizhefei.fragment.LazyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class NewNoteFragment extends LazyFragment {

    private Button new_blog;
    private GridView bloglist;
    private List<NoteModel> blogListModels;
    private BlogListAdapter mBlogAdapter;
    private TextView blog_null;
    private ACache aCache;
    private int i = 0;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_note);
        init();
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        Log.e("onResume", "BlogFragment");
    }

    @Override
    protected void onFragmentStartLazy() {
        // TODO Auto-generated method stub
        super.onFragmentStartLazy();
        getData();
        MineFragment.resetViewPagerHeight(0);
        Log.e("onFragmentStartLazy","blog");
    }

    public void init() {
        blog_null = (TextView) findViewById(R.id.blog_null);
        blogListModels = new ArrayList<NoteModel>();
        bloglist = (NoScrollGridView) findViewById(R.id.fragment_blog_list);
        bloglist.setFocusable(false);
        bloglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
            }
        });

        new_blog = (Button) findViewById(R.id.new_blog);
        new_blog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), ChoosePictureActivity.class);
                intent.putExtra("publish_title", "我的日志");
                intent.putExtra("publish_sort", 0);
                startActivity(intent);
            }
        });
        aCache = ACache.get(getActivity());
        if (!PublicUtils.isNetworkAvailable(getActivity())) {
            if (aCache.getAsJSONObject("blog") != null) {
                JSONObject response = aCache.getAsJSONObject("blog");
                try {
                    JSONObject value = response.getJSONObject("value");
                    JSONArray content = value.getJSONArray("content");
                    for (int i = 0; i < content.length(); i++) {
                        JSONObject object = content.getJSONObject(i);
                        NoteModel bModel = new NoteModel();
                        bModel.setComment(object.getString("content"));
                        bModel.setCommentID(object.getString("id"));
                        List<ImageModel> comImages = new ArrayList<ImageModel>();
                        for (int j = 1; j < 7; j++) {
                            ImageModel coImage = new ImageModel();
                            if (object.getString("image" + j).length() > 0
                                    && !object.getString("image" + j).equals(
                                    "null")) {

                            }
                            comImages.add(coImage);
                        }
                        bModel.setUser_photo_imgpath(object.getString("member_avatar"));
                        bModel.setUser_name(object.getString("member_name"));
                        bModel.setCommentsImg(comImages);
                        bModel.setPublish_time(TimeDiffer.caculatedDate(object
                                .getLong("create_time")));
                        blogListModels.add(bModel);
                    }
                    if (blogListModels.size() > 0) {
                        bloglist.setVisibility(View.VISIBLE);
                        blog_null.setVisibility(View.GONE);
                        mBlogAdapter = new BlogListAdapter(getActivity(),
                                blogListModels);
                        bloglist.setAdapter(mBlogAdapter);
                        MineFragment.resetViewPagerHeight(0);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    bloglist.setVisibility(View.VISIBLE);
                    blog_null.setVisibility(View.GONE);
                    mBlogAdapter = new BlogListAdapter(getActivity(), blogListModels);
                    bloglist.setAdapter(mBlogAdapter);
                    MineFragment.resetViewPagerHeight(0);
                    break;
                case 1:
                    int position = msg.arg1;
                    blogListModels.remove(position);
                    if (blogListModels.size() == 0) {
                        bloglist.setVisibility(View.GONE);
                        blog_null.setVisibility(View.VISIBLE);
                    }
                    mBlogAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    MineFragment.resetViewPagerHeight(0);
                    break;
                case 2:
                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void getData() {
        BaseClient.get("travelNote/create", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.e("success", "success");
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        aCache.put("blog", jsonObject);
                        Log.e("blog", response.toString());

                        if (jsonObject.getInt("code") == 0) {
                            JSONObject value = jsonObject.getJSONObject("value");
                            JSONArray content = value.getJSONArray("content");
                            blogListModels.clear();
                            for (int i = 0; i < content.length(); i++) {
                                JSONObject object = content.getJSONObject(i);
                                NoteModel bModel = new NoteModel();
                                bModel.setComment(object.getString("content"));
                                bModel.setCommentID(object.getString("id"));
                                bModel.setUser_photo_imgpath(object.getString("member_avatar"));
                                bModel.setUser_name(object.getString("member_name"));
                                bModel.setPublish_time(TimeDiffer.caculatedDate(object
                                        .getLong("create_time")));
                                blogListModels.add(bModel);
                            }
                            if (blogListModels.size() > 0) {
                                handler.sendEmptyMessage(0);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public class BlogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public List<NoteModel> _listData;
        private Context mContext;

        public BlogListAdapter(Context context, List<NoteModel> list) {
            _listData = list;
            this.mContext = context;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return _listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return Long.valueOf(_listData.get(position).getCommentID());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_blog, null);
                holder.image = (NoScrollGridView) convertView.findViewById(R.id.image_grid_view);
                holder.comments_contents = (TextView) convertView.findViewById(R.id.comment_detail);
                holder.imge_container = (HorizontalScrollView) convertView.findViewById(R.id.image_container);
                holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
                holder.user_name = (TextView) convertView.findViewById(R.id.comment_user_name);
                holder.user_photo = (CircleImageView) convertView.findViewById(R.id.comments_user_photo);
                holder.praise_imge = (ImageView) convertView.findViewById(R.id.comments_praise);
                holder.deleteBlog = (LinearLayout) convertView.findViewById(R.id.delete_blog);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imge_container.setVisibility(View.VISIBLE);
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            // getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            int allWidth = (int) (110 * _listData.get(position)
                    .getCommentsImg().size() * density);
            int itemWidth = (int) (100 * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    allWidth, LinearLayout.LayoutParams.FILL_PARENT);
            holder.image.setLayoutParams(params);
            holder.image.setColumnWidth(itemWidth);
            holder.image.setHorizontalSpacing(10);
            holder.image.setStretchMode(GridView.NO_STRETCH);

            holder.image.setNumColumns(_listData.get(position).getCommentsImg().size());
            holder.image.setAdapter(new ImageAdapter(mContext, _listData.get(
                    position).getCommentsImg()));
            holder.image.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position1, long id) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    //intent.putExtra("image_path", _listData.get(position).getCommentsImg().get(position1).getImage_path());
                    mContext.startActivity(intent);
                }
            });
            holder.comments_contents.setText(_listData.get(position).getComment());

            holder.praise_imge.setImageResource(R.drawable.delete_blog);
            holder.deleteBlog.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.e("id", _listData.get(position).getCommentID());
                    deleteBlog(_listData.get(position).getCommentID(), position);
                }
            });
            holder.user_name.setText(_listData.get(position).getUser_name());
            ImageLoader.getInstance().displayImage(_listData.get(position).getUser_photo_imgpath(), holder.user_photo);
            holder.publish_time.setText(_listData.get(position).getPublish_time());
            return convertView;
        }

        public class ViewHolder {
            private CircleImageView user_photo;
            private TextView user_name;
            private TextView comments_contents;
            private TextView publish_time;
            private HorizontalScrollView imge_container;
            private ImageView praise_imge;
            private LinearLayout deleteBlog;
            private NoScrollGridView image;
        }

        private void deleteBlog(String id, final int position) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            formBodyBuilder.add("id", id);
            BaseClient.delete("travelNote/create", formBodyBuilder.build(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e("onFailure", e.getMessage());
                    handler.sendEmptyMessage(2);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("success", "success");
                    if (response.isSuccessful()) {
                        Message message = new Message();
                        message.what = 1;
                        message.arg1 = position;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                }
            });
        }
    }
}