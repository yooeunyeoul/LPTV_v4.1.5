package com.lptv_12110101.settings;

import com.apms.sdk.APMS;
import com.lptv_12110101.Control;
import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


public class Setting_PushStyle extends AppCompatActivity implements View.OnClickListener {

    private ToggleButton pushStyle_sound_Btn, pushStyle_vibrate_Btn, pushStyle_popup_Btn;
    private Vibrator mVibrator;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_pushstyle);
        Application.actList.add(this);

        /************** UI 구성 ***************************************************************/
        TextView setting_titlebar_title = (TextView) findViewById(R.id.setting_titlebar_title);
        setting_titlebar_title.setText("푸시알림 스타일 설정");

        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);

        pushStyle_sound_Btn = (ToggleButton) findViewById(R.id.pushStyle_sound_Btn);
        pushStyle_vibrate_Btn = (ToggleButton) findViewById(R.id.pushStyle_vibrate_Btn);
        pushStyle_popup_Btn = (ToggleButton) findViewById(R.id.pushStyle_popup_Btn);
        /************************************************************************************/

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // getApppre_Boolean default 값이 true이다.
        Boolean isSound = Control.singleInstance().getApppre_Boolean(Control.pushStyle_sound);
        Boolean isVibrate = Control.singleInstance().getApppre_Boolean(Control.pushStyle_vibrate);
        Boolean isPopup = Control.singleInstance().getApppre_Boolean(Control.pushStyle_popup);

        pushStyle_sound_Btn.setChecked(isSound);
        pushStyle_vibrate_Btn.setChecked(isVibrate);
        pushStyle_popup_Btn.setChecked(isPopup);

        /*********** 버튼 클릭 리스너 *********************************************************/
        setting_titlebar_close.setOnClickListener(this);
        pushStyle_sound_Btn.setOnClickListener(this);
        pushStyle_vibrate_Btn.setOnClickListener(this);
        pushStyle_popup_Btn.setOnClickListener(this);
        /************************************************************************************/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_titlebar_close:
                finish();
                break;

            case R.id.pushStyle_sound_Btn:
                APMS.getInstance(this).setRingMode(pushStyle_sound_Btn.isChecked());
                Control.singleInstance().setAppPre_Boolean(Control.pushStyle_sound, pushStyle_sound_Btn.isChecked());
                break;

            case R.id.pushStyle_vibrate_Btn:
                if(pushStyle_vibrate_Btn.isChecked()){
                    mVibrator.vibrate(500);
                }else{
                    mVibrator.cancel();
                }

                APMS.getInstance(this).setVibeMode(pushStyle_vibrate_Btn.isChecked());
                Control.singleInstance().setAppPre_Boolean(Control.pushStyle_vibrate, pushStyle_vibrate_Btn.isChecked());
                break;

            case R.id.pushStyle_popup_Btn:
//                APMSUtil.setHeadsUp(getApplicationContext(),pushStyle_popup_Btn.isChecked());
                Control.singleInstance().setAppPre_Boolean(Control.pushStyle_popup, pushStyle_popup_Btn.isChecked());
                break;
        }
    }
}