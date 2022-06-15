package com.cuz.travelnote.fragement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.cuz.travelnote.ChangeMineInfoActivity;
import com.cuz.travelnote.R;
import com.cuz.travelnote.popwindow.MineLoginPopWindow;
import com.cuz.travelnote.utils.BaseClient;
import com.cuz.travelnote.utils.ImageUtils;
import com.cuz.travelnote.view.CircleImageView;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MineFragment extends LazyFragment{
	private TextView title;
	private ImageView back_img;
	private ImageView edit_user_info_img;
	private CircleImageView avatarImageView;
	private TextView user_name;
	private TextView user_sign;

	private String tempPicPath = "";

	private String sex = "";
	private String nickname = "";
	private String sign = "";

	private boolean isLoad;
	private boolean isLogin;

	public static ViewPager viewPager;


	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_mine);

		init();
		initViewPager();
	}

	@Override
	public void onResumeLazy() {
		Log.e("MineFragement", "onResumeLazy()");
		if (isVisible()) {
			Log.e("MineFragement", "onResumeLazy()->getData()");
			isLoad = true;
			getData();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.e("MineFragement", "setUserVisibleHint()" + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {   //如果已经resumed就不执行，避免在onResumeLazy和setUserVisibleHint两个方法中重复请求服务端数据
			if (!isLoad) {
				Log.e("MineFragement", "setUserVisibleHint()->getData()");
				isLoad = true;
				getData();
			}
		} else {
			isLoad = false;
		}
	}

	public void init() {
		title = findViewById(R.id.title);
		title.setText("个人中心");
		title.setTextColor(getResources().getColor(R.color.mine_title_color));

		back_img = findViewById(R.id.back_img);
		back_img.setVisibility(View.INVISIBLE);

		edit_user_info_img = findViewById(R.id.edit_user_info_img);
		edit_user_info_img.setImageResource(R.drawable.toolbar_edit);
		edit_user_info_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLogin) {
					Intent intent2 = new Intent(getActivity(), ChangeMineInfoActivity.class);
					intent2.putExtra("sex", sex);
					intent2.putExtra("nickname", nickname);
					intent2.putExtra("sign", sign);
					getActivity().startActivity(intent2);
				} else {
					showLoginPopWindow();
				}
			}
		});

		avatarImageView = findViewById(R.id.user_photo_mine);
		avatarImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLogin) {
					changePhoto();
				} else {
					showLoginPopWindow();
				}
			}
		});

		user_name = findViewById(R.id.user_name);
		user_sign = findViewById(R.id.user_sign);
	}

	void getData() {
		BaseClient.get("user/member", new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				Log.e("error", e.getMessage());
				handler.sendEmptyMessage(1);
				isLogin = false;
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					Log.e("success", "success");
					try {
						final String result = response.body().string();
						JSONObject jsonObject = new JSONObject(result);
						Log.e("onSuccess", jsonObject.toString());

						if (jsonObject.getInt("code") == 0) {
							JSONObject value = jsonObject.getJSONObject("value");
							Message message = new Message();
							message.what = 0;
							message.obj = value;
							handler.sendMessage(message);
							isLogin = true;
						} else {
							handler.sendEmptyMessage(1);
							isLogin = false;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(1);
						isLogin = false;
					}
				} else {
					Log.e("failure", "failure");
					handler.sendEmptyMessage(1);
					isLogin = false;
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:  //登陆成功
					JSONObject value = (JSONObject) msg.obj;
					try {
						String url = value.getString("avatar");
						Glide.with(getActivity()).load(BaseClient.getAbsoluteUrl("pic/" + url)).into(avatarImageView);

						sex = value.getString("sex");
						nickname = value.getString("nickName");
						sign = value.getString("sign");
						try {
							sign = URLDecoder.decode(sign, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if (nickname != null && nickname.length() > 0 && !nickname.equals("null")) {
							user_name.setText(nickname);
						}
						if (sign != null && sign.length() > 0 && !sign.equals("null")) {
							user_sign.setText(sign);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 1:  //登陆失败
					showLoginPopWindow();
					break;
				case 3:  //修改头像成功
					Glide.with(getActivity()).load(tempPicPath).into(avatarImageView);
					break;
				default:
					break;
			}
		}
	};

	void showLoginPopWindow() {
		clearData();
		MineLoginPopWindow login = new MineLoginPopWindow(getActivity());
		login.showAtLocation(
				getActivity().findViewById(R.id.mine_fragment),
				Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}

	void clearData() {
		avatarImageView.setImageDrawable(null);
		user_name.setText("");
		user_sign.setText("");
	}

	void changePhoto() {
		final String[] options = new String[]{"拍照", "从相册中选取"};
		new AlertDialog.Builder(getActivity())
				.setTitle("设置头像")
				.setItems(options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e("选择了", options[which]);
						if (which == 0) {
							String directoryPath = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/";
							String fileName = System.currentTimeMillis() + ".jpg";// 照片命名
							tempPicPath = directoryPath + fileName;
							File photoFile = new File(directoryPath, fileName);

							Uri uri = FileProvider.getUriForFile(getActivity(),
									"com.cuz.travelnote.fileProvider", photoFile);//cn.edu.cuz.dialect.
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent, 0);
						} else if (which == 1) {
							Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, 1);
						}
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", null)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("requestCode", "" + requestCode);
		Log.e("resultCode", "" + resultCode);
		if (resultCode == Activity.RESULT_OK && (requestCode == 0 || requestCode == 1)) {
			if (requestCode == 1) { // 选择图片返回
				tempPicPath = ImageUtils.getImagePathFromUri(getActivity(), data.getData());
			}

			Bitmap bitmap = null;
			try {
				bitmap = ImageUtils.rotateImage(ImageUtils.revisionImageSize(tempPicPath), ImageUtils.readImageDegree(tempPicPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (bitmap == null) {
				return;
			}
			tempPicPath = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".jpg";
			Log.e("saved image path", tempPicPath);
			boolean result = ImageUtils.saveBitmap(tempPicPath, bitmap);

			if (result) {
				uploadImage(new File(tempPicPath));
			}
		}
	}

	private void uploadImage(File img) {
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("type", "pic")
				.addFormDataPart("file", img.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), img))
				.build();
		BaseClient.post("fileUpload", requestBody, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				Log.e("onFailure", e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.e("success", "success");
				if (response.isSuccessful()) {
					try {
						String result = response.body().string();
						JSONObject jsonObject = new JSONObject(result);
						String value = jsonObject.getString("value");
						uploadInfo(value);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void uploadInfo(String imagePath) {
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		formBodyBuilder.add("avatar", imagePath);

		BaseClient.post("user/update", formBodyBuilder.build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				Log.e("onFailure", e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) {
				handler.sendEmptyMessage(3);
			}
		});
	}

	public void initViewPager(){
		viewPager= (ViewPager) findViewById(R.id.fragment_mine_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_mine_indicator);
		indicator.setScrollBar(new ColorBar(getApplicationContext(), getResources().getColor(R.color.load_logo), 5));
		Resources res = getResources();  int selectColor = res.getColor(R.color.load_logo);
		int unSelectColor = res.getColor(R.color.tab_top_text_1);
		indicator.setOnTransitionListener(new OnTransitionTextListener()
				.setColor(selectColor, unSelectColor).setSize(17, 17));
		viewPager.setOffscreenPageLimit(4);
		IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		// 注意这里 的 FragmentManager 是 getChildFragmentManager(); 因为是在 Fragment 里面
		// 而在 activity 里面用 FragmentManager 是 getSupportFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
	}

	private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}
		@Override
		public int getCount() {
			return 3;
		}
		@Override
		public View getViewForTab(int position, View convertView,
								  ViewGroup container) {
			LayoutInflater inflate = LayoutInflater.from(getApplicationContext());
			switch (position) {
				case 0:
					if (convertView == null) {
						convertView = inflate.inflate(R.layout.tab_top, container, false);
					}
					TextView blog = (TextView) convertView;
					blog.setText("日志");
					break;
				case 1:
					if (convertView == null) {
						convertView = inflate.inflate(R.layout.tab_top, container, false);
					}
					TextView comments = (TextView) convertView;
					comments.setText("评论");
					break;
				case 2:
					if (convertView == null) {
						convertView = inflate.inflate(R.layout.tab_top, container, false);
					}
					TextView collections = (TextView) convertView;
					collections.setText("点赞");
					break;
				default:
					break;
			}
			return convertView;
		}
		@Override
		public Fragment getFragmentForPage(int position) {
			Fragment fragment = null;
			switch (position) {
				case 0:  NewNoteFragment newNoteFragment = new NewNoteFragment();
					fragment = newNoteFragment;
					break;
				case 1:
					CommentFragment commentsFragment = new CommentFragment();
					fragment = commentsFragment;
					break;
				case 2:
					LikedFragment likedragment = new LikedFragment();
					fragment = likedragment;
					break;
				default:
					break;
			}
			return fragment;
		}
	}
	public static void resetViewPagerHeight(int position) {
		if(viewPager == null) {
			return;
		}
		View child = viewPager.getChildAt(position);
		if (child != null) {
			child.measure(0, 0);
			int h = child.getMeasuredHeight();
			LinearLayout.LayoutParams params =
					(LinearLayout.LayoutParams) viewPager.getLayoutParams();
			params.height = h;
			viewPager.setLayoutParams(params);
		}
	}
}
