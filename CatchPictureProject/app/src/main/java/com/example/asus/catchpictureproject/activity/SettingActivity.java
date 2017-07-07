package com.example.asus.catchpictureproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.Utils.SoundEffect;
import com.example.asus.catchpictureproject.model.StaticVariable;

import java.util.Calendar;

/**
 * Created by Asus on 6/19/2016.
 */
public class SettingActivity extends Activity implements View.OnClickListener{
    Button btSettingBack;
    Button btGoAbout,btGoRateApp,btGoSendFeedback;
    Switch switchSound;
    Boolean isSoundOn;
    RelativeLayout layoutAboutApp,layoutRateApp,layoutSendFeedback;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        setupView();
        registerEvent();
        getSettingSound();
    }

    private void getSettingSound() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        isSoundOn=pre.getBoolean(StaticVariable.SOUND_SETTING,true);
        if(isSoundOn)
            switchSound.setChecked(true);
    }

    private void registerEvent() {
        btSettingBack.setOnClickListener(this);
        btGoAbout.setOnClickListener(this);
        btGoRateApp.setOnClickListener(this);
        btGoSendFeedback.setOnClickListener(this);
        switchSound.setOnClickListener(this);
        layoutAboutApp.setOnClickListener(this);
        layoutSendFeedback.setOnClickListener(this);
        layoutRateApp.setOnClickListener(this);
    }

    private void setupView() {
        btSettingBack=(Button)findViewById(R.id.button_ic_back_setting);
        btGoAbout=(Button)findViewById(R.id.button_go_about);
        btGoRateApp=(Button)findViewById(R.id.button_go_rate_app);
        btGoSendFeedback=(Button)findViewById(R.id.button_go_send_feedback);
        switchSound=(Switch)findViewById(R.id.switch_sound);
        layoutAboutApp=(RelativeLayout)findViewById(R.id.layout_about_app);
        layoutRateApp=(RelativeLayout)findViewById(R.id.layout_rate_the_app);
        layoutSendFeedback=(RelativeLayout)findViewById(R.id.layout_send_feedback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.button_ic_back_setting:
                finish();
                overridePendingTransition(0, R.anim.zoom_exit);
                break;
            case R.id.button_go_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                break;
            case R.id.switch_sound:
                checkSoundOn();
                break;
            case R.id.layout_about_app:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                break;
            case R.id.button_go_rate_app:
                rateTheApp();
                break;
            case R.id.layout_rate_the_app:
                rateTheApp();
                break;
            case R.id.button_go_send_feedback:
                openFeedback(SettingActivity.this);
                break;
            case R.id.layout_send_feedback:
                openFeedback(SettingActivity.this);
                break;
        }

    }

    private void rateTheApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.link_google_play_for_rate))));
    }

    private static void openFeedback(Context paramContext) {
        Intent localIntent = new Intent(Intent.ACTION_SEND);
        localIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"edu_intelligent@gmail.com"});
        localIntent.putExtra(Intent.EXTRA_CC, "");
        String str = null;
        try {
            str = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
            localIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Your Android App");
            localIntent.putExtra(Intent.EXTRA_TEXT, "\n\n----------------------------------\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + str + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER);
            localIntent.setType("message/rfc822");
            paramContext.startActivity(Intent.createChooser(localIntent, "Choose an Email client :"));
        } catch (Exception e) {
            Log.d("OpenFeedback", e.getMessage());
        }
    }

    private void checkSoundOn() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        if(switchSound.isChecked()){
            isSoundOn=true;
            editor.putBoolean(StaticVariable.SOUND_SETTING,true);
        }else {
            isSoundOn=false;
            editor.putBoolean(StaticVariable.SOUND_SETTING,false);
        }
        editor.commit();
    }
}
