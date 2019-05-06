package com.lptv_12110101.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptv_12110101.Control;
import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;


public class Settings extends AppCompatActivity implements View.OnClickListener, Setting_Login_Interface {
    private Button myInfoLogin, myInfoLogout;
    private TextView my_id_login_ment, login_ment_01, login_ment_02, login_ment_03, sound_vibrate_popup, myInfoID, playerType;
    private Boolean isLoginView = true;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Application.actList.add(this);

        Control.singleInstance().setInterface(this);

        TextView myInfoTitle = (TextView) findViewById(R.id.myInfoTitle);
        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);
        RelativeLayout pushStyle_layout = (RelativeLayout) findViewById(R.id.pushStyle_layout);
        RelativeLayout receive_layout = (RelativeLayout) findViewById(R.id.receive_layout);
        RelativeLayout license_layout = (RelativeLayout) findViewById(R.id.license_layout);
        RelativeLayout versionInfo_layout = (RelativeLayout) findViewById(R.id.versionInfo_layout);
        RelativeLayout myInfo_layout = (RelativeLayout) findViewById(R.id.myInfo_layout);
        RelativeLayout playerSetting_layout = (RelativeLayout) findViewById(R.id.playerSetting_layout);

        TextView versionCode = (TextView) findViewById(R.id.versionCode);
        myInfoLogin = (Button) findViewById(R.id.myInfoLogin);
        myInfoLogout = (Button) findViewById(R.id.myInfoLogout);
        my_id_login_ment = (TextView) findViewById(R.id.my_id_login_ment);
        login_ment_01 = (TextView) findViewById(R.id.login_ment_01);
        login_ment_02 = (TextView) findViewById(R.id.login_ment_02);
        login_ment_03 = (TextView) findViewById(R.id.login_ment_03);
        sound_vibrate_popup = (TextView) findViewById(R.id.sound_vibrate_popup);
        myInfoID = (TextView) findViewById(R.id.myInfoID);

        playerType = (TextView) findViewById(R.id.playerType);

        myInfoTitle.setText("내 계정 & 정보");
        versionCode.setText(Application.application.getVersion());

        if (TextUtils.isEmpty(Control.singleInstance().getAppPre_Id(getApplicationContext()))) {
            myInfoLoginView();
        } else {
            myInfoLogoutView();
        }


        pushStyle_layout.setOnClickListener(this);
        setting_titlebar_close.setOnClickListener(this);
        receive_layout.setOnClickListener(this);
        versionInfo_layout.setOnClickListener(this);
        license_layout.setOnClickListener(this);
        myInfo_layout.setOnClickListener(this);
        playerSetting_layout.setOnClickListener(this);
        myInfoLogin.setOnClickListener(this);
        myInfoLogout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String pType = null;
        if (Application.application.getAppPreferencesBoolean("playerType")) {
            pType = "내장 플레이어";
        } else {
            pType = "외부 플레이어";
        }
        playerType.setText(pType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myInfoLogin:
                isLoginView = true;

                Intent myInfoLogin_intent = new Intent();
                setResult(2, myInfoLogin_intent);
                finish();
                break;

            case R.id.myInfoLogout:
                isLoginView = false;
                Intent myInfoLogout_intent = new Intent();
                setResult(1, myInfoLogout_intent);
                finish();
                break;

            case R.id.setting_titlebar_close:
                finish();
                break;

            case R.id.pushStyle_layout:
                settingAfterLogin(Setting_PushStyle.class);
                break;

            case R.id.receive_layout:
                settingAfterLogin(Setting_Receive.class);
                break;

            case R.id.versionInfo_layout:
                Control.singleInstance().intentToActivity(Settings.this, Setting_VersionInfo.class);
                break;

            case R.id.license_layout:
                Control.singleInstance().intentToActivity(Settings.this, Setting_OpenSource.class);
                break;

            case R.id.myInfo_layout:
                if (TextUtils.isEmpty(Control.singleInstance().getAppPre_Id(getApplicationContext()))) {
                    Intent settingAfterLogin_intent = new Intent();
                    setResult(2, settingAfterLogin_intent);
                } else {
                    Intent myInfo_layout_intent = new Intent();
                    setResult(3, myInfo_layout_intent);
                }
                finish();
                break;

            case R.id.playerSetting_layout:
                Control.singleInstance().intentToActivity(Settings.this, Setting_Player.class);
                break;
        }
    }


    public void settingAfterLogin(Class cls) {
        if (TextUtils.isEmpty(Control.singleInstance().getAppPre_Id(getApplicationContext()))) {
            Intent settingAfterLogin_intent = new Intent();
            setResult(2, settingAfterLogin_intent);
            finish();
        } else {
            Control.singleInstance().intentToActivity(Settings.this, cls);
        }
    }

    @Override
    public void receiver() {
        if (isLoginView) {
            myInfoLogoutView();
        } else {
            myInfoLoginView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return false;
    }

    public void myInfoLoginView() {
        myInfoLogin.setVisibility(View.VISIBLE);
        myInfoLogout.setVisibility(View.GONE);
        my_id_login_ment.setText("내 계정 로그인");
        login_ment_01.setVisibility(View.VISIBLE);
        login_ment_02.setVisibility(View.VISIBLE);
        login_ment_03.setVisibility(View.VISIBLE);
        sound_vibrate_popup.setVisibility(View.GONE);
        myInfoID.setVisibility(View.GONE);
    }

    public void myInfoLogoutView() {
        myInfoLogin.setVisibility(View.GONE);
        myInfoLogout.setVisibility(View.VISIBLE);
        my_id_login_ment.setText("내 계정 로그아웃");
        login_ment_01.setVisibility(View.GONE);
        login_ment_02.setVisibility(View.GONE);
        login_ment_03.setVisibility(View.GONE);
        sound_vibrate_popup.setVisibility(View.VISIBLE);
        myInfoID.setVisibility(View.VISIBLE);
        myInfoID.setText(Control.singleInstance().getAppPre_Id(getApplicationContext()));
    }
}