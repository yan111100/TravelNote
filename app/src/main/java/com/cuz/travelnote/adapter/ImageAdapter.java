package com.cuz.travelnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cuz.travelnote.R;
import com.cuz.travelnote.model.ImageModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	public List<ImageModel> _listData = null;
	private Context mContext;
	public ImageAdapter(Context context, List<ImageModel> list){
		_listData = new ArrayList<ImageModel>();
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
		return _listData == null ? null : _listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return _listData == null ? 0 : position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_image, null);
			holder.album_img=(ImageView)convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage("http://121.199.40.253:98/photo/"+_listData.get(position)
				.getImagePath(), holder.album_img);
		return convertView;
	}
	public class ViewHolder{
		private ImageView album_img;
  	}
}
