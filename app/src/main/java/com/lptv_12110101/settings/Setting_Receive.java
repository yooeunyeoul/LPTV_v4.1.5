package com.lptv_12110101.settings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.lptv_12110101.Control;
import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.Library;


public class Setting_Receive extends AppCompatActivity implements View.OnClickListener {

    private View networkErrorLy;
    private Boolean isRetry = false, isError = false;
    private String failUrl = "";
    private WebView receive_webview;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_receive);
        Application.actList.add(this);

        TextView setting_titlebar_title = (TextView) findViewById(R.id.setting_titlebar_title);
        setting_titlebar_title.setText("알림 수신 설정");

        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);

        networkErrorLy = (View) findViewById(R.id.networkErrorLy); //네트워크 오류 레이아웃
        networkErrorLy.setVisibility(View.GONE);
        Button refreshBtn = (Button) findViewById(R.id.refreshBtn); // 네트워크 재시도 버튼

        /*******************************************************************************/
        receive_webview = (WebView) findViewById(R.id.receive_webview);
        receive_webview.getSettings().setJavaScriptEnabled(true);
        receive_webview.loadUrl(Application.application.getLptvDomain() + Control.app_Alarmsys);
        receive_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (Library.getConnectionStatus(Setting_Receive.this) == 0) {
                    networkErrorLy.setVisibility(View.VISIBLE);
                } else {
                    if (isRetry) {
                        networkErrorLy.setVisibility(View.VISIBLE);
                    } else {
                        networkErrorLy.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (isRetry) {
                    if (!isError) {
                        networkErrorLy.setVisibility(View.GONE);
                        failUrl = "";
                        isRetry = false;
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                isError = true;
                failUrl = failingUrl;
                networkErrorLy.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        /*******************************************************************************/

        setting_titlebar_close.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_titlebar_close:
                finish();
                break;

            case R.id.refreshBtn:
                isRetry = true;
                isError = false;
                receive_webview.loadUrl(failUrl);
                break;
        }
    }
}