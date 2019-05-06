package com.lptv_12110101.library;


import com.lptv_12110101.MainActivity;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;


public class AppLog {

    public static void e(String tag, String msg){
        if(MainActivity.isAppLog && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)){
            Log.e(tag, msg);
        }
    }

    public static void e(Activity activity, String msg){
        if(MainActivity.isAppLog && (activity != null) && !TextUtils.isEmpty(msg)){
            Log.e(activity.getLocalClassName(), msg);
        }
    }
}
