package com.lptv_12110101.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apms.sdk.bean.PushMsg;
import com.lptv_12110101.Control;
import com.lptv_12110101.R;
import com.lptv_12110101.library.AppLog;


public class PushPopupActivity extends AppCompatActivity {
    private Toast popupToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.e("PushPopupActivity", "PushPopupActivity");

        Intent intent = getIntent();

        PushMsg pushMsg = new PushMsg(intent.getExtras());

        String title = pushMsg.notiTitle;
        String msg = pushMsg.notiMsg;

        if (Control.singleInstance().getApppre_Boolean(Control.pushStyle_popup)) {
            showPopupToast(title, msg);
        } else{
            finish();
        }
    }


    private void showPopupToast(String _title, String _msg) {
        View layout = View.inflate(getApplicationContext(), R.layout.pushpopup, null);

        ((TextView) layout.findViewById(R.id.pms_txt_title)).setText(_title);
        ((TextView) layout.findViewById(R.id.pms_txt_msg)).setText(_msg);

        if (popupToast == null) {
            popupToast = new Toast(getApplicationContext());
        }
        popupToast.setGravity(Gravity.TOP, 0, 0);
        popupToast.setDuration(Toast.LENGTH_SHORT);
        popupToast.setView(layout);
        popupToast.show();
        finish();
    }
}