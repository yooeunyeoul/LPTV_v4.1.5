package com.lptv_12110101;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AppLog;


public class PlayFullScreenActivity extends AppCompatActivity {
    private WebView fullscreen_webview;

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playfullscreen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 화면 꺼짐 방지
        Application.actList.add(this);

        String getOnAir_Url = getIntent().getStringExtra("play_url");

        fullscreen_webview = (WebView) findViewById(R.id.fullscreen_webview);

        fullscreen_webview.getSettings().setJavaScriptEnabled(true);
        fullscreen_webview.getSettings().setDomStorageEnabled(false);

        fullscreen_webview.setWebChromeClient(new WebChromeClient());

        fullscreen_webview.loadUrl(getOnAir_Url);
        fullscreen_webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppLog.e("***********_onDestroy", "onDestroy");

        /*** 영상이 엑티비티가 종료되어도 실행이 되어서 아래 코드로 웹뷰를 완전 종료 시킴  **************************************************/
        if (fullscreen_webview != null) {
            try {
                Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(fullscreen_webview, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
                AppLog.e("onDestroy","e-> " +e.toString());
            }
        }
        /****************************************************************************************************************************/
    }
}