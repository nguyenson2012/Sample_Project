package com.example.asus.catchpictureproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.Utils.SoundEffect;
import com.example.asus.catchpictureproject.model.StaticVariable;

/**
 * Created by Asus on 6/19/2016.
 */
public class AboutActivity extends Activity implements View.OnClickListener{
    Button btBack;
    MediaPlayer mediaPlayer;
    Boolean isSoundOn;
    TextView tvGmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        getSettingSound();
        setupView();
        registerEvent();
    }

    private void getSettingSound() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        isSoundOn=pre.getBoolean(StaticVariable.SOUND_SETTING,true);
    }

    private void registerEvent() {
        btBack.setOnClickListener(this);
        tvGmail.setOnClickListener(this);
    }

    private void setupView() {
        btBack=(Button)findViewById(R.id.button_ic_back_about);
        tvGmail=(TextView)findViewById(R.id.tv_link_email);
        String mystring=getResources().getString(R.string.link_gmail);
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        tvGmail.setText(content);
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
            case R.id.button_ic_back_about:
                finish();
                overridePendingTransition(0, R.anim.zoom_exit);
                break;
            case R.id.tv_link_email:
                openFeedback(AboutActivity.this);
                break;

        }
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
}
