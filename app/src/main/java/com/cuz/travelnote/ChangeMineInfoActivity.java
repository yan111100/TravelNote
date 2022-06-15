package com.cuz.travelnote;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cuz.travelnote.utils.BaseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class ChangeMineInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout set_nickname;
    private RelativeLayout set_sex;
    private RelativeLayout set_signature;
    private TextView nicknameTextView;
    private TextView sexTextView;
    private TextView signTextView;
    private ImageView backImageView;
    private Button exitButton;
    private String nickname;
    private String sex;
    private String sign;
    private String editType;
    private String editValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mineinfo);
        nickname = getIntent().getExtras().getString("nickname");
        sex = getIntent().getExtras().getString("sex");
        sign = getIntent().getExtras().getString("sign");
        init();
    }

    public void init() {
        exitButton = findViewById(R.id.logoutButton);
        exitButton.setOnClickListener(this);
        backImageView = findViewById(R.id.change_info_back);
        backImageView.setOnClickListener(this);
        nicknameTextView = findViewById(R.id.nickname);
        sexTextView = findViewById(R.id.sex);
        signTextView = findViewById(R.id.signature);
        set_nickname = findViewById(R.id.set_nickname);
        set_sex = findViewById(R.id.set_sex);
        set_signature = findViewById(R.id.set_signature);
        set_nickname.setOnClickListener(this);
        set_sex.setOnClickListener(this);
        set_signature.setOnClickListener(this);

        nicknameTextView.setText(nickname);
        if (sex.equals("0")) {
            sexTextView.setText("保密");
        } else if (sex.equals("1")) {
            sexTextView.setText("男");
        } else if (sex.equals("2")) {
            sexTextView.setText("女");
        }
        signTextView.setText(sign);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.set_nickname:
                View editNickNameDialog = LayoutInflater.from(this).inflate(R.layout.dialog_editbox, null);
                final EditText nickNameEditText = (EditText) editNickNameDialog.findViewById(R.id.editText);
                nickNameEditText.setText(nickname);
                nickNameEditText.requestFocus();
                new AlertDialog.Builder(this)
                        .setTitle("编辑昵称")
                        .setView(editNickNameDialog)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editType = "nickname";
                                editValue = nickNameEditText.getText().toString();
                                sendData(editType, editValue);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.set_sex:
                final String[] sexList = new String[]{"保密", "男", "女"};
                int index = Integer.parseInt(sex);
                new AlertDialog.Builder(this)
                        .setTitle("修改性别")
                        .setSingleChoiceItems(sexList, index, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editType = "sex";
                                editValue = which + "";
                                sendData(editType, editValue);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.set_signature:
                View editSignDialog = LayoutInflater.from(this).inflate(R.layout.dialog_editbox, null);
                final EditText signEditText = editSignDialog.findViewById(R.id.editText);
                signEditText.setText(sign);
                signEditText.requestFocus();
                new AlertDialog.Builder(this)
                        .setTitle("编辑签名")
                        .setView(editSignDialog)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editType = "sign";
                                editValue = signEditText.getText().toString();
                                sendData(editType, editValue);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.change_info_back:
                finish();
                break;
            case R.id.logoutButton:
                exit();
            default:
                break;
        }
    }

    private void sendData(String type, String data) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add(type, data);

        BaseClient.post("user/update", formBodyBuilder.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 0) {
                            handler.sendEmptyMessage(0);
                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(1);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void exit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangeMineInfoActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(ChangeMineInfoActivity.this, "退出当前帐号成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(ChangeMineInfoActivity.this, "修改" + editType + "成功", Toast.LENGTH_SHORT).show();
                    if (editType.equals("sex")) {
                        sex = editValue;
                        if (sex.equals("0")) {
                            sexTextView.setText("保密");
                        } else if (sex.equals("1")) {
                            sexTextView.setText("男");
                        } else if (sex.equals("2")) {
                            sexTextView.setText("女");
                        }
                    } else if (editType.equals("nickname")) {
                        nickname = editValue;
                        nicknameTextView.setText(nickname);
                    } else if (editType.equals("sign")) {
                        sign = editValue;
                        signTextView.setText(sign);
                    }
                    break;
                case 1:
                    Toast.makeText(ChangeMineInfoActivity.this, "修改" + editType + "失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}

