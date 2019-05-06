package com.lptv_12110101.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lptv_12110101.Control;
import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;

import java.util.HashMap;
import java.util.Map;


public class Setting_VersionInfo extends AppCompatActivity implements View.OnClickListener {
    private Button recentVersion_Text, update_please;
    private String newVerStr, nowVerStr;
    private Float nowVerf, newVerf;
    private TextView recentVersion;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_versioninfo);
        Application.actList.add(this);

        nowVerStr = Application.application.getVersion();
        getNewVersion(nowVerStr);

        TextView setting_titlebar_title = (TextView) findViewById(R.id.setting_titlebar_title);
        setting_titlebar_title.setText("버전 정보");
        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);
        TextView nowVersion = (TextView) findViewById(R.id.nowVersion);
        recentVersion = (TextView) findViewById(R.id.recentVersion);
        recentVersion_Text = (Button) findViewById(R.id.recentVersion_Text);
        update_please = (Button) findViewById(R.id.update_please);

        nowVersion.setText("현재버전 V" + nowVerStr);

        setting_titlebar_close.setOnClickListener(this);
        update_please.setOnClickListener(this);
    }

    private void getNewVersion(final String ver) {
        String url = Application.application.getAppDomain() + "/Inform/VerCheck.aspx";

        StringRequest client = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                newVerStr = s;

                if (newVerStr.length() == 4) {
                    String newVerStr1 = newVerStr.substring(0, newVerStr.length() - 1);
                    String newVerStr2 = newVerStr.substring(newVerStr.length() - 1, newVerStr.length());
                    newVerStr = newVerStr1 + "." + newVerStr2;
                } else if (newVerStr.length() == 1) {
                    newVerStr = newVerStr + ".0";
                }
                recentVersion.setText("최신버전 V" + newVerStr);

                newVerf = Float.parseFloat(s);

                if (nowVerStr.length() == 5) {
                    String nowVerStr1 = nowVerStr.substring(0, nowVerStr.indexOf(".") + 1);
                    String nowVerStr2 = nowVerStr.substring(nowVerStr.indexOf(".") + 1, nowVerStr.length());
                    nowVerStr2 = nowVerStr2.replace(".", "");
                    nowVerf = Float.parseFloat(nowVerStr1 + nowVerStr2);
                } else {
                    nowVerf = Float.parseFloat(nowVerStr);
                }

                if (nowVerf >= newVerf) {
                    recentVersion_Text.setVisibility(View.VISIBLE);
                    update_please.setVisibility(View.GONE);
                } else if (nowVerf < newVerf) {
                    recentVersion_Text.setVisibility(View.GONE);
                    update_please.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();

                param.put("ver", ver);
                param.put("os", "android");
                param.put("app_Idx", Control.app_Idx);
                param.put("isGettingVer", "Y");

                return param;
            }
        };
        Application.application.addToRequestQueue(client);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_titlebar_close:
                finish();
                break;

            case R.id.update_please:
                Control.singleInstance().gotoMarket(this, getPackageName());
                break;
        }
    }
}
