package com.lptv_12110101.push;


import android.content.Context;
import android.content.Intent;

public class PushReceiver extends com.apms.sdk.push.PushReceiver {

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        /************************************************
         * 브레인월드코리아 서버로 정보를 수집하는 곳이지만
         * 딱히 필요가 없어서 주석 처리 됨.
         * **********************************************/

//        if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
//            handleRegistration(context, intent);
//        }
    }

//    private void handleRegistration(Context context, Intent intent) {
//        final String registration_Id = intent.getStringExtra("registration_id");
//
//        Application lptvApp = ((Application) context.getApplicationContext());
//        DeviceInfoController deviceInfo = new DeviceInfoController();
//
//        if (registration_Id != null) {
//            try {
//                String deviceId = URLEncoder.encode(deviceInfo.getDeviceId(context), "UTF-8");
//                String model = URLEncoder.encode(deviceInfo.getDeviceModel(), "UTF-8");
//                String ver = URLEncoder.encode(deviceInfo.getDeviceOSVer(), "UTF-8");
//
//                final ArrayList<Object> list = Application.application.setDeviceInfoUrl(registration_Id, deviceId, model, ver);
//
//                String urlStr = (String) list.get(0);
//
//                StringRequest client = new StringRequest(Request.Method.POST, urlStr, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError e) {
//                        Log.e("handleRegistration_fail", e.toString());
//                    }
//                }) {
//                    @SuppressWarnings("unchecked")
//                    @Override
//                    protected Map<String, String> getParams() {
//                        return (Map<String, String>) list.get(1);
//                    }
//                };
//
//                client.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                lptvApp.addToRequestQueue(client);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }


}
