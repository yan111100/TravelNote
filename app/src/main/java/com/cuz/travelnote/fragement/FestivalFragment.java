package com.cuz.travelnote.fragement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cuz.travelnote.TravelNoteDetailActivity;
import com.cuz.travelnote.R;
import com.cuz.travelnote.model.TravelNote;
import com.cuz.travelnote.model.User;
import com.cuz.travelnote.utils.ACache;
import com.cuz.travelnote.utils.BaseClient;
import com.cuz.travelnote.utils.PublicUtils;
import com.shizhefei.fragment.LazyFragment;

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
import uk.co.deanwild.flowtextview.FlowTextView;

public class FestivalFragment extends LazyFragment {

    private RecyclerView travelNoteListView;
    private List<TravelNote> travelNoteList = new ArrayList<TravelNote>();

    private ACache aCache;

    private SwipeRefreshLayout refreshLayout;

    private TravelNoteAdapter adapter;

    private boolean ISLIKED = false;
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean ISCOLLECTED = false;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_travelnote_list);

        travelNoteListView = (RecyclerView) findViewById(R.id.travelnote_list);
        travelNoteListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new TravelNoteAdapter(travelNoteList);
        travelNoteListView.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        //设置下拉时圆圈的颜色
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        //设置下拉刷新时的操作
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //具体操作
                getData();
            }
        });

        aCache = ACache.get(getActivity());
        getData();
        init();
    }

    class TravelNoteHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private FlowTextView introTextView;
        private TextView createTimeTextView;
        private TextView userNameTextView;
        private ImageView isCollectImageView;

        private TravelNote mTravelNote;

        public TravelNoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.travelnote_listview_item, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            introTextView = (FlowTextView) itemView.findViewById(R.id.introTextView);
            createTimeTextView = (TextView) itemView.findViewById(R.id.createTimeTextView);
            userNameTextView=(TextView)itemView.findViewById(R.id.log_user_name);
            isCollectImageView=(ImageView)itemView.findViewById(R.id.isCollectImageView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TravelNoteDetailActivity.class);
                    intent.putExtra("travelNote_id", mTravelNote.getId());
                    intent.putExtra("is_liked", ISLIKED);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
            });
            isCollectImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    formBodyBuilder.add("travelNoteId", String.valueOf(mTravelNote.getId()));
                    BaseClient.post("travelNote/collect/", formBodyBuilder.build(), new Callback(){
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            Log.e("error", e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ISCOLLECTED = !ISCOLLECTED;
                            String result = response.body().string();
                            Log.e("success", "success");
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    if(ISCOLLECTED == true){
                                        isCollectImageView.setImageResource(R.drawable.collecting_active);
                                    }else{
                                        isCollectImageView.setImageResource(R.drawable.collecting_normal);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    });
                }
            });
        }

        public void bind(TravelNote travelNote) {
            mTravelNote = travelNote;
            titleTextView.setText(travelNote.getTitle());
            introTextView.setTextSize(getActivity().getResources().getDimension(R.dimen.select_flow_textSize));
            introTextView.setColor(getActivity().getResources().getColor(R.color.text_gray));
            introTextView.setText(travelNote.getContent());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            String time = formatter.format(travelNote.getDate());
            createTimeTextView.setText(time);

            userNameTextView.setText(travelNote.getUser().getUsername() + "：");

        }
    }


    public class TravelNoteAdapter extends RecyclerView.Adapter<TravelNoteHolder> {
        public List<TravelNote> mTravelNotes;

        public TravelNoteAdapter(List<TravelNote> travelnotes) {
            mTravelNotes = travelnotes;
        }

        @Override
        public TravelNoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TravelNoteHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TravelNoteHolder holder, int position) {
            TravelNote travelNote = mTravelNotes.get(position);
            holder.bind(travelNote);
        }

        @Override
        public int getItemCount() {
            return mTravelNotes.size();
        }

        public void setTravelNote(List<TravelNote> travelNotes) {
            mTravelNotes = travelNotes;
        }
    }

    void getData() {
        BaseClient.get("travelNote/myCollectTravelNote/", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        Log.e("onSuccess", jsonObject.toString());
                        if (jsonObject.getInt("code") == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("value");

                            travelNoteList.clear(); //清空数据列表
                            addToList(jsonArray, travelNoteList);

                            handler.sendEmptyMessage(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);
            }
        });

    }

    void addToList(JSONArray jsonArray, List<TravelNote> travelNoteList) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                jsonObject = (JSONObject) jsonArray.get(i);
                TravelNote travelNote = new TravelNote();
                travelNote.setId(jsonObject.getLong("id"));
                travelNote.setTitle(jsonObject.getString("title"));
                travelNote.setDate(new Date(jsonObject.getLong("date")));
                travelNote.setContent(jsonObject.getString("content"));

                JSONObject userJSONObject = jsonObject.getJSONObject("user");
                User user = new User();
                user.setId(userJSONObject.getLong("id"));
                user.setUsername(userJSONObject.getString("username"));
                travelNote.setUser(user);

                travelNoteList.add(travelNote);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    refreshLayout.setRefreshing(false); //关闭刷新滚动条
                    break;
                case 2:
                    refreshLayout.setRefreshing(false); //关闭刷新滚动条
                    Toast.makeText(getActivity(), R.string.refresh_fail, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void init() {
        if (!PublicUtils.isNetworkAvailable(getActivity())) {
            JSONObject jsonObject = aCache.getAsJSONObject("TravelNote_list");
            if (jsonObject != null) {
                Log.e("in cache", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    addToList(jsonArray, travelNoteList);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            ISLIKED = TravelNoteDetailActivity.wasLikedShown(data);
        }
    }

}
