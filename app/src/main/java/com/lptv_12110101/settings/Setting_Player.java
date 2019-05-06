package com.lptv_12110101.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lptv_12110101.R;
import com.lptv_12110101.application.Application;

public class Setting_Player extends AppCompatActivity implements View.OnClickListener {

    private ToggleButton entrailsPlayerBtn, externalPlayerBtn;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_player);
        Application.actList.add(this);

        TextView setting_titlebar_title = (TextView) findViewById(R.id.setting_titlebar_title);
        setting_titlebar_title.setText("플레이어 설정");
        Button setting_titlebar_close = (Button) findViewById(R.id.setting_titlebar_close);
        setting_titlebar_close.setOnClickListener(this);


        entrailsPlayerBtn = (ToggleButton) findViewById(R.id.entrailsPlayerBtn);
        entrailsPlayerBtn.setOnClickListener(this);
        externalPlayerBtn = (ToggleButton) findViewById(R.id.externalPlayerBtn);
        externalPlayerBtn.setOnClickListener(this);

        if (Application.application.getAppPreferencesBoolean("playerType")) {
            entrailsPlayerBtn.setChecked(true);
            externalPlayerBtn.setChecked(false);
        } else {
            entrailsPlayerBtn.setChecked(false);
            externalPlayerBtn.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_titlebar_close:
                finish();
                break;

            case R.id.entrailsPlayerBtn:
                entrailsPlayerBtn.setChecked(true);
                externalPlayerBtn.setChecked(false);
                Application.application.setAppPreferences("playerType", true);
                break;

            case R.id.externalPlayerBtn:
                entrailsPlayerBtn.setChecked(false);
                externalPlayerBtn.setChecked(true);
                Application.application.setAppPreferences("playerType", false);
                break;
        }
    }
}
