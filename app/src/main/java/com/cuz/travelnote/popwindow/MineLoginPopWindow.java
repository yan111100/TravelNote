package com.cuz.travelnote.popwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.cuz.travelnote.LoginActivity;
import com.cuz.travelnote.R;

public class MineLoginPopWindow extends PopupWindow implements OnClickListener {
    private View mLoginView;
    private FrameLayout mFrameLayout;
    private Button login;
    private ImageView close;
    private Context mContext;

    public MineLoginPopWindow(Context context) {
        super(context);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoginView = inflater.inflate(R.layout.popwindow_login, null);
        login = (Button) mLoginView.findViewById(R.id.login);
        close = (ImageView) mLoginView.findViewById(R.id.close_wechat_login);
        login.setOnClickListener(this);
        close.setOnClickListener(this);
        mFrameLayout = (FrameLayout) mLoginView.findViewById(R.id.login_pop);
        this.setContentView(mLoginView);
        this.setWidth(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的宽
        this.setHeight(LayoutParams.MATCH_PARENT);// 设置SelectPicPopupWindow弹出窗体的高
        this.setFocusable(true);                   // 设置SelectPicPopupWindow弹出窗体可点击

        ColorDrawable dw = new ColorDrawable(0xcc000000);
        this.setBackgroundDrawable(dw);             // 设置SelectPicPopupWindow弹出窗体的背景

        mFrameLayout.setFocusable(true);
        mFrameLayout.setFocusableInTouchMode(true);
        mFrameLayout.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.close_wechat_login:
                dismiss();
                break;
            case R.id.login:
                dismiss();
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
        Log.e("popwindow", "dismiss");
    }

}
