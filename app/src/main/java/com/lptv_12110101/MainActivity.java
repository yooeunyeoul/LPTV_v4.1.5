package com.lptv_12110101;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apms.sdk.APMS;
import com.apms.sdk.api.APIManager;
import com.apms.sdk.api.request.LogoutPms;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.MeResponseCallback;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.exception.KakaoException;
import com.kakao.helper.Logger;
import com.kakao.widget.LoginButton;
import com.loopj.android.http.HttpGet;
import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AppLog;
import com.lptv_12110101.library.Library;
import com.lptv_12110101.library.SocialShareControl;
import com.lptv_12110101.library.VersionCheck;
import com.lptv_12110101.pms.PmsControl;
import com.lptv_12110101.settings.Settings;
import com.naver.wcs.WCSLogEventAPI;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Boolean isAppLog = false;
    public ValueCallback<Uri[]> uploadMessage;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 200;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_STATE = 300;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;

    private Context mContext;
    private ProgressBar progress, toMain_progress;
    private WebView webview;
    private DrawerLayout drawerLayout;

    private String  failUrl = "", playUrl, onAirUrl, impdUrl, settingsLogin = null; // DIALOG_CARDNM,

    private OAuthLogin naver_mOAuthLoginInstance;
    private SessionCallback callback;
    private CallbackManager callbackManager;
    private Boolean isFbCallbackManager = false, isRetry = false, isError = false;

    private View networkErrorLy;
    private Uri mCapturedImageURI;

    private WCSLogEventAPI wcslog;
    private Intent captureIntent = null;

    private FrameLayout mFullscreenContainer;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    /**********************************************************************************************/
    /*************************이전 체인지TV 로그인 유지를 위해 필요함******************************/
    /**********************************************************************************************/
    public enum AccountType {
        Normal(100), Naver(101), Facebook(102), Kakao(103);
        private int value;

        AccountType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    private static String removeString(String str, int startIndex, int length) {
        return str.substring(0, startIndex) + str.substring(startIndex + length, str.length());
    }
    // 복호화 작업
    public static String DecryptLoginInfo(String toDecode) {
        String last3 = toDecode.substring(100, 101) + toDecode.substring(103, 104) + toDecode.substring(122, 123);
        toDecode = removeString(toDecode, 0, 101);
        toDecode = removeString(toDecode, 2, 16);
        toDecode = removeString(toDecode, 5, 21);
        toDecode = toDecode.substring(0, toDecode.length() - 2) + last3 + toDecode.substring(toDecode.length() - 2, toDecode.length());

        String decryptedStr = new String(Base64.decode(new String(Base64.decode(toDecode, 0)), 0));

        Boolean shouldSubString = true;

        for (int i = 0; i < 10; i++) {
            if (decryptedStr.charAt(i) != '|') {
                shouldSubString = false;
                break;
            }
        }
        if (shouldSubString) {
            decryptedStr = decryptedStr.substring(10, decryptedStr.length());
        }
        return decryptedStr;
    }
    /**********************************************************************************************/
    /*************************이전 체인지TV 로그인 유지를 위해 필요함******************************/
    /**********************************************************************************************/

    private static class FullscreenHolder extends FrameLayout{
        public FullscreenHolder(Context context){
            super(context);
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        FacebookSdk.setApplicationId(Control.fbApplicationId);
        setContentView(R.layout.main);

        /******************************************************************************************/
        /*****************************테스트 서버 사용 유무****************************************/
        /******************************************************************************************/
        Application.application.setIsTestMode(false);
        /******************************************************************************************/
        /*****************************테스트 서버 사용 유무****************************************/
        /******************************************************************************************/

        Application.actList.add(this);
        mContext = this;

        wcslog = WCSLogEventAPI.getInstance(mContext);  // 네이버 애널리틱스

        /******************************* UI 구성 **************************************************/
        drawerLayout = findViewById(R.id.drawerLayout);

        progress = findViewById(R.id.progress);
        toMain_progress = findViewById(R.id.progress02);

        ImageView bottom_gotoHome = findViewById(R.id.bottom_gotoHome);
        ImageView bottom_back_Page = findViewById(R.id.bottom_back_Page);
        ImageView bottom_next_Page = findViewById(R.id.bottom_next_Page);
        ImageView bottom_reset_Page = findViewById(R.id.bottom_reset_Page);
        ImageView bottom_page_Share = findViewById(R.id.bottom_page_Share);
        ImageView bottom_app_settings = findViewById(R.id.bottom_app_settings);

        networkErrorLy = findViewById(R.id.networkErrorLy); //네트워크 오류 레이아웃
        networkErrorLy.setVisibility(View.GONE);
        Button refreshBtn = findViewById(R.id.refreshBtn); // 네트워크 재시도 버튼

        /*************************** sns 로그인 ***************************************/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // 네이버
        OAuthLoginDefine.DEVELOPER_VERSION = true;
        initData();
        // 카카오
       callback = new Sessioncallback();

        Session.initializeSession(getApplicationContext(), callback);
        // 페이스북
        callbackManager = CallbackManager.Factory.create();

        /******************************* Main WebView ********************************************************/
        webview = findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);  // @SuppressLint("SetJavaScriptEnabled") 사용.
        webview.getSettings().setAllowFileAccess(true);

        // 캐시 삭제
        webview.clearHistory();
        webview.clearCache(true);
        webview.clearView();

        // 쿠키 삭제
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieSyncManager.sync();

        // 롤리팝 이상 버전에서 결제가 안되는게 있어서 코드 추가
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(webview.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webview, true);
        }

        /*************** 개편 전 체인지TV와 로그인 정보를 유지 시키기 위해 ************************/
        if (!TextUtils.isEmpty(Application.application.getAppPreferencesString("lptv_userId"))) {
            Control.singleInstance().setAppPre_Id(getApplicationContext(), DecryptLoginInfo(Application.application.getAppPreferencesString("lptv_userId")));
            if (!TextUtils.isEmpty(Application.application.getAppPreferencesString("lptv_pwd"))) {
                Control.singleInstance().setAppPre_pwd(getApplicationContext(), DecryptLoginInfo(Application.application.getAppPreferencesString("lptv_pwd")));
            }
            int i = Application.application.getAppPreferencesInteger("lptv_accountType");
            AccountType aa = AccountType.values()[i];
            String ss = aa.toString();
            if (ss.equals("Normal")) {            // 통합 회원
                Control.singleInstance().setAppPre_loginType("lptv");
            } else if (ss.equals("Naver")) {     // 네이버
                Control.singleInstance().setAppPre_loginType("nv");
            } else if (ss.equals("Facebook")) {  // 페이스북
                Control.singleInstance().setAppPre_loginType("fb");
            } else if (ss.equals("Kakao")) {     // 카카오톡
                Control.singleInstance().setAppPre_loginType("ka");
            }

            Application.application.setAppPreferences("lptv_userId", "");
            Application.application.setAppPreferences("lptv_pwd", "");
            Application.application.setAppPreferences("lptv_accountType", 0);
        }
        /***************************************************************************************************************************************/
        /***************************************************************************************************************************************/
        /***************************************************************************************************************************************/
        //WebView Loading Start(WebView init)
        String category_url = "";
        if (!TextUtils.isEmpty(getIntent().getStringExtra("category_url"))) {
            category_url = Library.decode(getIntent().getStringExtra("category_url"));
            category_url = Library.encode(category_url);
        }

        webview.loadUrl(Application.application.getLptvDomain() + Control.changeTV_firstPage + category_url);
        /***************************************************************************************************************************************/
        /***************************************************************************************************************************************/
        /***************************************************************************************************************************************/
        webview.addJavascriptInterface(new ChangeTVJavaScriptInterface(), "HTMLOUT");
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if(mCustomView != null){
                    callback.onCustomViewHidden();
                    return;
                }
                int mOriginalOrientation = getRequestedOrientation();
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(MainActivity.this);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
                setRequestedOrientation(mOriginalOrientation);

                super.onShowCustomView(view, callback);
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                super.onShowCustomView(view, requestedOrientation, callback);
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null) {
                    return;
                }
                setFullscreen(false);
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(mFullscreenContainer);
                mFullscreenContainer = null;
                mCustomView = null;
                mCustomViewCallback.onCustomViewHidden();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                Library.singleInstance().showAlert(mContext, message, "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                return true;
            }


            /*************** 파일 업로드 *******************************************************************************************************************/
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.e("@@onShowFileChooser@@", "@@onShowFileChooser@@");
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }
                    uploadMessage = filePathCallback;

                    String cameraType = fileChooserParams.getAcceptTypes().clone()[0].toUpperCase();
                    String[] split_CameraType = cameraType.split("/");

                    cameraType = split_CameraType[0];

                    File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "체인지TV");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }

                    if (cameraType.equals("IMAGE")) {//  카메라 호출(사진 찍는 기능)
                        File image_File = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".png");
                        mCapturedImageURI = Uri.fromFile(image_File);

                        captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                    } else if (cameraType.equals("VIDEO")) { // 카메라 호출(녹화 찍는 기능)
                        File video_File = new File(imageStorageDir + File.separator + "VIDEO_" + String.valueOf(System.currentTimeMillis()) + ".mp4");
                        mCapturedImageURI = Uri.fromFile(video_File);

                        captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    }

                    Boolean isPermissionCamera = false;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            checkAppPermissionCamera();
                        }else{
                            isPermissionCamera = true;
                        }
                    }else{
                        isPermissionCamera = true;
                    }

                    if(isPermissionCamera){
                        /*********** 사진 또는 영상 문서 파일 **********************************************************************************************/
                        Intent intent = fileChooserParams.createIntent();
                        /********************************************************************************************************************************/

                        Intent chooserIntent = Intent.createChooser(intent, "작업 선택");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
                        startActivityForResult(chooserIntent, REQUEST_SELECT_FILE);
                    }
                }
                return true;
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                AppLog.e("webview_onPageStarted", "onPageStarted-> " + url);

                /********** 첫 로딩 할 경우와 검색하는 경우 프로그래스 생성 ************/
                if (url.toUpperCase().contains(Control.changeTV_firstPage.toUpperCase()) || url.toUpperCase().contains(Control.search_url.toUpperCase())) {
                    toMain_progress.setVisibility(View.VISIBLE);
                }

                /************** 구글 통계, 네이버 통계 url을 보냄  *****************/
                if (url.toUpperCase().contains(Application.application.getLptvDomain().toUpperCase()) && !url.toUpperCase().contains(Control.changeTV_firstPage.toUpperCase())) {
                    Application.application.sendPageViewToGoogleAnalytics(url.toUpperCase());
                    wcslog.onTrackSite((Activity) mContext, url.toUpperCase());
                }

                /************** 에러 페이지 유/무 **********************************************************/
                if (Library.getConnectionStatus(MainActivity.this) == 0) {
                    networkErrorLy.setVisibility(View.VISIBLE);
                } else {
                    if (isRetry) {
                        networkErrorLy.setVisibility(View.VISIBLE);
                    } else {
                        networkErrorLy.setVisibility(View.GONE);
                    }
                }

                /**************** 로그인 클릭 시 이전 url을 저장 시켜 로그인 후 load 시켜줌 ******************/
                if (url.toUpperCase().contains(Control.changeTV_login.toUpperCase())) {
                    Uri pageStartUri = Uri.parse(url);
                    String pageStartgetUrl = pageStartUri.getQueryParameter("url");

                    if (pageStartgetUrl != null) {
                        if (pageStartgetUrl.toUpperCase().contains("M/MYPAGE/LEFTMYMENU.ASPX")) {
                            Control.singleInstance().setAppPre_url(Application.application.getLptvDomain() + Control.changeTV_Main);
                        } else{
                            Control.singleInstance().setAppPre_url(pageStartgetUrl);
                        }
                    }
                }

                /*********** Main Webview 로드시작 시 LeftMenu Webview는 reload 시킴 *************************/
//                leftmenuWebview.reload();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                AppLog.e("webView_onPageFinished", "url:" + url);

                /********* 에러 페이지 유/무 ********************************************************** */
                if (!isRetry) {
                    progress.setVisibility(View.GONE);
                    toMain_progress.setVisibility(View.GONE);
                } else {
                    if (!isError) {
                        networkErrorLy.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        toMain_progress.setVisibility(View.GONE);
                        failUrl = "";
                        isRetry = false;
                    }
                }

                /************************ 파일 업로드 롤리팝 이상에만 지원 ********************************/
                if (url.toUpperCase().contains(Control.healingItem)
                        || url.toUpperCase().contains(Control.healingTime)
                        || url.toUpperCase().contains(Control.myInfoPage)
                        || url.toUpperCase().contains(Control.iamPd)) {     // 롤리팝 이하는 false, 이상은 true로 보냄.(업로드가 롤리팝이상만 되기 때문)
                    String isUpload = "true";
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        isUpload = "false";
                    }
                    view.loadUrl("javascript:isFileUpload('" + isUpload + "');");
                }

                /*********** 로딩 후 페이지와 검색이 끝나는 페이지에서 프로그레스 제거 ************************************************************/
                if (webview.copyBackForwardList().getCurrentIndex() == 1 || url.toUpperCase().contains(Control.search_url.toUpperCase())) {
                    toMain_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                /************** 구글 통계 오류 내용을 보냄  ***********************************************************************************/
                Application.application.sendEventToGoogleAnalytics("Error", failingUrl.toUpperCase(), errorCode + " , " + description, null);

                /*********** 에러 페이지 유/무 ***********************************************************************************************/
                isError = true;
                failUrl = failingUrl;
                progress.setVisibility(View.GONE);
                toMain_progress.setVisibility(View.GONE);
                networkErrorLy.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String real_url = url.toUpperCase();
                Uri mUri = Uri.parse(url);
//                Log.e("SOUL_urlChekc", "url:" + url);
                Log.e("shouldOverrideUrlLoad", "url:" + url);

                if (real_url.startsWith("COM.LPTV.12110101")) {
                    String cmd = mUri.getQueryParameter("cmd").toUpperCase();
                    String toUpperHostName = mUri.getHost().toUpperCase();
                    Log.e("wshouldOverrideUrlLoad", "cmd:" + url);

                    if (toUpperHostName.equals("LOGIN")) {
                        if (cmd.equals("HAVEIDPWD")) {                 // 로그인 후 다시 시작하는데 id/pwd 있는경우
                            String haveId = Control.singleInstance().getAppPre_Id(getApplicationContext());
                            String havePwd = Control.singleInstance().getAppPre_pwd(getApplicationContext());
                            String haveType = Control.singleInstance().getAppPre_loginType();

                            view.loadUrl("javascript:sendLoginData('" + haveId + "','" + havePwd + "','" + haveType + "');");

                        } else if (cmd.equals("EXCHANGEIDPWD")) {        // 로그인 후 다시 시작하는데 1.탈퇴한 회원 2.휴면계정 3.비번 변경된 경우
                            view.loadUrl("javascript:goMainPage();");

                            Control.singleInstance().setAppPre_Id(getApplicationContext(), "");
                            Control.singleInstance().setAppPre_pwd(getApplicationContext(), "");

                        } else if (cmd.equals("AUTOLOGIN")) {            // 로그인 페이지에서 로그인 버튼을 누를 경우
                            String getId = mUri.getQueryParameter("id");
                            String getPwd = mUri.getQueryParameter("pwd");
                            String getUrl = mUri.getQueryParameter("url");

                            if (getUrl.toUpperCase().contains("/M/MYPAGE/LEFTMYMENU.ASPX")) {
                                getUrl = Control.singleInstance().getAppPre_url(getApplicationContext());
                            }

                            if(!TextUtils.isEmpty(settingsLogin)){
                                getUrl = settingsLogin;
                            }

                            String domain = Application.application.getLptvDomain();

                            if (!getUrl.toUpperCase().contains(domain.toUpperCase()) && !getUrl.toUpperCase().contains(domain.toUpperCase().replace("HTTP://", "HTTPS://"))) {
                                getUrl = domain.toUpperCase() + getUrl;
                            }

                            view.loadUrl(getUrl);

                            // 처음 pms로그인 할때, deviceId로 로그인 되기때문에 체인지TV로 로그인할때 다시 PMS로그인 한다.
                            APMS.getInstance(getApplicationContext()).setCustId(getId);
                            PmsControl.singleInstance().getCertToPMS(mContext);

                            Control.singleInstance().setAppPre_Id(getApplicationContext(), getId);
                            Control.singleInstance().setAppPre_pwd(getApplicationContext(), getPwd);
                            Control.singleInstance().setAppPre_loginType("lptv");

                        } else if (cmd.equals("LOGOUT")) {              // 로그아웃
                            view.loadUrl(Application.application.getLptvDomain() + Control.changeTV_Main);

                            String socialFlag = Control.singleInstance().getAppPre_loginType().toUpperCase();

                            if (socialFlag.equals("FB")) {      // 페이스북으로 로그인 할 경우에만 로그아웃 시킴.
                                LoginManager.getInstance().logOut();
                            } else if (socialFlag.equals("KA")) {   // 카카오톡 로그인 할 경우에만 로그아웃 시킴.
                                UserManagement.requestLogout(new LogoutResponseCallback() {
                                    @Override
                                    protected void onSuccess(long userId) {
                                    }

                                    @Override
                                    protected void onFailure(APIErrorResult errorResult) {
                                    }
                                });
                            } else if (socialFlag.equals("NV")) {   // 네이버 로그인 할 경우에만 로그아웃 시킴.
                                OAuthLogin naver_mOAuthLoginInstance = OAuthLogin.getInstance();
                                naver_mOAuthLoginInstance.init(mContext, Control.Naver_OAUTH_CLIENT_ID, Control.Naver_OAUTH_CLIENT_SECRET, Control.Naver_OAUTH_CLIENT_NAME);
                                naver_mOAuthLoginInstance.logout(mContext);
                            }

                            new LogoutPms(getApplicationContext()).request(new APIManager.APICallback() {       // PMS 로그아웃
                                @Override
                                public void response(String s, JSONObject jsonObject) {
                                    AppLog.e("PMS-LOGOUT", "s-> " + s + " , jsonObject-> " + jsonObject);
                                }
                            });

                            Control.singleInstance().setAppPre_Id(getApplicationContext(), "");
                            Control.singleInstance().setAppPre_pwd(getApplicationContext(), "");
                            Control.singleInstance().setAppPre_loginType("");
                            Control.singleInstance().setAppPre_url(null);

                            if(!TextUtils.isEmpty(settingsLogin)){
                               settingsLogin = null;
                            }

                        } else if (cmd.equals("LOGINWITHNAVER")) {           // 네이버 소셜 로그인
                            naver_mOAuthLoginInstance.startOauthLoginActivity(MainActivity.this, naver_mOAuthLoginHandler);

                        } else if (cmd.equals("LOGINWITHKAKAO")) {           // 카카오톡 소셜 로그인
                            LoginButton kakaoBtn = (LoginButton) findViewById(R.id.kakaoBtn);
                            kakaoBtn.performClick();

                            Session.getCurrentSession().close(callback);

                            kakaoBtn.setLoginSessionCallback(new SessionCallback() {
                                @Override
                                public void onSessionOpened() {
                                    UserManagement.requestMe(new MeResponseCallback() {
                                        @Override
                                        protected void onSuccess(UserProfile userProfile) {
                                            String kakao_id = Long.toString(userProfile.getId());
                                            String kakao_nickname = userProfile.getNickname();
                                            String kakaoLogin_success = Application.application.getLptvDomain() + "/m/login/SocialIdChk.aspx?ref=app&SocialTypeVal=ka&myName=" + kakao_nickname + "&myId=" + kakao_id;

                                            webview.loadUrl(kakaoLogin_success);
                                        }

                                        @Override
                                        protected void onNotSignedUp() {
                                            AppLog.e("onNotSignedUp", " onNotSignedUp");
                                        }

                                        @Override
                                        protected void onSessionClosedFailure(APIErrorResult errorResult) {
                                            AppLog.e("onSessionClosedFailure", "errorResult : " + errorResult);
                                        }

                                        @Override
                                        protected void onFailure(APIErrorResult errorResult) {
                                            AppLog.e("fail_kakao", "fail : " + errorResult);
                                        }
                                    });
                                }

                                @Override
                                public void onSessionClosed(KakaoException exception) {
                                    AppLog.e("onSessionClosed", "-> " + exception);
                                }
                            });

                        } else if (cmd.equals("LOGINWITHFACEBOOK")) {        // 페이스북 소셜 로그인인
                            isFbCallbackManager = true;

                            com.facebook.login.widget.LoginButton facebookBtn = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebookBtn);
                            facebookBtn.performClick();
                            facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            try {
                                                String facebook_id = object.getString("id");
                                                String facebook_name = object.getString("name");

                                                String domain = Application.application.getLptvDomain();
                                                String facebookLogin_success = domain + "/m/login/SocialIdChk.aspx?ref=app&SocialTypeVal=fb&myName=" + facebook_name + "&myId=" + facebook_id;

                                                webview.loadUrl(facebookLogin_success);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    request.executeAsync();
                                }

                                @Override
                                public void onCancel() {
                                    AppLog.e("FB_Login_Cancel", "@@@@@@@@->>> Cancel");
                                }

                                @Override
                                public void onError(FacebookException error) {
                                    AppLog.e("FB_Login_Error", "********->> Error");
                                }
                            });
                        } else if (cmd.equals("SNSLOGINFINISHED")) {        // 소셜로그인 완료 후
                            String getType = mUri.getQueryParameter("type");
                            String getUserId = mUri.getQueryParameter("userId");
                            String getSnsUrl = Library.decode(mUri.getQueryParameter("url"));

                            // 처음 pms로그인 할때, deviceId로 로그인 되기때문에 체인지TV로 로그인할때 다시 PMS로그인 한다.
                            APMS.getInstance(getApplicationContext()).setCustId(getUserId);
                            PmsControl.singleInstance().getCertToPMS(mContext);

                            Control.singleInstance().setAppPre_Id(getApplicationContext(), getUserId);
                            Control.singleInstance().setAppPre_pwd(getApplicationContext(), "");
                            Control.singleInstance().setAppPre_loginType(getType);

                            webview.loadUrl(getSnsUrl);
                        }
                    } else if (toUpperHostName.equals("CONTENTINFO")) {      // 사이트소개>서비스안내>콘텐츠안내
                        String contentInfo_url = null;

                        if (cmd.equals("HEALINGAUDIO")) {               // 힐링 오디오 버튼
                            contentInfo_url = Control.healingAudio;
                        } else if (cmd.equals("SHAREENERGY")) {         //  에너지 나누기 버튼
                            contentInfo_url = Control.shareEnergy;
                        }
                        Uri content_uri = Uri.parse(Application.application.getLptvDomain() + contentInfo_url);

                        Control.singleInstance().intentToActionView(mContext, content_uri);

                    } else if (toUpperHostName.equals("MOVIE")) {        // 영상 클릭 시 내부or외부 플레이어
                        if (cmd.equals("PLAY")) {
                            playUrl = Library.decode(mUri.getQueryParameter("url"));

                            if (Application.application.getAppPreferencesBoolean("playerType")) {
                                if (Library.getConnectionStatus(mContext) == 1) {
                                    Library.singleInstance().showAlert(mContext, "3G/LTE로 시청 시,\n요금제에 따라 데이터 통화료가 부과될 수 있습니다.",
                                            "확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Control.singleInstance().intentToActivity_putStringData(mContext, VideoPlayerActivity.class, "playUrl", playUrl.trim());
                                                }
                                            }, "취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {}
                                            });
                                } else if (Library.getConnectionStatus(mContext) == 2) {
                                    Control.singleInstance().intentToActivity_putStringData(mContext, VideoPlayerActivity.class, "playUrl", playUrl.trim());
                                }
                            } else {
                                Uri play_uri = Uri.parse(playUrl.trim());
                                Intent play_intent = new Intent(Intent.ACTION_VIEW);
                                play_intent.setDataAndType(play_uri, "video/*");
                                startActivity(play_intent);
                            }
                        }
                    } else if (toUpperHostName.equals("ONAIR")) {      // On-Air 전체보기 안될 경우 멘트 클릭 시
                        if (cmd.equals("PLAY")) {
                            Uri onAir_Uri = Uri.parse(Application.application.getLptvDomain() + Control.onAirPage);
                            Control.singleInstance().intentToActionView(mContext, onAir_Uri);
                        }
                    } else if (toUpperHostName.equals("IMPD")){     // 나는 PD다 - 영상
                        if(cmd.equals("PLAY")){
                            impdUrl = mUri.getQueryParameter("movieUrl");

                            if (Library.getConnectionStatus(mContext) == 1) {
                                Library.singleInstance().showAlert(mContext, "3G/LTE로 시청 시,\n요금제에 따라 데이터 통화료가 부과될 수 있습니다.",
                                        "확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Control.singleInstance().intentToActivity_putStringData(mContext, PlayFullScreenActivity.class, "play_url", impdUrl);
                                            }
                                        }, "취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {}
                                        });
                            } else if (Library.getConnectionStatus(mContext) == 2) {
                                Control.singleInstance().intentToActivity_putStringData(mContext, PlayFullScreenActivity.class, "play_url", impdUrl);
                            }
                        }
                    } else if (toUpperHostName.equals("NEWBROWSER")){       // 새 창(웹 브라우저 연결)
                        if(cmd.equals("GOTOBROWSER")){
                            String getPageUrl = mUri.getQueryParameter("pageurl");

                            if(!TextUtils.isEmpty(getPageUrl)){
                                // 1.탈퇴한회원, 2.휴면계정, 3.전화번호변경으로 인한 회원 일 경우 4. 전화번호 변경 페이지
                                if(getPageUrl.toUpperCase().contains("/MEMBER/MEMAGREEMENT.ASPX?") || getPageUrl.toUpperCase().contains("/MEMBER/DORMANTACCOUNTDEFAULT.ASPX?")
                                        || getPageUrl.toUpperCase().contains("/MEMBER/POP_REAUTHORIZATION.ASPX?") || getPageUrl.toUpperCase().contains("/MEMBER/CELLCHANGEINTRO.ASPX?")){
                                    String splitUrl = url.split("&pageurl=")[1];
                                    getPageUrl = Library.decode(splitUrl);

                                    webview.loadUrl(webview.getUrl());
                                }

                                Uri pageUri = Uri.parse(getPageUrl);
                                Control.singleInstance().intentToActionView(mContext, pageUri);
                            }
                        }
                    } else if(toUpperHostName.equals("APPSETTINGS")){
                        if(cmd.equals("SETTINGS")){
                            Intent settings_intent = new Intent(MainActivity.this, Settings.class);
                            startActivityForResult(settings_intent, 0);
                        }
                    }
                    return true;
                } else if (real_url.contains("/M/MYPAGE/LEFTMYMENU.ASPX")) {    // left 메뉴
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                }
                else if (real_url.contains("WWW.USTREAM.TV/EMBED/")) {     // ON-Air 클릭 시 전체 보기
                    onAirUrl = url;
                    if (Library.getConnectionStatus(mContext) == 1) {
                        Library.singleInstance().showAlert(mContext, "3G/LTE로 시청 시,\n요금제에 따라 데이터 통화료가 부과될 수 있습니다.",
                                "확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Control.singleInstance().intentToActivity_putStringData(mContext, PlayFullScreenActivity.class, "play_url", onAirUrl);
                                    }
                                }, "취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                    } else if (Library.getConnectionStatus(mContext) == 2) {
                        Control.singleInstance().intentToActivity_putStringData(mContext, PlayFullScreenActivity.class, "play_url", onAirUrl);
                    }
                    return true;
                }
                //ISP결제 관련
                else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                    Intent intent;

                    try {
                        AppLog.e("<INIPAYMOBILE>", "intent url : " + url);
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException ex) {
                        AppLog.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                        return false;
                    }

                    Uri uri = Uri.parse(intent.getDataString());
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(intent);
                        /*가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다.
                            삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에
		    			    조건을 걸어 종료하도록 하였습니다.*/
                        if (url.startsWith("ispmobile://")) {
//		    				finish();
                        }
                    } catch (ActivityNotFoundException e) {
                        AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url);
                        AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, uri.getScheme()" + intent.getDataString());

                        if (url.startsWith("ispmobile://")) { //ISP
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("hdcardappcardansimclick://")) { //현대앱카드
//                            DIALOG_CARDNM = "HYUNDAE";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 현대앱카드설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://")) { //신한앱카드
//                            DIALOG_CARDNM = "SHINHAN";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 신한카드앱설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("mpocket.online.ansimclick://")) { //삼성앱카드
//                            DIALOG_CARDNM = "SAMSUNG";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 삼성카드앱설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("lottesmartpay://")) { //롯데 모바일결제
//                            DIALOG_CARDNM = "LOTTE";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데모바일결제 설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("lotteappcard://")) { //롯데앱카드(간편결제)
//                            DIALOG_CARDNM = "LOTTEAPPCARD";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데앱카드 설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("kb-acp://")) { //KB앱카드
//                            DIALOG_CARDNM = "KB";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, KB카드앱설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        } else if (intent.getDataString().startsWith("hanaansim://")) { //하나SK카드 통합안심클릭앱
//                            DIALOG_CARDNM = "HANASK";
                            AppLog.e("INIPAYMOBILE", "INIPAYMOBILE, 하나카드앱설치 ");
                            view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                            return false;
                        }
                    /*
                    //신한카드 SMART신한 앱
					else if( intent.getDataString().startsWith("smshinhanansimclick://"))
					{
						DIALOG_CARDNM = "SHINHAN_SMART";
						DhLog.e("INIPAYMOBILE", "INIPAYMOBILE, Smart신한앱설치");
						view.loadData("<html><body></body></html>", "text/html", "euc-kr");
						showDialog(DIALOG_CARDAPP);
						return false;
					}
					*/
                        /**
                         > 현대카드 안심클릭 droidxantivirusweb://
                         - 백신앱 : Droid-x 안드로이이드백신 - NSHC
                         - package name : net.nshc.droidxantivirus
                         - 특이사항 : 백신 설치 유무는 체크를 하고, 없을때 구글마켓으로 이동한다는 이벤트는 있지만, 구글마켓으로 이동되지는 않음
                         - 처리로직 : intent.getDataString()로 하여 droidxantivirusweb 값이 오면 현대카드 백신앱으로 인식하여
                         하드코딩된 마켓 URL로 이동하도록 한다.
                         */
                        else if (intent.getDataString().startsWith("droidxantivirusweb")) { //현대카드 백신앱
                            /*************************************************************************************/
                            Log.d("<INIPAYMOBILE>", "ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: ");
                            /*************************************************************************************/
                            Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
                            hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
                            startActivity(hydVIntent);
                        } else if (url.startsWith("intent://")) { //INTENT:// 인입될시 예외 처리
                            /**
                             > 삼성카드 안심클릭
                             - 백신앱 : 웹백신 - 인프라웨어 테크놀러지
                             - package name : kr.co.shiftworks.vguardweb
                             - 특이사항 : INTENT:// 인입될시 정상적 호출

                             > 신한카드 안심클릭
                             - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                             - package name : com.TouchEn.mVaccine.webs
                             - 특이사항 : INTENT:// 인입될시 정상적 호출

                             > 농협카드 안심클릭
                             - 백신앱 : V3 Mobile Plus 2.0
                             - package name : com.ahnlab.v3mobileplus
                             - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                             > 외환카드 안심클릭
                             - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                             - package name : com.TouchEn.mVaccine.webs
                             - 특이사항 : INTENT:// 인입될시 정상적 호출

                             > 씨티카드 안심클릭
                             - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                             - package name : com.TouchEn.mVaccine.webs
                             - 특이사항 : INTENT:// 인입될시 정상적 호출

                             > 하나SK카드 안심클릭
                             - 백신앱 : V3 Mobile Plus 2.0
                             - package name : com.ahnlab.v3mobileplus
                             - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                             > 하나카드 안심클릭
                             - 백신앱 : V3 Mobile Plus 2.0
                             - package name : com.ahnlab.v3mobileplus
                             - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                             > 롯데카드
                             - 백신이 설치되어 있지 않아도, 결제페이지로 이동
                             */

                            /*************************************************************************************/
                            Log.d("<INIPAYMOBILE>", "Custom URL (intent://) 로 인입될시 마켓으로 이동되는 예외 처리: ");
                            /*************************************************************************************/
                            try {

                                Intent excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                String packageNm = excepIntent.getPackage();

                                Log.d("<INIPAYMOBILE>", "excepIntent getPackage : " + packageNm);

                                excepIntent = new Intent(Intent.ACTION_VIEW);
                                excepIntent.setData(Uri.parse("market://search?q=" + packageNm));

                                startActivity(excepIntent);

                            } catch (URISyntaxException e1) {
                                AppLog.e("<INIPAYMOBILE>", "INTENT:// 인입될시 예외 처리  오류 : " + e1);
                            }

                        }
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        /**************************************************************************************************/

        /********************* 클릭 리스너 등록 ***************************************************************/
        bottom_gotoHome.setOnClickListener(this);
        bottom_back_Page.setOnClickListener(this);
        bottom_next_Page.setOnClickListener(this);
        bottom_reset_Page.setOnClickListener(this);
        bottom_page_Share.setOnClickListener(this);
        bottom_app_settings.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        /****************************************************************************************************/
        /************************* onCreate finish **********************************************************/

//        setPMSSetting();
//        Log.e("##key_hash##", "key:" + getKeyHash(MainActivity.this));
    }

    private void setFullscreen(boolean enabled) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);

    }
//    public static String getKeyHash(final Context context) {
//        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
//        if (packageInfo == null)
//            return null;
//
//        for (Signature signature : packageInfo.signatures) {
//            try {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
//            } catch (NoSuchAlgorithmException e) {
//                Log.e("TAG_", "Unable to get MessageDigest. signature=" + signature, e);
//            }
//        }
//        return null;
//    }

    class ChangeTVJavaScriptInterface {
        @JavascriptInterface
        public void getHTMLData(String _hdnTitle, String _hdnDesc, String _hdnThumb, String _hdnWidth, String _hdnHeight, String _hdnUrl) {
            Message msg = handler.obtainMessage();
            Bundle bun = new Bundle();
            bun.putString("appHdnTitle", _hdnTitle);
            bun.putString("appHdnDesc", _hdnDesc);
            bun.putString("appHdnThumb", _hdnThumb);
            bun.putString("appHdnWidth", _hdnWidth);
            bun.putString("appHdnHeight", _hdnHeight);
            bun.putString("appHdnUrl", _hdnUrl);
            msg.setData(bun);
            handler.sendMessage(msg);
        }
    }

    private final MyHandler handler = new MyHandler();
    private class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            String appHdnTitle = msg.getData().getString("appHdnTitle");
            String appHdnDesc = msg.getData().getString("appHdnDesc");
            String appHdnThumb = msg.getData().getString("appHdnThumb");
            String appHdnWidth = msg.getData().getString("appHdnWidth");
            String appHdnHeight = msg.getData().getString("appHdnHeight");
            String appHdnUrl = msg.getData().getString("appHdnUrl");

//            if(TextUtils.isEmpty(appHdnWidth) || TextUtils.isEmpty(appHdnHeight)){
//                appHdnWidth = "800";
//                appHdnHeight = "800";
//            }
            SocialShareControl.ShareInstance().shareContentInfo(MainActivity.this, appHdnTitle, appHdnDesc, appHdnThumb, Integer.parseInt(appHdnWidth), Integer.parseInt(appHdnHeight), appHdnUrl, appHdnUrl, "웹에서 보기", false);
        }
    }

    /***********************************************************************************************
     *@setPMSSetting :  로그인 아이디 및 PMS 설정
     **********************************************************************************************/
    public void setPMSSetting() {
        String pmsLoginID = Control.singleInstance().getAppPre_Id(getApplicationContext());
        Boolean isPopupNoti = true;

        APMS pms = APMS.getInstance(this);
        pms.setPopupSetting(false, "ASP");  // Popup에 대한 설정

        // 5.0이상부터 pms sdk에 내장된 pop-up이 나타남.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isPopupNoti = false;
        }
        pms.setPopupNoti(isPopupNoti); // push 알림시 팝업창을 뛰울것인지

        // 저장된 아이디가 없을 경우 DeviceId로 PMS로그인, 있을 경우 저정된 Id로 로그인
        if (TextUtils.isEmpty(pmsLoginID)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pmsLoginID = checkAppPermissionReadPhone();
            }else{
                pmsLoginID = Library.singleInstance().getDeviceId(getApplicationContext());
            }
        }

        Log.e("@@pmsLoginID@@", "pmsLoginID:" + pmsLoginID);
        if(!TextUtils.isEmpty(pmsLoginID)) {
            try {
                pms.setCustId(pmsLoginID);   // pms 로그인 ID
                pms.setRingMode(Control.singleInstance().getApppre_Boolean(Control.pushStyle_sound));      // push 알림시 알림소리가 울릴것인지
                pms.setVibeMode(Control.singleInstance().getApppre_Boolean(Control.pushStyle_vibrate));    // push 알림시 진동이 울릴것인지

                PmsControl.singleInstance().getCertToPMS(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /***********************************************************************************************
     *@checkVersionApplication : 버전 체크 후 메인으로 넘어감
     **********************************************************************************************/
    public void checkVersionApplication() {
        int appIdx = Integer.parseInt(Control.app_Idx);

        VersionCheck check_AD_Ver = new VersionCheck(MainActivity.this, appIdx, "http://bit.ly/Tsti3a", Application.application.getAppDomain());
        check_AD_Ver.checkVersion(new VersionCheck.VersionChecker() {
            @Override
            public void versionCheckFinished(boolean canProceed) {
                if (!canProceed) {
                    finish();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        APMS.getInstance(this).deleteAll();
        CookieSyncManager.getInstance().startSync();
        checkVersionApplication();
    }


    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_gotoHome:
                if (webview != null) {
                    webview.loadUrl(Application.application.getLptvDomain() + Control.changeTV_Main);
                }
                break;

            case R.id.bottom_back_Page:
                if (webview != null) {
                    webview.goBack();
                }
                break;

            case R.id.bottom_next_Page:
                if (webview != null) {
                    webview.goForward();
                }
                break;

            case R.id.bottom_reset_Page:
                if (webview != null) {
                    webview.reload();
                }
                break;

            case R.id.bottom_page_Share:
//                String shareUrl = webview.getUrl();
//                if (shareUrl.toUpperCase().contains("/M/EBROADCAST/CONTENTVIEW.ASPX")            // 영상(낱개 영상)
//                        || shareUrl.toUpperCase().contains("/M/EBROADCAST/PROGRAMLIST.ASPX")     // 영상(프로그램 리스트)
//                        || shareUrl.toUpperCase().contains("/M/EBROADCAST/SUBMAINLIST.ASPX")     // 영상 서브메인(프로그램 리스트)
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/HEALINGSTORYLIST.ASPX") // 커뮤니티 > 힐링스토리 리스트
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/HEALINGSTORYVIEW.ASPX") // 커뮤니티 > 힐링스토리 > 상세
//                        || shareUrl.toUpperCase().contains("/M/NEWS/NEWSLIST.ASPX")              // 커뮤니티 > 뉴스 리스트
//                        || shareUrl.toUpperCase().contains("/M/NEWS/NEWSVIEW.ASPX")              // 커뮤니티 > 뉴스 > 상세
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/KNOWHOWLIST1.ASPX")     // 커뮤니티 > 힐링타임
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/MYITEMLIST.ASPX")       // 커뮤니티 > 힐링용품 리스트
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/MYITEMVIEW.ASPX")       // 커뮤니티 > 힐링용춤 > 상세
//                        || shareUrl.toUpperCase().contains("/M/ONAIR/ONAIRVIEW.ASPX")            // 온에어 > 댓글
//                        || shareUrl.toUpperCase().contains("/M/ONAIR/ONAIRVIEW02.ASPX")          // 온에어 > 편성표
//                        || shareUrl.toUpperCase().contains("/M/ONAIR/ONAIRVIEW03.ASPX")          // 온에어 > 추천
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/COMMUNITYPDLIST.ASPX")  // 나는 PD다 리스트
//                        || shareUrl.toUpperCase().contains("/M/COMMUNITY/COMMUNITYPDVIEW.ASPX")  // 나는 PD다 뷰
//                        || shareUrl.toUpperCase().contains("M/DAHN/DAHNWORLD_MEMMAIN.ASPX")      // 단월드 회원 전용
//                        || shareUrl.toUpperCase().contains("M/CMS/SELECTCMS.ASPX")      // 후원 회원 전용
//                        || shareUrl.toUpperCase().contains("M/MEMBER/MEMPAYMENT.ASPX")) {   //프리패스 구매
//                    webview.loadUrl("javascript:window.HTMLOUT.getHTMLData(document.getElementById('appHdnTitle').value, document.getElementById('appHdnDesc').value, document.getElementById('appHdnThumb').value, document.getElementById('appHdnWidth').value, document.getElementById('appHdnHeight').value, document.getElementById('appHdnUrl').value);");
//                } else {
//                    String shreTitle ="체인지TV : Change your Life";
//                    String shareMsg = "체인지TV는 건강하고 행복한 변화, 스스로 힐링할 수 있게 하는 힐링 전문 인터넷 방송입니다.";
//                    String shareThumb = "http://www.changetv.kr/images/Main/head_title_img.jpg";
//                    String linkUrl = "http://www.changetv.kr/M/DEFAULT.ASPX";
//                    SocialShareControl.ShareInstance().shareContentInfo(MainActivity.this, shreTitle, shareMsg, shareThumb, 800, 800, linkUrl, linkUrl, "웹에서 보기", false);
//                }
                webview.loadUrl("javascript:window.HTMLOUT.getHTMLData(appShareData.appHdnTitle, appShareData.appHdnDesc, appShareData.appHdnThumb, appShareData.appHdnWidth, appShareData.appHdnHeight, appShareData.appHdnUrl);");
                break;

            case R.id.bottom_app_settings:
                Intent bottom_app_settings_intent = new Intent(MainActivity.this, Settings.class);
                startActivityForResult(bottom_app_settings_intent, 0);
                break;

            case R.id.refreshBtn:
                isRetry = true;
                isError = false;
                progress.setVisibility(View.VISIBLE);
                webview.loadUrl(failUrl);

                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else if (webview.copyBackForwardList().getCurrentIndex() == 1 || isError) {   // webview history가 1일 경우와 에러페이지 경우
                    Library.singleInstance().showAlert(mContext, "앱을 종료하시겠습니까?",
                            "확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }, "취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                } else {
                    webview.goBack();
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {           // 앱 설정
            switch (resultCode) {
                case 1:
                    webview.loadUrl(Application.application.getLptvDomain() + Control.changeTV_logout);
                    break;
                case 2:

                    if(webview.getUrl().toUpperCase().contains(Control.changeTV_login.toUpperCase())){
                        settingsLogin = Uri.parse(webview.getUrl()).getQueryParameter("url");
                    }else{
                        settingsLogin = webview.getUrl();
                    }

                    webview.loadUrl(Application.application.getLptvDomain() + Control.changeTV_login + webview.getUrl());
                    break;
                case 3:
                    webview.loadUrl(Application.application.getLptvDomain() + Control.setting_Myinfo);
                    break;
            }
        }  else if (requestCode == REQUEST_SELECT_FILE) {      // 파일 업로드
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (uploadMessage == null) {
                    return;
                }

                Uri[] result = new Uri[0];

                result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);

                // 파일 업로드 버튼을 누르고 업로드하지 않고 취소 했을 경우.
                File file = new File(mCapturedImageURI.getPath());
                if (data == null && !file.exists()) {
                    result = null;
                }

                uploadMessage.onReceiveValue(result);
                uploadMessage = null;

            } else if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage)
                    return;
                // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
                // Use RESULT_OK only if you're implementing WebView inside an Activity
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            } else {
                Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
            }
            captureIntent = null;
        } else {
            if (isFbCallbackManager) {      // 페이스북
                callbackManager.onActivityResult(requestCode, resultCode, data);
                isFbCallbackManager = false;
            }
        }
    }

    /* 카카오 */
    private void redirectSignupActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* 카카오 */
    private class Sessioncallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionClosed(KakaoException exception) {
            if (exception != null) {
                Logger.getInstance().e(exception);
            }
        }
    }

    /* 네이버 로그인 시 사용자 정보 변환  */
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    /* 네이버 사용자 정보 xml -> String 으로 파싱 */
    public void xmlParsing(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("response");

            String naver_id = "";
            String naver_nickname = "";
            String naver_email = "";
            String naver_gender = "";

            for (int i = 0; i < nodes.getLength(); i++) {
                Element t_element = (Element) nodes.item(i);

                NodeList id = t_element.getElementsByTagName("id");
                Element line = (Element) id.item(0);
                naver_id = getCharacterDataFromElement(line);

                NodeList id_name = t_element.getElementsByTagName("nickname");
                Element line_name = (Element) id_name.item(0);
                naver_nickname = getCharacterDataFromElement(line_name);

                NodeList id_email = t_element.getElementsByTagName("email");
                Element line_email = (Element) id_email.item(0);
                naver_email = getCharacterDataFromElement(line_email);

                NodeList id_gender = t_element.getElementsByTagName("gender");
                Element line_gender = (Element) id_gender.item(0);
                naver_gender = getCharacterDataFromElement(line_gender);
            }

            String domain = Application.application.getLptvDomain();
            String naverLogin_success = domain + "/m/login/SocialIdChk.aspx?ref=app&SocialTypeVal=nv&myName=" + naver_nickname + "&myEmail=" + naver_email + "&myId=" + naver_id + "&MyGender=" + naver_gender;

            webview.loadUrl(naverLogin_success);

        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("Exception", "e-> " + e.toString());
        }
    }

    /* 네이버 로그인 핸들러 */
    private OAuthLoginHandler naver_mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = new OAuthLoginPreferenceManager(mContext).getAccessToken();
                try {
                    HttpClient client = new DefaultHttpClient();
                    String getURL = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
                    HttpGet get = new HttpGet(getURL);
                    get.setHeader("Authorization", "Bearer " + accessToken);
                    HttpResponse responseGet = client.execute(get);
                    HttpEntity resEntityGet = responseGet.getEntity();

                    if (resEntityGet != null) {
                        //do something with the response
                        String naver_xml = EntityUtils.toString(resEntityGet);

                        xmlParsing(naver_xml);
                    }
                } catch (Exception e) {
                    AppLog.e("naver_mOAuthLoginHandler-Exception", "-> " + e.toString());
                    e.printStackTrace();
                }
            } else {
                AppLog.e("Naver_Login_Fail", "Naver_Login_Fail");
            }
        }
    };

    /* 네이버 로그인에 필요한 데이터 */
    private void initData() {
        naver_mOAuthLoginInstance = OAuthLogin.getInstance();
        naver_mOAuthLoginInstance.init(mContext, Control.Naver_OAUTH_CLIENT_ID, Control.Naver_OAUTH_CLIENT_SECRET, Control.Naver_OAUTH_CLIENT_NAME);
    }


    /***********************************************************************************************
     *@checkAppPermissionReadPhone : 사용자 Device에 대한 정보를 가져오기 위한 권한
     **********************************************************************************************/
    private String checkAppPermissionReadPhone(){
        String deviceId = "";
        //권한이 없는 경우
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            //사용자 권한에 대해 재 요청일 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("사용자 정보 권한")
                        .setMessage("사용자 정보로 로그인 및 기타 알림을 설정하기위해서 필요한 권한입니다.")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 임의로 취소 시킨 경우 권한 재요청
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                            }
                        })
                        .setIcon(R.mipmap.app_icon)
                        .show();
            }
            //권한이 아직 부여되지 않아 직접 권한 요청을 하도록 한다.
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
        //권한을 얻은경우
        else{
            deviceId = Library.singleInstance().getDeviceId(getApplicationContext());
        }

        return  deviceId;
    }


    /***********************************************************************************************
     *@checkAppPermissionCamera : 카메라를 사용하기 위한 권한
     **********************************************************************************************/
    private void checkAppPermissionCamera(){
        //사용자 권한에 대해 재 요청일 경우
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("카메라 사용 권한")
                    .setMessage("카메라 및 사진 등을 사용하기 위하여 필요한 권한입니다.")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 임의로 취소 시킨 경우 권한 재요청
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA_STATE);
                        }
                    })
                    .setIcon(R.mipmap.app_icon)
                    .show();
        }
        //권한이 아직 부여되지 않아 직접 권한 요청을 하도록 한다.
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA_STATE);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e("@@onRequestPer@@", "requestCode:" + requestCode);

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setPMSSetting();
                }else{
                    finish();
                }
            return;

            case MY_PERMISSIONS_REQUEST_CAMERA_STATE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    webview.loadUrl("javascript:reloadFrame();");
                }else{
                    webview.reload();
                }
            return;
        }
    }


    /***********************************************************************************************
     *@onConfigurationChanged : 화면 회전시 WebView Refreshf
     **********************************************************************************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}