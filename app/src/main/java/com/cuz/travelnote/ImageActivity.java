package com.cuz.travelnote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.viewpager.widget.ViewPager;

import com.cuz.travelnote.view.album.PhotoView;


import java.util.List;

public class ImageActivity extends Activity{
	private ViewPager mViewPager;
	private List<PhotoView> mImages;
	private PhotoView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		image = (PhotoView)findViewById(R.id.image);
		image.enable();
		String imagepath ="http://121.199.40.253:98/photo/" +getIntent().getExtras().getString("imagePath")+"/w/1000/h/2000/m/0";
//		String photopath = "";
//		String audiopath = "";
//		String videopath = "";
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imagepath, image);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
