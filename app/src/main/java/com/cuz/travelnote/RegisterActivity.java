package com.cuz.travelnote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cuz.travelnote.utils.BaseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {

	private EditText usernameTx;
	private EditText passwordTx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("注册");
		setContentView(R.layout.activity_register);

        findViewById(R.id.change_info_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

		usernameTx = findViewById(R.id.username_tx);
		passwordTx = findViewById(R.id.password_tx);

        Button registerButton = findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});

        TextView loginTextView = findViewById(R.id.login_tx);
        loginTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
	}

	public void register() {
		Log.e("register", "register");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("username", usernameTx.getText().toString());
        formBodyBuilder.add("password", passwordTx.getText().toString());
		BaseClient.post("user/register", formBodyBuilder.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                handler.sendEmptyMessage(2);
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
                            login();
                        } else {
                            String messageInfo = jsonObject.getString("message");
                            Message message = new Message();
                            message.what = 1;
                            message.obj = messageInfo;
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("register", "failure");
                        handler.sendEmptyMessage(2);
                    }
                } else {
                    Log.e("register", "failure");
                    handler.sendEmptyMessage(2);
                }
            }
		});
	}

    public void login() {
        Log.e("login", "login");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("username", usernameTx.getText().toString());
        formBodyBuilder.add("password", passwordTx.getText().toString());
        BaseClient.post("user/login", formBodyBuilder.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                handler.sendEmptyMessage(5);
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
                            String token = value.getString("token");
                            Message message = new Message();
                            message.what = 3;
                            message.obj = token;
                            handler.sendMessage(message);
                        } else {
                            String messageInfo = jsonObject.getString("message");
                            Message message = new Message();
                            message.what = 4;
                            message.obj = messageInfo;
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("login", "failure");
                        handler.sendEmptyMessage(5);
                    }
                } else {
                    Log.e("login", "failure");
                    handler.sendEmptyMessage(5);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:               //注册失败，账号已存在
                    String messageInfo = (String) msg.obj;
                    Toast.makeText(RegisterActivity.this, messageInfo, Toast.LENGTH_SHORT).show();
                    break;
                case 2:              //注册失败，链接服务器失败
                    Toast.makeText(RegisterActivity.this, R.string.warning_register_failed, Toast.LENGTH_SHORT).show();
                    break;
                case 3:             //登陆成功
                    String token = (String) msg.obj;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.commit();
                    Toast.makeText(RegisterActivity.this, R.string.warning_register_success, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 4:                //登陆失败，账号或密码错误
                    String messageInfo1 = (String) msg.obj;
                    Toast.makeText(RegisterActivity.this, messageInfo1, Toast.LENGTH_SHORT).show();
                    break;
                case 5:               //登陆失败，链接服务器失败
                    Toast.makeText(RegisterActivity.this, R.string.warning_register_failed, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
}
