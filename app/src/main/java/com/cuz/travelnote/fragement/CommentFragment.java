package com.cuz.travelnote.fragement;

import android.os.Bundle;
import android.util.Log;

import com.cuz.travelnote.R;
import com.shizhefei.fragment.LazyFragment;

public class CommentFragment extends LazyFragment {

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_comments);
	}

	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();
		MineFragment.resetViewPagerHeight(1);
		Log.e("onFragmentStartLazy","comment");
	}
}
