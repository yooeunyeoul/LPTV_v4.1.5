package com.lptv_12110101.application;


import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.lptv_12110101.Control;
import com.lptv_12110101.library.AppLog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class Application extends android.app.Application {
    public static Application application;
    public Boolean isTestMode = false;

    public static ArrayList<Activity> actList = new ArrayList<Activity>();

    ///////////////// Volley ////////////////////////
    public static final String TAG = Application.class.getSimpleName();
    private RequestQueue mRequestQueue;
    ///////////////// Volley ////////////////////////


    public Application() {
        application = this;
    }

    public void sendPageViewToGoogleAnalytics(String pageNm) {
        Tracker easyTracker = EasyTracker.getInstance(getApplicationContext());
        easyTracker.set(Fields.SCREEN_NAME, pageNm);
        easyTracker.send(MapBuilder.createAppView().build());
    }

    public void sendEventToGoogleAnalytics(String category, String action, String label, Long value){
        EasyTracker easyTracker = EasyTracker.getInstance(this);
        easyTracker.send(MapBuilder.createEvent(category,  action,  label,  value).build());
    }

    public void deleteActivity() {
        if (actList.size() > 0) {
            for (int i = 0; i < actList.size(); i++) {
                actList.get(i).finish();
            }
            actList.clear();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    public String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAppDomain() {
        if (isTestMode) {
            return "http://192.168.3.26:3208";
        } else {
            return "http://app.brainworld.com";
        }
    }

    public void setIsTestMode(boolean isTestMode) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int flags = packageInfo.applicationInfo.flags;
            AppLog.e("##############################", "##############################");
            AppLog.e("## 체인지TV isTestMode", isTestMode + "");
            AppLog.e("##############################", "##############################");

            if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // development mode
                this.isTestMode = isTestMode;
            } else {
                // release mode
                this.isTestMode = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            this.isTestMode = false;
        }
    }

    public String getLptvDomain() {
        return getLptvDomain(false);
    }

    public String getLptvDomain(Boolean isSSL) {
        if (isTestMode) {
            return "http://192.168.3.26:7700";
        } else {
            return (isSSL ? "https" : "http") + "://www.changeTV.kr";
        }
    }

    public ArrayList<Object> setDeviceInfoUrl(String registration_Id, String deviceId, String model, String ver) {
        ArrayList<Object> arr = new ArrayList<Object>();
        Map<String, String> param = new HashMap<String, String>();

        param.put("gcmYn", "Y");
        param.put("app_Idx", Control.app_Idx);
        param.put("registration_Id", registration_Id);
        param.put("deviceId", deviceId);
        param.put("model", model);
        param.put("ver", ver);

        arr.add(getAppDomain() + "/C2DM/DeviceTokenInfo_INSERT.aspx");
        arr.add(param);

        return arr;
    }

    ///////////////// Volley ////////////////////////
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    ///////////////// Volley ////////////////////////

    // app 쉐어드 프리퍼런스 값 설정(string형태)
    public void setAppPreferences(String key, String value) {
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.apply();
    }

    // app 쉐어드 프리퍼런스 값 설정(boolean형태)
    public void setAppPreferences(String key, Boolean value) {
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.apply();
    }

    // app 쉐어드 프리퍼런스 값 설정(int형태)
    public void setAppPreferences(String key, int value) {
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, value);

        prefEditor.commit();
    }

    // app 쉐어드 프리퍼런스 값 읽어옴(string형태)
    public String getAppPreferencesString(String key) {
        String returnValue = null;
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        returnValue = pref.getString(key, "");
        return returnValue;
    }

    // app 쉐어드 프리퍼런스 값 읽어옴(boolean형태)
    public Boolean getAppPreferencesBoolean(String key) {
        boolean returnValue = false;
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        returnValue = pref.getBoolean(key, true);
        return returnValue;
    }

    // app 쉐어드 프리퍼런스 값 읽어옴(int형태)
    public int getAppPreferencesInteger(String key) {
        int returnValue = 0;
        SharedPreferences pref = null;
        pref = this.getSharedPreferences("com.lptv_12110101", 0);
        returnValue = pref.getInt(key, 0);
        return returnValue;
    }
}
