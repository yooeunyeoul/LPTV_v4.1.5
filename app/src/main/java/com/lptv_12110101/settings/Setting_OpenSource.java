package com.lptv_12110101.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AppLog;

import java.io.InputStream;


public class Setting_OpenSource extends AppCompatActivity implements View.OnClickListener {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_opensource);
        Application.actList.add(this);

        TextView setting_titlebar_title = (TextView) findViewById(R.id.setting_titlebar_title);
        setting_titlebar_title.setText("Open Source License");
        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);
        setting_titlebar_close.setOnClickListener(this);


        //Volley
        try {
            InputStream is = getAssets().open("volley_license.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String volleryStr = new String(buffer);

            TextView volleyTxt = (TextView) findViewById(R.id.volleyLicenseTxt);
            volleyTxt.setText(volleryStr);
        } catch (Exception e) {
            AppLog.e("volley_license", e.toString());
            e.printStackTrace();
        }

        //Kakao
        try {
            InputStream is = getAssets().open("kakao_license.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String kakaoStr = new String(buffer);

            TextView kakaoTxt = (TextView) findViewById(R.id.kakaoLicenseTxt);
            kakaoTxt.setText(kakaoStr);
        } catch (Exception e) {
            AppLog.e("kakao_license", e.toString());
            e.printStackTrace();
        }

        //Kakao
        try {
            InputStream is = getAssets().open("facebook_license.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String kakaoStr = new String(buffer);

            TextView kakaoTxt = (TextView) findViewById(R.id.facebookLicenseTxt);
            kakaoTxt.setText(kakaoStr);
        } catch (Exception e) {
            AppLog.e("facebook_license", e.toString());
            e.printStackTrace();
        }

        //Kakao
        try {
            InputStream is = getAssets().open("naver_license.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String kakaoStr = new String(buffer);

            TextView kakaoTxt = (TextView) findViewById(R.id.naverLicenseTxt);
            kakaoTxt.setText(kakaoStr);
        } catch (Exception e) {
            AppLog.e("naver_license", e.toString());
            e.printStackTrace();
        }

        //ExoMedia
        try {
            InputStream is = getAssets().open("exomedia_license.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String kakaoStr = new String(buffer);

            TextView kakaoTxt = (TextView) findViewById(R.id.exomediaLicenseTxt);
            kakaoTxt.setText(kakaoStr);
        } catch (Exception e) {
            AppLog.e("exomedia_license", e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_titlebar_close:
                finish();
                break;
        }
    }
}
