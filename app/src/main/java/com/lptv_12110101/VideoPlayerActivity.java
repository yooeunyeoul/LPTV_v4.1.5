package com.lptv_12110101;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.devbrackets.android.exomedia.EMVideoView;
import com.lptv_12110101.application.Application;


public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    private EMVideoView emVideoView;
    private int playTime;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exomedia);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 화면 꺼짐 방지
        Application.actList.add(this);

        playTime = 0;

        Application.application.setAppPreferences("playTime", playTime);

        Intent getUrl_Intent = getIntent();

        String playUrl = getUrl_Intent.getStringExtra("playUrl");

        emVideoView = (EMVideoView) findViewById(R.id.video_view);

        emVideoView.setOnPreparedListener(this);
        emVideoView.setVideoURI(Uri.parse(playUrl));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (emVideoView.isPlaying()) {
            emVideoView.pause();
            Application.application.setAppPreferences("playTime", (int)emVideoView.getCurrentPosition());
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playTime = Application.application.getAppPreferencesInteger("playTime");

        if (playTime == 0) {
            emVideoView.start();
        } else {
            emVideoView.seekTo(playTime);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emVideoView.release();
    }
}