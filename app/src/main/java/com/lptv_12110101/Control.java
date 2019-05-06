package com.lptv_12110101;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AES256Cipher;
import com.lptv_12110101.library.Library;
import com.lptv_12110101.settings.Setting_Login_Interface;

import java.util.ArrayList;
import java.util.List;

public class Control {
    private static Control control;
    public static String app_Idx = "8";
    public static String fbApplicationId ="649612225150901";
    public static String changeTV_firstPage = "/M/AppConnect.aspx?url=";                                                            // 체인지TV 메인
    public static String changeTV_Main = "/M/Default.aspx";                                                                         // 체인지TV 메인
    public static String changeTV_LeftMenu = "/m/MyPage/LeftMyMenu.aspx";                                                           // Left 메뉴
    public static String search_url = "/M/TotalSearch/TotalList.aspx?Info=Total&q=";                                                // 검색 url
    public static String changeTV_login = "/m/Login/?url=";                                                                         // 체인지TV 로그인
    public static String changeTV_logout = "/M/Login/Logout.aspx";                                                                  // 체인지TV 로그아웃
    public static Uri youtube_uri = Uri.parse("https://www.youtube.com/user/lifeparticlekr");                                       // Youtube
    public static Uri kakaotalk_uri = Uri.parse("http://plus.kakao.com/home/gjrcbila");                                             // 카카오톡
    public static Uri changetv_series_app_uri = Uri.parse("https://play.google.com/store/search?q=체인지TV건강명상시리즈");            // 체인지TV 건강명상 시리즈
    public static String jitalk_PackageNm = "com.ectk_14051501";                                                                    // 지구시민톡 패키지 이름
    public static String kook_PackageNm = "com.kook_12030101";                                                                      // 국학원 패키지 이름
    public static String brainMedia_PackagNm = "com.bwme_12081701";                                                                 // 브레인미디어 패키지 이름
    public static String app_Alarmsys ="/M/App/App_Alarmsys.aspx";                                                                  // 앱설정->알림수신설정
    public static String setting_Myinfo = "/M/MyPage/MyMemInfo.aspx";                                                               // 내 계정 정보
    public static String user_id = "user_id";                                                                                       // 아이디 키 값
    public static String user_pwd= "user_pwd";                                                                                      // 비밀번호 키 값
    public static String urlbeforeleftmenu ="urlbeforeleftmenu";
    public static String save_list ="save_list";
    public static String pushStyle_sound = "pushStyle_sound";                                                                       // 소리 키 값
    public static String pushStyle_vibrate = "pushStyle_vibrate";                                                                   // 진동 키 값
    public static String pushStyle_popup = "pushStyle_popup";                                                                       // 팝업 키 값
    public static String healingItem = "/M/COMMUNITY/MYITEMWRITE.ASPX";
    public static String healingTime = "/M/COMMUNITY/KNOWHOWREPLYWRITE.ASPX";
    public static String iamPd = "/M/COMMUNITY/COMMUNITYPDWRITE.ASPX";
    public static String myInfoPage = "/M/MYPAGE/MYMEMINFO.ASPX";                                                                   // 회원정보 수정 페이지
    public static String facebookCh = "https://www.facebook.com/건강하녀-979516982095223/";                                          // 페이스북 채널 주소
    public static String kakaostroyCh = "https://story.kakao.com/ch/changetv";                                                      // 카카오 스토리 채널 주소
    public static String healingAudio = "/HealingMusic/HMusic_MusicList.aspx?ProgramIdx=41&MusicType=Audio";                        // 서비스 안내>콘텐츠 안내 힐링오디오 버튼 url
    public static String shareEnergy = "/Community/EsendList.aspx?menu=Esend";                                                      // 서비스 안내>콘텐츠 안내 에너지나누기 버튼 url
    public static String Naver_OAUTH_CLIENT_ID = "EzW_k7nS2yKcDH_aR5L4";                                                            // 소셜 로그인 네이버 데이터
    public static String Naver_OAUTH_CLIENT_SECRET = "91wCf3M8aT";                                                                  // 소셜 로그인 네이버 데이터
    public static String Naver_OAUTH_CLIENT_NAME = "ChangeTV";                                                                      // 소셜 로그인 네이버 데이터
    public static String onAirPage = "/m/onAir/onAirView.aspx?Info=OnAir";                                                          // 온에어 페이지

    public static Control singleInstance() {
        if (control == null) {
            control = new Control();
        }
        return control;
    }

    public void setInterface(Setting_Login_Interface obj) {
        ArrayList<Setting_Login_Interface> arrInterfaces = new ArrayList<>();
        arrInterfaces.add(obj);
    }

    /******** Boolean 값을 저장  ********************************************************************/
    public void setAppPre_Boolean(String key, Boolean value) {
        Application.application.setAppPreferences(key, value);
    }

    /******** 저장된 Boolean 값을 호출 ***************************************************************/
    public Boolean getApppre_Boolean(String key) {
        return Application.application.getAppPreferencesBoolean(key);
    }

    /*********** user-id 저장 및 암호화 **************************************************************/
    public void setAppPre_Id(Context context, String _value) {
        AES256Cipher aesId = new AES256Cipher();

        try {
            String value = aesId.encrypt(_value, getUserEncryptKey(context));
            Application.application.setAppPreferences(user_id, value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********** user-id 호출 및 복호화 ***************************************************************/
    public String getAppPre_Id(Context context) {
        AES256Cipher aesEncId = new AES256Cipher();
        String simId, encrySimId;
        try {
            encrySimId = Application.application.getAppPreferencesString(user_id);
            simId = aesEncId.decrypt(encrySimId, getUserEncryptKey(context));
        } catch (Exception e) {
            simId = "";
            e.printStackTrace();
        }
        return simId;
    }

    /********** user-password 호출 및 암호화 *********************************************************/
    public void setAppPre_pwd(Context context, String _value) {
        AES256Cipher aesPwd = new AES256Cipher();
        try {
            String value = aesPwd.encrypt(_value, getUserEncryptKey(context));
            Application.application.setAppPreferences(user_pwd, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********** user-password 호출 및 복호화 *********************************************************/
    public String getAppPre_pwd(Context context) {
        AES256Cipher aesEncPwd = new AES256Cipher();
        String simPwd, encrySimPwd;
        try {
            encrySimPwd = Application.application.getAppPreferencesString(user_pwd);
            simPwd = aesEncPwd.decrypt(encrySimPwd, getUserEncryptKey(context));
        } catch (Exception e) {
            simPwd = "";
            e.printStackTrace();
        }
        return simPwd;
    }

    /********** 로그인 타입 저장 *********************************************************************/
    public void setAppPre_loginType(String _value){
        Application.application.setAppPreferences("loginFlag", _value);
    }

    /********** 저장된 로그인 타입 호출 ***************************************************************/
    public String getAppPre_loginType(){
        return Application.application.getAppPreferencesString("loginFlag");
    }

    /********** url 저장 ****************************************************************************/
    public void setAppPre_url(String value) {
        Application.application.setAppPreferences(urlbeforeleftmenu, value);
    }

    /********** 저장된 url 호출 **********************************************************************/
    public String getAppPre_url(Context context) {
        String returnValue = null;
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("com.lptv_12110101", 0);
        returnValue = pref.getString(urlbeforeleftmenu, Application.application.getLptvDomain() + Control.changeTV_Main);
        return returnValue;
    }

    /********* 현재 클래스에서 해당 클래스로 이동 ******************************************************/
    public void intentToActivity(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public void intentToActivity_putStringData(Context context, Class cls, String key,String value){
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    /********** 해당 url을 받아 웹 브라우저로 열어줌 ***************************************************/
    public void intentToActionView(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /********* 페이스북, 카카오스토리 앱이 있는경우 해당 채널을 열어줌, 앱이 없을 경우 웹 브라우저로 열어준다.*/
    public void intentToActionView(Context context, String packagName, String pageUrl, String url) {
        Intent intent;
        Uri mUri = Uri.parse(url);
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packagName, 0);
            if (info.applicationInfo.enabled) {
                mUri = Uri.parse(pageUrl);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        intent = new Intent(Intent.ACTION_VIEW, mUri);
        context.startActivity(intent);
    }

    /*********** 현재 앱이 있는지 없는지 체크 **********************************************************/
    public Boolean getPackageList(Context context, String packageNm) {
        boolean isExist = false;

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = packageManager.queryIntentActivities(mainIntent, 0);
        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.packageName.startsWith(packageNm)) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            isExist = false;
            e.printStackTrace();
        }
        return isExist;
    }

    /********** 해당 앱이 설치되어 있으면 앱으로 이동, 아니면 마켓으로 이동한다. **************************/
    public void gotoAppOrMarket(Context context, String packageNm) {
        if (getPackageList(context, packageNm)) {
            gotoApp(context, packageNm);
        } else {
            gotoMarket(context, packageNm);
        }
    }

    /********** 해당 앱이 있는 마켓으로 이동 **********************************************************/
    public void gotoMarket(Context context, String packageNm) {
        String url = "market://details?id=" + packageNm;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    /********** 해당 앱으로 이동 *********************************************************************/
    public void gotoApp(Context context, String packageNm) {
        Intent intent_gotoApp = context.getPackageManager().getLaunchIntentForPackage(packageNm);
        intent_gotoApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent_gotoApp);
    }

    /****** 암호화 설정 키 ***************************************************************************/
    private String getUserEncryptKey(Context mContext) {
        StringBuilder sb = new StringBuilder();

        sb.append(Library.singleInstance().getDeviceId(mContext).replace("-", ""));

        if (sb.length() > 32) {
            sb = sb.delete(32, sb.length());
        } else if (sb.length() < 32) {
            String tnp = "qftnsdhfjkwehrjshsjehfsejfhsje";
            sb = sb.append(tnp.substring(0, (32 - sb.length())));
        }
        return sb.toString();
    }
}