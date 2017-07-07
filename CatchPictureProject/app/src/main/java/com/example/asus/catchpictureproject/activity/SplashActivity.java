package com.example.asus.catchpictureproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.IntentCompat;
import android.widget.ProgressBar;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.Utils.DBHelper;
import com.example.asus.catchpictureproject.model.StaticVariable;
import com.example.asus.catchpictureproject.model.Word4Pic;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Asus on 6/17/2016.
 */
public class SplashActivity extends Activity {
    ProgressBar progressBar;
    DBHelper database;
    StaticVariable staticVariable;
    //khai báo handler class để xử lý đa tiến trình
    Handler handler;
    //dùng AtomicBoolean để thay thế cho boolean
    AtomicBoolean isrunning=new AtomicBoolean(false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        staticVariable=StaticVariable.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.progressbar_splash);
        if(!checkAlreadyDatabase()){
            setupDatabse();
        }
        handler=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //msg.arg1 là giá trị được trả về trong message
                //của tiến trình con
                progressBar.setProgress(msg.arg1);
                if(msg.arg1==100){
                    Intent intent=new Intent(SplashActivity.this,StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
            }
        };
        doStart();
    }

    public void doStart()
    {
        progressBar.setProgress(0);
        isrunning.set(false);
        //tạo 1 tiến trình CON
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                //vòng lặp chạy 100 lần
                for(int i=1;i<=100 && isrunning.get();i++)
                {
                    //cho tiến trình tạm ngừng 100 mili second
                    SystemClock.sleep(50);
                    //lấy message từ Main thread
                    Message msg=handler.obtainMessage();
                    //gán giá trị vào cho arg1 để gửi về Main thread
                    msg.arg1=i;
                    //gửi lại Message này về cho Main Thread
                    handler.sendMessage(msg);
                }
            }
        });
        isrunning.set(true);
        //kích hoạt tiến trình
        th.start();
    }
    private void setupDatabse() {
        database=new DBHelper(this);
        ArrayList<Word4Pic> listWord=staticVariable.getListQuestion();
        for(Word4Pic word4Pic:listWord){
            database.insertWord4pic(word4Pic);
        }
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putBoolean(StaticVariable.DATABASE, true);
        editor.commit();
    }
    private boolean checkAlreadyDatabase(){
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        boolean alreadyDatabase=pre.getBoolean(StaticVariable.DATABASE, false);
        return alreadyDatabase;
    }
}
