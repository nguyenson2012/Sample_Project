package com.example.asus.catchpictureproject.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.Utils.ConnectionDetector;
import com.example.asus.catchpictureproject.Utils.SoundEffect;
import com.example.asus.catchpictureproject.model.StaticVariable;
import com.example.asus.catchpictureproject.model.Word4Pic;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int AD_HEIGHT =50 ;
    private Button btStartGame;
    private Button btSetting;
    private Button btShareApp;
    private Button btBuyCoin;
    private TextView tvNumberCoin;
    private TextView tvCoinAdded;
    Button btRandomCoin,btYesGetCoin;
    ImageView imgCoin,imgStart;
    private AdView adView;
    private DisplayImageOptions opt;
    private ImageLoader imageLoader;
    private ConnectionDetector connectionDetector;
    private int coinAdded;
    private ArrayList<Word4Pic> listQuestion;
    private int currentQuestionPosition;
    //khai báo handler class để xử lý đa tiến trình
    Handler handler;
    //dùng AtomicBoolean để thay thế cho boolean
    AtomicBoolean isrunning=new AtomicBoolean(false);
    private MediaPlayer mediaPlayer;
    DisplayMetrics metrics;
    private int numberGetCoinTime=0;
    private boolean isSoundOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNumberGetCoinPerday();
        setContentView(R.layout.start_layout);
        getSettingSound();
        setupView();
        registerEvent();
        initImageLoader(StartActivity.this);
        setupImageDisplayOptions();
        setCurrentCoin();
        setupImageSize();
    }

    private void getSettingSound() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        isSoundOn=pre.getBoolean(StaticVariable.SOUND_SETTING,true);
    }

    private void registerEvent() {
        btStartGame.setOnClickListener(this);
        btSetting.setOnClickListener(this);
        btShareApp.setOnClickListener(this);
        btBuyCoin.setOnClickListener(this);
    }

    private void setupView() {
        connectionDetector=new ConnectionDetector(StartActivity.this);
        btStartGame=(Button)findViewById(R.id.btPlayNow);
        btSetting=(Button)findViewById(R.id.button_setting);
        btShareApp=(Button)findViewById(R.id.bt_share_app_start);
        tvNumberCoin=(TextView)findViewById(R.id.tvNumberCoinStart);
        btBuyCoin=(Button)findViewById(R.id.ic_coin_start);
        imgStart=(ImageView)findViewById(R.id.img_4_pic_start);
        adView=(AdView)findViewById(R.id.adViewStart);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        adView.setMinimumHeight(AD_HEIGHT);
        metrics = new DisplayMetrics();
        metrics =this.getResources().getDisplayMetrics();
        handler=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //msg.arg1 là giá trị được trả về trong message
                //của tiến trình con
                Random random=new Random();
                coinAdded=(random.nextInt(5)+1)*10;
                switch (coinAdded){
                    case 10:
                        imgCoin.setBackgroundResource(R.drawable.ic_shop_01_coins);
                        break;
                    case 20:
                        imgCoin.setBackgroundResource(R.drawable.ic_shop_03_coins);
                        break;
                    case 30:
                        imgCoin.setBackgroundResource(R.drawable.ic_shop_04_coins);
                        break;
                    case 40:
                        imgCoin.setBackgroundResource(R.drawable.ic_shop_05_coins);
                        break;
                    case 50:
                        imgCoin.setBackgroundResource(R.drawable.ic_shop_06_coins);
                        break;

                }
                tvCoinAdded.setText(coinAdded+"");
                if(msg.arg1==50){
                   btRandomCoin.setVisibility(View.GONE);
                    btYesGetCoin.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCurrentCoin();
        getSettingSound();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btPlayNow:
                startMainGameAcitivity();
                break;
            case R.id.button_setting:
                startSettingActivity();
                break;
            case R.id.bt_share_app_start:
                shareApp();
                break;
            case R.id.ic_coin_start:
                if(numberGetCoinTime==0)
                    showDialogBuyCoin();
                else
                    showInfoGetCoin();
                break;
        }
    }

    private void showInfoGetCoin() {
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(StartActivity.this,mediaPlayer);
        final Dialog dialog=new Dialog(StartActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        Button btYes=(Button)dialog.findViewById(R.id.btInfoYes);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_info);
        TextView tvInfo=(TextView)dialog.findViewById(R.id.tvInfomation);
        tvInfo.setText(getResources().getString(R.string.cant_get_coin));
        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(StartActivity.this, mediaPlayer);
                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(StartActivity.this, mediaPlayer);
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/4*3,metrics.widthPixels/4*3);
        dialog.show();
    }

    private void showDialogBuyCoin() {
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(StartActivity.this,mediaPlayer);
        final Dialog dialog=new Dialog(StartActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_buy_coin);
        btRandomCoin=(Button)dialog.findViewById(R.id.button_random);
        btYesGetCoin=(Button)dialog.findViewById(R.id.button_yes_get_coin);
        tvCoinAdded=(TextView)dialog.findViewById(R.id.tv_coin_buy);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_buy_coin);
        imgCoin=(ImageView)dialog.findViewById(R.id.img_coin_buy);
        btRandomCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomCoinAdded();
            }
        });
        btYesGetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentCoin();
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(StartActivity.this, mediaPlayer);
                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(StartActivity.this,mediaPlayer);
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/4*3,metrics.widthPixels/4*3);
        dialog.show();

    }

    private void addCurrentCoin() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        int currentCoin=pre.getInt(StaticVariable.CURRENT_COIN, 0);
        currentCoin+=coinAdded;
        numberGetCoinTime=1;
        SharedPreferences.Editor editor=pre.edit();
        editor.putInt(StaticVariable.CURRENT_COIN,currentCoin);
        editor.putInt(StaticVariable.NUM_GET_COIN,1);
        editor.commit();
        tvNumberCoin.setText(currentCoin + "");
    }

    private void randomCoinAdded() {
        isrunning.set(false);
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<=50 && isrunning.get();i++)
                {
                    SystemClock.sleep(50);
                    Message msg=handler.obtainMessage();
                    msg.arg1=i;
                    handler.sendMessage(msg);
                }
            }
        });
        isrunning.set(true);
        th.start();
    }

    private void setupImageSize(){
        ViewGroup.LayoutParams paramLayoutImage = imgStart.getLayoutParams();
        int layoutImageSize=(int)(metrics.widthPixels/4*3);
        paramLayoutImage.width = layoutImageSize;
        paramLayoutImage.height=layoutImageSize;
        imgStart.setLayoutParams(paramLayoutImage);

    }

    private void shareApp() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.link_google_play_for_share));

        startActivity(Intent.createChooser(share, getResources().getString(R.string.share_app_title)));
    }

    private void startSettingActivity() {
        Intent intent=new Intent(StartActivity.this,SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    private void startMainGameAcitivity() {
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(StartActivity.this, mediaPlayer);
        Intent intent=new Intent(StartActivity.this,MainGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

    public void setCurrentCoin() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        int currentCoin=pre.getInt(StaticVariable.CURRENT_COIN,0);
        tvNumberCoin.setText(currentCoin + "");
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(6 * 1024 * 1024); // 6 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config.build());
    }

    private void setupImageDisplayOptions() {
        opt = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_48pt)
                .showImageOnFail(R.drawable.ic_error_48pt)
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    private void getNumberGetCoinPerday(){
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        Calendar calendar=Calendar.getInstance();
        int currentDay=calendar.get(Calendar.DAY_OF_MONTH);
        int lastDay=pre.getInt(StaticVariable.LAST_DAY,1);
        if(currentDay!=lastDay) {
            numberGetCoinTime = 0;
            editor.putInt(StaticVariable.NUM_GET_COIN,0);
            editor.putInt(StaticVariable.LAST_DAY,currentDay);
            editor.commit();
        }
        else {
            numberGetCoinTime=pre.getInt(StaticVariable.NUM_GET_COIN,0);
        }
    }
}
