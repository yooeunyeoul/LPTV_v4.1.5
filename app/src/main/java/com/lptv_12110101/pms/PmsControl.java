package com.lptv_12110101.pms;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apms.sdk.APMS;
import com.apms.sdk.api.APIManager;
import com.apms.sdk.api.request.DeviceCert;
import com.apms.sdk.api.request.LoginPms;
import com.apms.sdk.api.request.SetConfig;
import com.lptv_12110101.Control;
import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AppLog;
import com.lptv_12110101.library.Library;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class PmsControl {
    public static PmsControl pmsControl;
    private Boolean canLoginToPMS = false;
    private int PMSRetryCnt = 0, PMSLoginRetryCnt = 0;

    public static PmsControl singleInstance(){
        if(pmsControl == null){
            pmsControl = new PmsControl();
        }
        return pmsControl;
    }

    /*************** PMS - DeviceCert**************************************************************/
    public void getCertToPMS(final Context context) {
        // 인증 실패시 최대 3번까지 재시도 한다.
        if (PMSRetryCnt < 3) {
            new DeviceCert(context).request(null, new APIManager.APICallback() {
                @Override
                public void response(String code, JSONObject json) {
                    if (code.equals("000")) {
                        canLoginToPMS = true;
                        loginToPMS(context);
                    } else {
                        PMSRetryCnt++;
                        getCertToPMS(context);
                    }
                }
            });
        }
    }

    /****************** PMS 로그인*********************************************************************************/
    public void loginToPMS(final Context context) {
        if (PMSRetryCnt < 3) {  // 인증도 아직 되지 않은 경우, 인증이 3번 실패해 버리면 인증이 멈추고, 로그인도 멈추게 한다.
            if (canLoginToPMS) {
                // PMS에 바뀐 아이디로 로그인 정보 기록.
                if (PMSLoginRetryCnt < 3) {   // 로그인 실패시, 역시 3번까지 재시도.
                    new LoginPms(context).request(APMS.getInstance(context).getCustId(), null,
                            new APIManager.APICallback() {
                                @Override
                                public void response(String arg0, JSONObject arg1) {
                                    AppLog.e("@@@PMS_Login_Result@@@", arg0 + " , " + arg1);
                                    if (!arg0.equals("000")) {
                                        PMSLoginRetryCnt++;
                                        loginToPMS(context);
                                    } else {
                                        // 1분간의 에티켓모드 시간을 없애는것은 현재 안드로이드 SDK에서는 지원이 불가능해서(03:00~03:01 에티켓 모드)
                                        new SetConfig(context).request("Y", "Y", "Y", "0300", "0301", new APIManager.APICallback() {
                                            @Override
                                            public void response(String s, JSONObject jsonObject) {
                                                AppLog.e("SetConfig", s + "," + jsonObject.toString());
                                            }
                                        });
                                        sendUserInfoToAppDevServer(context, false);
                                    }
                                }
                            });
                } else {
                    // 로그인이 3회 이상 실패해 버림.
                    AppLog.e("PMS Login Failed", "PMS Login Failed.");
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!canLoginToPMS && PMSRetryCnt < 3) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                AppLog.e("Error Thread", e.toString());
                            }
                        }
                        loginToPMS(context);
                    }
                }).start();
            }
        } else {
            // 로그인을 하기 전에 인증부터 되어야 하는데, 인증작업부터 실패해 버린 경우임.
            AppLog.e("PMS Login Failed", "PMS Login Failed. Cert is the problem.");
        }
    }

    /****************** 서버로 데이터를 보냄*****************************************************************/
    public void sendUserInfoToAppDevServer(final Context context, final Boolean isRetry) {
        String url = Application.application.getAppDomain() + "/PMS/UserInsert.aspx";

        StringRequest client = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("success")) {
                    if (!isRetry) {
                        sendUserInfoToAppDevServer(context,true);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                AppLog.e("sendUserInfoToAppDevServer Fail", arg0 == null ? "Error Unknown" : arg0.toString());
                if (!isRetry) {
                    sendUserInfoToAppDevServer(context,true);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();

                param.put("app_Idx", Control.app_Idx);
                param.put("app_UserId", APMS.getInstance(context).getCustId());
                param.put("UUID", Library.encode(Library.singleInstance().getDeviceId(context)));
                param.put("token", "");
                param.put("os", "android");
                param.put("osVer", Library.encode(Library.singleInstance().getDeviceOSVer()));
                param.put("model", Library.encode(Library.singleInstance().getDeviceModel()));

                return param;
            }
        };
        Application.application.addToRequestQueue(client);
    }
}
