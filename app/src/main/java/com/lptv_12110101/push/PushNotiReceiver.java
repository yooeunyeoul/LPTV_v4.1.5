package com.lptv_12110101.push;

import com.apms.sdk.APMS;
import com.apms.sdk.bean.PushMsg;
import com.lptv_12110101.MainActivity;
import com.lptv_12110101.application.Application;
import com.lptv_12110101.library.AppLog;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushNotiReceiver extends BroadcastReceiver {
    String getUrl = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        APMS.getInstance(context).deleteAll();

        PushMsg msg = new PushMsg(intent.getExtras());

        try {
            JSONObject jsonObject = new JSONObject(msg.data);
            getUrl = jsonObject.getString("l");
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("PushNotiReceiver_onReceive", "-> " + e.toString());
        }

        AppLog.e("PushNotiReceiver", "getUrl-> " + getUrl);

        Application.application.deleteActivity();

        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("category_url", getUrl);
        context.startActivity(i);
    }
}