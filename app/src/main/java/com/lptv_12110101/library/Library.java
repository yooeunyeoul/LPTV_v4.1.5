package com.lptv_12110101.library;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

public class Library {
    public static Library library;

    public static Library singleInstance() {
        if (library == null) {
            library = new Library();
        }
        return library;
    }

    public void showAlert(Context context, String ment,
                          String positiveMent,
                          DialogInterface.OnClickListener positiveListener) {
        new AlertDialog
                .Builder(context)
                .setMessage(ment)
                .setPositiveButton(positiveMent, positiveListener)
                .setCancelable(false)
                .show();
    }

    public void showAlert(Context context, String ment,
                          String positiveMent,
                          DialogInterface.OnClickListener positiveListener,
                          String negativeMent,
                          DialogInterface.OnClickListener negativListener) {
        new AlertDialog
                .Builder(context)
                .setMessage(ment)
                .setPositiveButton(positiveMent, positiveListener)
                .setNegativeButton(negativeMent, negativListener)
                .show();
    }

    public void showAlert(Context context, String ment,
                          String positiveMent,
                          DialogInterface.OnClickListener positiveListener,
                          String neutralMent,
                          DialogInterface.OnClickListener neutralListener,
                          String negativeMent,
                          DialogInterface.OnClickListener negativeListener) {
        new AlertDialog
                .Builder(context)
                .setMessage(ment)
                .setPositiveButton(positiveMent, positiveListener)
                .setNeutralButton(neutralMent, neutralListener)
                .setNegativeButton(negativeMent, negativeListener)
                .show();
    }

    public static String encode(String strToEncode) {
        try {
            return URLEncoder.encode(strToEncode, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String decode(String strToDeconde) {
        try {
            return URLDecoder.decode(strToDeconde, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    /*********************************************************************************************
     * @return 0 :현재 네트워크에 연결이 안되었을경우
     * 1 :현재 3G or LTE 연결 되었을경우
     * 2 :현재 Wifi에 연결 되었을경우
     * @getConnectionStat : 현재 네트워크 연결상태를 확인
     *********************************************************************************************/
    public final static int getConnectionStatus(Context context) {
        ConnectivityManager cManager;
        NetworkInfo mobile, wifi;
        int status = 0;

        cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile != null && mobile.isConnected()) {
            status = 1;
        } else if (wifi.isConnected()) {
            status = 2;
        }
        return status;
    }

    //android는 기기종류별로 기기의 특정 고유값을 가져올수 있는 통일된 메소드가 없기 때문에, 각 종류별로 유효한 여러가지 메소드를 조합하여 사용.
    @SuppressLint("MissingPermission")
    public String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();  //IMEI 값
        tmSerial = "" + tm.getSimSerialNumber(); //SIM 카드 시리얼넘버
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public String getDeviceModel() {
        return android.os.Build.MANUFACTURER + " / " + android.os.Build.MODEL;
    }

    public String getDeviceOSVer() {
        return android.os.Build.VERSION.RELEASE;
    }
}