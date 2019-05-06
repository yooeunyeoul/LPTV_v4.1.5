package com.lptv_12110101.library;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lptv_12110101.application.Application;

import java.util.HashMap;
import java.util.Map;

public class VersionCheck {
    private Context context;
    private String versionName, downUrl, appDomain;
    private int app_Idx;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor prefEditor = null;
    private VersionChecker callback;

    public interface VersionChecker {
        void versionCheckFinished(boolean canProceed);
    }

    public VersionCheck(Context context, int app_Idx, String downUrl, String appDomain) {
        this.context = context;
        this.app_Idx = app_Idx;
        this.downUrl = downUrl;
        this.appDomain = appDomain;

        pref = context.getSharedPreferences(context.getPackageName(), 0);
        prefEditor = pref.edit();
    }

    public void checkVersion(VersionChecker _callback) {
        this.callback = _callback;

        versionName = Application.application.getVersion();

        String url = appDomain + "/Inform/VerCheck.aspx";

        StringRequest checkVersion_client = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int result = Integer.parseInt(response);
                boolean checkVerRefused = pref.getBoolean("checkVerRefused" + versionName, false);

                AppLog.e("*******_checkVersion_onResponse","result-> "+result);

                if (result == 0) {
                    if (!checkVerRefused) {
                        Library.singleInstance().showAlert(context,
                                "새로운 버전이 준비되었습니다.\n업그레이드하시겠습니까?\n(3G 또는 LTE망 사용 시 데이터 요금이 발생됩니다.)",
                                "업그레이드\n하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downUrl));
                                        context.startActivity(intent);
                                        callback.versionCheckFinished(false);
                                    }
                                }, "다시\n보지않기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        prefEditor.putBoolean("checkVerRefused" + versionName, true);
                                        prefEditor.commit();

                                        callback.versionCheckFinished(true);
                                    }
                                }, "나중에\n하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        callback.versionCheckFinished(true);
                                    }
                                });
                    } else {
                        callback.versionCheckFinished(true);
                    }
                } else if (result == -1) {
                    Library.singleInstance().showAlert(context, "새로운 버전이 준비되었습니다.\n업그레이드하시겠습니까?\n(3G 또는 LTE망 사용 시 데이터 요금이 발생됩니다.)",
                            "업그레이드\n하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downUrl));
                                    context.startActivity(intent);
                                    callback.versionCheckFinished(false);
                                }
                            }, "나중에\n하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    callback.versionCheckFinished(false);
                                }
                            });
                } else if (result == 1) {
                    callback.versionCheckFinished(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError Error) {
                AppLog.e("checkVersion_onErrorResponse", "Error-> " + Error.toString());
                callback.versionCheckFinished(true);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();

                param.put("ver", versionName);
                param.put("os", "android");
                param.put("app_Idx", String.valueOf(app_Idx));

                return param;
            }
        };
        Application.application.addToRequestQueue(checkVersion_client);
    }
}