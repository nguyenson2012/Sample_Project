package com.example.asus.catchpictureproject.fragment;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.Utils.DBHelper;
import com.example.asus.catchpictureproject.Utils.SoundEffect;
import com.example.asus.catchpictureproject.activity.StartActivity;
import com.example.asus.catchpictureproject.model.StaticVariable;
import com.example.asus.catchpictureproject.model.Word4Pic;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Asus on 6/18/2016.
 */
public class MainGameFragment extends Fragment implements View.OnClickListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    private static final int AD_HEIGHT =50 ;
    ImageView buttonImgOne,buttonImgTwo,buttonImgThree,buttonImgFour;
    AdView adView;
    private ArrayList<Button> listButtonResult;
    private int[] buttonResultIds={R.id.btResult1,R.id.btResult2,R.id.btResult3,R.id.btResult4,
            R.id.btResult5,R.id.btResult6,R.id.btResult7,R.id.btResult8};
    private ArrayList<Button> listKeyboard;
    private int[] buttonAnswerIds={R.id.btAnswer1,R.id.btAnswer2,R.id.btAnswer3,R.id.btAnswer4,
            R.id.btAnswer5,R.id.btAnswer6,R.id.btAnswer7,R.id.btAnswer8,R.id.btAnswer9,R.id.btAnswer10,
            R.id.btAnswer11,R.id.btAnswer12,R.id.btAnswer13,R.id.btAnswer14};
    private StaticVariable staticVariable;
    private ArrayList<Word4Pic> listWord;
    private DisplayImageOptions opt;
    private ImageLoader imageLoader;
    private int currentLevel=1;
    private int currentCoin=0;
    private int numberImageOpen=0;
    private int numberGetCoinTime=0;
    private Word4Pic currentWord;
    private int[] arrayPositionButtonResult;
    private int[] arrayPositionAnswerButton;
    private String[] arrayStringResult;
    private int[] arrayImageOpen;
    private static final int INVALID_POSITION=-1;
    private static final int HINT_POSITION=-2;
    private static final int DELETE_LETTER_POSITION=-3;
    private static final int GONE_LETTER_POSITION=-4;
    private static final int IMAGE_CLOSE=9;
    private static final int IMAGE_OPEN=10;
    Handler handlerWrong;
    Handler handlerGetCoin;
    AtomicBoolean isrunning=new AtomicBoolean(false);
    private RelativeLayout layoutCorrectStars;
    private TextView tvGotIt;
    private ImageView imgCoinCorrect;
    private Button buttonContinue,buttonRevealLetter,buttonDeleteLetter;
    private FrameLayout frCorrect;
    private TextView tvLevel;
    private TextView tvNumberCoin;
    private Button btBack;
    private Button btCoin;
    private LinearLayout layoutImage;
    DisplayMetrics metrics;
    private MediaPlayer mediaPlayer;
    private TextView tvCoinAdded;
    Button btRandomCoin,btYesGetCoin;
    Button btShare;
    ImageView imgCoin;
    DBHelper database;
    private int coinAdded;
    private Bitmap myBitmap;
    private boolean isSoundOn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main_game_layout,container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSettingSound();
        getNumberGetCoinPerday();
        setupView();
        setSizeForButtonAnswer();
        getCurrentCoin();
        getCurrentLevel();
        resetLevel();
        setupButtonResult();
        setupButtonAnswer();
        registerEvent();
        initImageLoader(getActivity());
        setupImageDisplayOptions();
        setupHandler();
        getState();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateBeforeLeave();
    }

    private void getCurrentLevel() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        currentLevel=pre.getInt(StaticVariable.CURRENT_LEVEL,1);
        tvLevel.setText("LEVEL:" + currentLevel);
        if(currentLevel<=listWord.size()) {
            //currentWord=listWord.get(currentLevel-1);
            currentWord=database.getPicture(currentLevel);
        }

    }

    private void getSettingSound() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        isSoundOn=pre.getBoolean(StaticVariable.SOUND_SETTING,true);
    }

    private void setupHandler() {
        handlerWrong=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //msg.arg1 là giá trị được trả về trong message
                //của tiến trình con
                if(msg.arg1%2==0){
                    for(Button button:listButtonResult)
                        button.setTextColor(Color.WHITE);
                }else {
                    for(Button button:listButtonResult)
                        button.setTextColor(Color.RED);
                }
                if(msg.arg1==10){
                    for(int i=0;i<arrayPositionButtonResult.length;i++){
                        if(arrayPositionButtonResult[i]==HINT_POSITION)
                            listButtonResult.get(i).setTextColor(Color.GREEN);
                    }
                }

            }
        };
    }

    private void registerEvent() {
        for(Button button:listKeyboard){
            button.setOnClickListener(this);
        }
        for(Button button:listButtonResult){
            button.setOnClickListener(this);
        }
        buttonImgOne.setOnClickListener(this);
        buttonImgTwo.setOnClickListener(this);
        buttonImgThree.setOnClickListener(this);
        buttonImgFour.setOnClickListener(this);
        buttonContinue.setOnClickListener(this);
        buttonRevealLetter.setOnClickListener(this);
        buttonDeleteLetter.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btCoin.setOnClickListener(this);
        btShare.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        setCurrentCoin();
    }

    private void setCurrentCoin() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putInt(StaticVariable.CURRENT_COIN, currentCoin);
        editor.commit();
    }

    private void setupButtonAnswer() {
        String result=currentWord.getResult();
        String positionCharacterResult=currentWord.getPositionLetterResult();
        for(int i=0;i<positionCharacterResult.length()-1;i+=2){
            String letterButton=result.substring(i/2,i/2+1);
            int positionButton=Integer.parseInt(positionCharacterResult.substring(i,i+2));
            listKeyboard.get(positionButton).setText(letterButton);
        }
        for(Button button:listKeyboard){
            if(button.getText().equals("")){
                randomLetterAnswer(button);
            }
            button.setClickable(true);
        }
    }

    private void setupButtonResult() {

        arrayPositionButtonResult=new int[8];
        arrayStringResult=new String[8];
        arrayPositionAnswerButton=new int[14];
        arrayImageOpen=new int[4];
        for(int i=0;i<8;i++) {
            arrayPositionButtonResult[i] = INVALID_POSITION;
            arrayStringResult[i]="";
            listButtonResult.get(i).setTextColor(Color.WHITE);
            listButtonResult.get(i).setClickable(false);
        }
        for(int i=0;i<14;i++)
            arrayPositionAnswerButton[i]=0;
        for (int i=0;i<4;i++)
            arrayImageOpen[i]=IMAGE_CLOSE;
        switch (currentWord.getResult().length()){
            case 3://3 result word
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                break;
            case 4:
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                listButtonResult.get(5).setVisibility(View.VISIBLE);
                break;
            case 5:
                listButtonResult.get(1).setVisibility(View.VISIBLE);
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                listButtonResult.get(5).setVisibility(View.VISIBLE);
                break;
            case 6:
                listButtonResult.get(1).setVisibility(View.VISIBLE);
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                listButtonResult.get(5).setVisibility(View.VISIBLE);
                listButtonResult.get(6).setVisibility(View.VISIBLE);
                break;
            case 7:
                listButtonResult.get(0).setVisibility(View.VISIBLE);
                listButtonResult.get(1).setVisibility(View.VISIBLE);
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                listButtonResult.get(5).setVisibility(View.VISIBLE);
                listButtonResult.get(6).setVisibility(View.VISIBLE);
                break;
            case 8:
                listButtonResult.get(1).setVisibility(View.VISIBLE);
                listButtonResult.get(2).setVisibility(View.VISIBLE);
                listButtonResult.get(3).setVisibility(View.VISIBLE);
                listButtonResult.get(4).setVisibility(View.VISIBLE);
                listButtonResult.get(5).setVisibility(View.VISIBLE);
                listButtonResult.get(6).setVisibility(View.VISIBLE);
                listButtonResult.get(7).setVisibility(View.VISIBLE);
                listButtonResult.get(8).setVisibility(View.VISIBLE);
                break;

        }

    }

    private void resetButtonResult(){
        for(Button button:listButtonResult){
            button.setVisibility(View.INVISIBLE);
            button.setText("");
            button.setClickable(false);
        }
    }

    private void setupView() {
        metrics = new DisplayMetrics();
        metrics = getActivity().getResources().getDisplayMetrics();
        buttonImgOne=(ImageView)getActivity().findViewById(R.id.button_img_one);
        buttonImgTwo=(ImageView)getActivity().findViewById(R.id.button_img_two);
        buttonImgThree=(ImageView)getActivity().findViewById(R.id.button_img_three);
        buttonImgFour=(ImageView)getActivity().findViewById(R.id.button_img_four);
        buttonContinue=(Button)getActivity().findViewById(R.id.button_continue_level);
        frCorrect=(FrameLayout)getActivity().findViewById(R.id.frame_correct_answer);
        buttonRevealLetter=(Button)getActivity().findViewById(R.id.btAnswerHint);
        buttonDeleteLetter=(Button)getActivity().findViewById(R.id.btAnswerDeleteLetter);
        tvLevel=(TextView)getActivity().findViewById(R.id.tvlevelMain);
        tvNumberCoin=(TextView)getActivity().findViewById(R.id.tvNumberCoinMain);
        btBack=(Button)getActivity().findViewById(R.id.button_ic_back_main);
        btCoin=(Button)getActivity().findViewById(R.id.ic_coin_main);
        btShare=(Button)getActivity().findViewById(R.id.button_share_app);
        layoutCorrectStars=(RelativeLayout)getActivity().findViewById(R.id.layout_corrext_star);
        tvGotIt=(TextView)getActivity().findViewById(R.id.tvYouGotIt);
        imgCoinCorrect=(ImageView)getActivity().findViewById(R.id.imgCoinCorrect);
        layoutImage=(LinearLayout)getActivity().findViewById(R.id.layout_image);
        adView=(AdView)getActivity().findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        adView.setMinimumHeight(AD_HEIGHT);
        database=new DBHelper(getActivity());
        setupKeyBoard();
        staticVariable=StaticVariable.getInstance();
        listWord=new ArrayList<Word4Pic>();
        listWord=staticVariable.getListQuestion();
        handlerGetCoin=new Handler(){
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

    private void setupKeyBoard() {
        listButtonResult=new ArrayList<Button>();
        for(int i=0;i<buttonResultIds.length;i++){
            Button button=(Button)getActivity().findViewById(buttonResultIds[i]);
            listButtonResult.add(button);
        }
        listKeyboard=new ArrayList<Button>();
        for(int i=0;i<buttonAnswerIds.length;i++){
            Button button=(Button)getActivity().findViewById(buttonAnswerIds[i]);
            listKeyboard.add(button);
        }
    }

    public static MainGameFragment newInstance() {
        MainGameFragment demoFragment=new MainGameFragment();
        return demoFragment;
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

    private void randomLetterAnswer(Button btAnswer){
        Random random=new Random();
        int randomLetterPosition=random.nextInt(26);
        switch (randomLetterPosition){
            case 0:
                btAnswer.setText("A");
                break;
            case 1:
                btAnswer.setText("B");
                break;
            case 2:
                btAnswer.setText("C");
                break;
            case 3:
                btAnswer.setText("D");
                break;
            case 4:
                btAnswer.setText("E");
                break;
            case 5:
                btAnswer.setText("F");
                break;
            case 6:
                btAnswer.setText("G");
                break;
            case 7:
                btAnswer.setText("H");
                break;
            case 8:
                btAnswer.setText("I");
                break;
            case 9:
                btAnswer.setText("J");
                break;
            case 10:
                btAnswer.setText("K");
                break;
            case 11:
                btAnswer.setText("L");
                break;
            case 12:
                btAnswer.setText("M");
                break;
            case 13:
                btAnswer.setText("N");
                break;
            case 14:
                btAnswer.setText("O");
                break;
            case 15:
                btAnswer.setText("P");
                break;
            case 16:
                btAnswer.setText("Q");
                break;
            case 17:
                btAnswer.setText("R");
                break;
            case 18:
                btAnswer.setText("S");
                break;
            case 19:
                btAnswer.setText("T");
                break;
            case 20:
                btAnswer.setText("U");
                break;
            case 21:
                btAnswer.setText("V");
                break;
            case 22:
                btAnswer.setText("W");
                break;
            case 23:
                btAnswer.setText("X");
                break;
            case 24:
                btAnswer.setText("Y");
                break;
            case 25:
                btAnswer.setText("Z");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btAnswer1:
                fillLetterInAnswer(0);
                break;
            case R.id.btAnswer2:
                fillLetterInAnswer(1);
                break;
            case R.id.btAnswer3:
                fillLetterInAnswer(2);
                break;
            case R.id.btAnswer4:
                fillLetterInAnswer(3);
                break;
            case R.id.btAnswer5:
                fillLetterInAnswer(4);
                break;
            case R.id.btAnswer6:
                fillLetterInAnswer(5);
                break;
            case R.id.btAnswer7:
                fillLetterInAnswer(6);
                break;
            case R.id.btAnswer8:
                fillLetterInAnswer(7);
                break;
            case R.id.btAnswer9:
                fillLetterInAnswer(8);
                break;
            case R.id.btAnswer10:
                fillLetterInAnswer(9);
                break;
            case R.id.btAnswer11:
                fillLetterInAnswer(10);
                break;
            case R.id.btAnswer12:
                fillLetterInAnswer(11);
                break;
            case R.id.btAnswer13:
                fillLetterInAnswer(12);
                break;
            case R.id.btAnswer14:
                fillLetterInAnswer(13);
                break;
            case R.id.btResult1:
                redoFillLetter(0);
                break;
            case R.id.btResult2:
                redoFillLetter(1);
                break;
            case R.id.btResult3:
                redoFillLetter(2);
                break;
            case R.id.btResult4:
                redoFillLetter(3);
                break;
            case R.id.btResult5:
                redoFillLetter(4);
                break;
            case R.id.btResult6:
                redoFillLetter(5);
                break;
            case R.id.btResult7:
                redoFillLetter(6);
                break;
            case R.id.btResult8:
                redoFillLetter(7);
                break;
            case R.id.button_img_one:
                if(arrayImageOpen[0]==IMAGE_CLOSE) {
                    showImageInButton(buttonImgOne, 0);
                }
                else
                    showDialogImgLarge(0);
                break;
            case R.id.button_img_two:
                if(arrayImageOpen[1]==IMAGE_CLOSE)
                    showImageInButton(buttonImgTwo, 1);
                else
                    showDialogImgLarge(1);
                break;
            case R.id.button_img_three:
                if(arrayImageOpen[2]==IMAGE_CLOSE)
                    showImageInButton(buttonImgThree, 2);
                else
                    showDialogImgLarge(2);
                break;
            case R.id.button_img_four:
                if(arrayImageOpen[3]==IMAGE_CLOSE)
                    showImageInButton(buttonImgFour, 3);
                else
                    showDialogImgLarge(3);
                break;
            case R.id.button_continue_level:
                increaseLevel();
                break;
            case R.id.btAnswerHint:
                showDialogHint(getResources().getString(R.string.reveal_question));
                break;
            case R.id.btAnswerDeleteLetter:
                showDialogHint(getResources().getString(R.string.delete_letter_question));
                break;
            case R.id.button_ic_back_main:
                backToPreviousLesson();
                break;
            case R.id.ic_coin_main:
                if(numberGetCoinTime==0)
                    showDialogBuyCoin();
                else
                    showInfoGetCoin();
                break;
            case R.id.button_share_app:
                //shareApp();
                //takeScreenShot();
//                myBitmap = getBitmapOfView(layoutImage);
//                saveBitmap(myBitmap);
                //takeScreenShotFalcon();
                takeScreenShortAndShare();
                break;
        }
    }

    private void takeScreenShortAndShare() {
        View rootView = getActivity().getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        new ScreenShot().execute(bitmap);
    }

    public Bitmap getBitmapOfView(View v)
    {
        View rootview = getActivity().getWindow().getDecorView().getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bmp = rootview.getDrawingCache();
        return bmp;
    }

    private void takeScreenshotNew() {
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" +"screen.jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getActivity(), "Screenshot saved..!", Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }



    public void createImageFromBitmap(Bitmap bmp)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File file = new File( Environment.getExternalStorageDirectory() +
                "/capturedscreen.jpg");
        try
        {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            ostream.write(bytes.toByteArray());
            ostream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void takeScreenShot() {
        //take screenshot
        myBitmap = captureScreen(layoutImage);


        try {
            if(myBitmap!=null){
                //save image to SD card
                saveImage(myBitmap);
            }
            Toast.makeText(getActivity(), "Screenshot saved..!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Bitmap captureScreen(View v) {
        Bitmap screenshot = null;
        try {
            if(v!=null) {
                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }

        }catch (Exception e){
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
        }

        return screenshot;
    }

    public static void saveImage(Bitmap bitmap) throws IOException{

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
//        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png");
//        f.createNewFile();
        File direct = new File(Environment.getExternalStorageDirectory() + "/MyFolder/abc");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/MyFolder/abc");
            wallpaperDirectory.mkdirs();
        }

        File mainfile = new File(new File("/sdcard/MyFolder/abc"), "screen.jpg");
        if (mainfile.exists()) {
            mainfile.delete();
        }
        FileOutputStream fo = new FileOutputStream(mainfile);
        fo.write(bytes.toByteArray());
        fo.close();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "screenshot",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void saveBitmap(Bitmap bitmap) {
        try {

            File mainfile = createImageFile();
            FileOutputStream fos = new FileOutputStream(mainfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
    }

    class ScreenShot extends AsyncTask<Bitmap, String, String> {
        File imageFile;
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            int count;
            try {
                // image naming and path  to include sd card  appending name you choose for file
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" +"catch_picture"+currentDateandTime+".png";
                imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            File checkFile=new File(imageFile.getAbsolutePath());
            if(checkFile.exists()) {
                shareImageQuiz(imageFile);
            }else {
                shareApp();
            }

        }

    }

    private void shareImageQuiz(File imageFile) {
//        Uri uri = Uri.fromFile(new File(imageFile.getAbsolutePath()));
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_img_message));
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.setType("image/*");
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_app_title)));
        List<Intent> shareIntentsLists = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        Uri uri = Uri.fromFile(new File(imageFile.getAbsolutePath()));
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_img_message));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> resInfos = getActivity().getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                if (packageName.toLowerCase().contains("facebook")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.setPackage(packageName);
                    shareIntent.setPackage(packageName);
                    shareIntentsLists.add(intent);
                }
            }
            Intent chooserIntent = Intent.createChooser(shareIntent, "Choose app to share");
            startActivity(chooserIntent);
//            if (!shareIntentsLists.isEmpty()) {
//                Intent chooserIntent = Intent.createChooser(shareIntentsLists.remove(0), "Choose app to share");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toArray(new Parcelable[]{}));
//                chooserIntent.setAction(Intent.ACTION_SEND);
//                chooserIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_img_message));
//                chooserIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                chooserIntent.setType("image/*");
//                chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(chooserIntent);
//            } else
//                Log.e("Error", "No Apps can perform your task");

        }
    }

    private void shareApp() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_app_title));
        share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.link_google_play_for_share));

        startActivity(Intent.createChooser(share, getResources().getString(R.string.share_app_title)));
    }

    private void showDialogBuyCoin() {
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(getActivity(),mediaPlayer);
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_buy_coin);
        btRandomCoin=(Button)dialog.findViewById(R.id.button_random);
        btYesGetCoin=(Button)dialog.findViewById(R.id.button_yes_get_coin);
        tvCoinAdded=(TextView)dialog.findViewById(R.id.tv_coin_buy);
        imgCoin=(ImageView)dialog.findViewById(R.id.img_coin_buy);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_buy_coin);
        btRandomCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomCoinAdded();
            }
        });
        btYesGetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(getActivity(), mediaPlayer);
                addCurrentCoin();
                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(getActivity(), mediaPlayer);
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels / 4 * 3, metrics.widthPixels / 4 * 3);
        dialog.show();
    }
    private void addCurrentCoin() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        currentCoin+=coinAdded;
        numberGetCoinTime=1;
        SharedPreferences.Editor editor=pre.edit();
        editor.putInt(StaticVariable.CURRENT_COIN, currentCoin);
        editor.putInt(StaticVariable.NUM_GET_COIN, 1);
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
                    Message msg=handlerGetCoin.obtainMessage();
                    msg.arg1=i;
                    handlerGetCoin.sendMessage(msg);
                }
            }
        });
        isrunning.set(true);
        th.start();
    }

    private void backToPreviousLesson() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, R.anim.zoom_exit);
    }

    private void fillLetterInAnswer(int positionButton) {
        if(isSoundOn)
            SoundEffect.getInstance().playInsertLetterSound(getActivity(), mediaPlayer);
        Button btAnswerClick=listKeyboard.get(positionButton);
        for(int i=0;i<8;i++){
            if(arrayPositionButtonResult[i]==INVALID_POSITION&&listButtonResult.get(i).getVisibility()==View.VISIBLE&&arrayPositionAnswerButton[positionButton]==0){
                arrayPositionAnswerButton[positionButton]=GONE_LETTER_POSITION;
                arrayPositionButtonResult[i]=positionButton;
                Button btResult=listButtonResult.get(i);
                btResult.setText(btAnswerClick.getText()+"");
                btResult.setClickable(true);
                arrayStringResult[i]=btAnswerClick.getText()+"";
                btAnswerClick.setBackgroundResource(R.drawable.bt_letter_result_after_go);
                btAnswerClick.setText("");
                btAnswerClick.setClickable(false);
                break;
            }
        }
        checkAnswer();
    }
    private void redoFillLetter(int positionResultButton){
        Button btResult=listButtonResult.get(positionResultButton);
        if(isSoundOn&&!btResult.getText().equals(""))
            SoundEffect.getInstance().playRemoveLetterSound(getActivity(), mediaPlayer);
        if(!btResult.getText().equals("")&&btResult.getVisibility()==View.VISIBLE&&arrayPositionButtonResult[positionResultButton]!=HINT_POSITION){
            int positionBtAnswerRedo=arrayPositionButtonResult[positionResultButton];
            if(positionBtAnswerRedo!=INVALID_POSITION) {
                listKeyboard.get(positionBtAnswerRedo).setBackgroundResource(R.drawable.bt_answer);
                listKeyboard.get(positionBtAnswerRedo).setClickable(true);
                listKeyboard.get(positionBtAnswerRedo).setText(btResult.getText() + "");
                arrayPositionButtonResult[positionResultButton]=INVALID_POSITION;
                arrayPositionAnswerButton[positionBtAnswerRedo]=0;
                arrayStringResult[positionResultButton]="";
            }
            btResult.setText("");
            }
        }
    private void showImageInButton(final ImageView button,int positionButton) {
//        ObjectAnimator animOut = ObjectAnimator.ofFloat(button, "rotationX", 0f, 90f).
//                setDuration(300);
//        ObjectAnimator animInt = ObjectAnimator.ofFloat(button, "rotationX", 90f, 0f).
//                setDuration(300);
//        animInt.setStartDelay(300);
//        final AnimatorSet animation = new AnimatorSet();
//        ((AnimatorSet) animation).playTogether(animOut,animInt);
//        animation.start();
        button.animate().setDuration(400);
        button.animate().rotationXBy(360);
        mediaPlayer=MediaPlayer.create(getActivity(),R.raw.flip);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        if(arrayImageOpen[positionButton]==IMAGE_CLOSE){
            numberImageOpen++;
            arrayImageOpen[positionButton]=IMAGE_OPEN;
            imageLoader.displayImage(currentWord.getListImageLink().get(positionButton), button);
        }
    }


    private void doAnimateWrongAnswer(){
        isrunning.set(false);
        //tạo 1 tiến trình CON
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                //vòng lặp chạy 100 lần
                for(int i=1;i<=10 && isrunning.get();i++)
                {
                    SystemClock.sleep(100);
                    //lấy message từ Main thread
                    Message msg=handlerWrong.obtainMessage();
                    //gán giá trị vào cho arg1 để gửi về Main thread
                    msg.arg1=i;
                    //gửi lại Message này về cho Main Thread
                    handlerWrong.sendMessage(msg);
                }
            }
        });
        isrunning.set(true);
        //kích hoạt tiến trình
        th.start();
    }
    private void doAnimateRightAnswer(){
        frCorrect.setVisibility(View.VISIBLE);
        Animation animSlideLeft=AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_left);
        Animation animZoomEnter=AnimationUtils.loadAnimation(getActivity(),R.anim.dialog_enter);
        layoutCorrectStars.startAnimation(animSlideLeft);
        tvGotIt.startAnimation(animSlideLeft);
        buttonContinue.startAnimation(animZoomEnter);
        imgCoinCorrect.startAnimation(animZoomEnter);
//        Animation animationRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
//        buttonStartCorrectLeft.startAnimation(animationRotate);
//        buttonStartCorrectRight.startAnimation(animationRotate);
    }

    private void checkAnswer(){
        String answerOfUser="";
        for(Button button:listButtonResult){
            if(button.getVisibility()==View.VISIBLE&&!button.getText().equals("")){
                answerOfUser+=button.getText();
            }
            if(answerOfUser.length()==currentWord.getResult().length()){
                if(!answerOfUser.equals(currentWord.getResult())) {
                    doAnimateWrongAnswer();
                    for(int i=0;i<arrayPositionButtonResult.length;i++){
                        if(arrayPositionButtonResult[i]==HINT_POSITION)
                            listButtonResult.get(i).setTextColor(Color.GREEN);
                    }
                    if(isSoundOn)
                        playSoundEffect(false);
                }
                else {
                    doAnimateRightAnswer();
                    if(isSoundOn)
                        playSoundEffect(true);
                }
            }
        }
    }

    private void resetLevel(){
        resetButtonResult();
        resetButtonAnswer();
        resetButtonImage();
        frCorrect.setVisibility(View.GONE);
        tvLevel.setText(currentLevel+"");
    }

    private void resetButtonImage() {
        buttonImgOne.setImageResource(R.drawable.puzzle_icon);
        buttonImgTwo.setImageResource(R.drawable.puzzle_icon);
        buttonImgThree.setImageResource(R.drawable.puzzle_icon);
        buttonImgFour.setImageResource(R.drawable.puzzle_icon);
    }

    private void resetButtonAnswer() {
        for(Button button:listKeyboard) {
            button.setText("");
            button.setBackgroundResource(R.drawable.bt_answer);
        }
    }

    private void increaseLevel(){
        for(int i=0;i<8;i++) {
            arrayPositionButtonResult[i] = INVALID_POSITION;
            arrayStringResult[i]="";
        }
        for(int i=0;i<14;i++)
            arrayPositionAnswerButton[i]=0;
        for (int i=0;i<4;i++)
            arrayImageOpen[i]=IMAGE_CLOSE;
        if(listWord.size()>currentLevel) {
            currentLevel++;
            //currentWord = listWord.get(currentLevel - 1);
            currentWord=database.getPicture(currentLevel);
        }
        setCurrentLevel();
        increaseCoin();
        resetLevel();
        setupButtonResult();
        setupButtonAnswer();
    }

    private void setCurrentLevel() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putInt(StaticVariable.CURRENT_LEVEL, currentLevel);
        editor.commit();

    }

    private void increaseCoin() {
        switch (numberImageOpen){
            case 0:
                currentCoin+=40;
                break;
            case 1:
                currentCoin+=40;
                break;
            case 2:
                currentCoin+=30;
                break;
            case 3:
                currentCoin+=20;
                break;
            case 4:
                currentCoin+=10;
        }
        tvNumberCoin.setText(currentCoin + "");
    }

    private void showDialogHint(final String HintSentence){
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(getActivity(),mediaPlayer);
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        TextView tvHint=(TextView)dialog.findViewById(R.id.tvHint);
        Button btHintNo=(Button)dialog.findViewById(R.id.btNoHint);
        Button btHintYes=(Button)dialog.findViewById(R.id.btYesHint);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_help);
        tvHint.setText(HintSentence);
        btHintNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btHintYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HintSentence.equals(getResources().getString(R.string.reveal_question))) {
                    revealLetter();
                    if(isSoundOn)
                        SoundEffect.getInstance().playCloseLayerSound(getActivity(),mediaPlayer);
                }
                else if(HintSentence.equals(getResources().getString(R.string.delete_letter_question))){
                    deleteLetter();
                    if(isSoundOn)
                        SoundEffect.getInstance().playCloseLayerSound(getActivity(), mediaPlayer);
                }

                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/4*3,metrics.widthPixels/4*3);
        dialog.show();

    }

    private void showDialogInfo(final String InfoSentence){
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(getActivity(),mediaPlayer);
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        TextView tvInfo=(TextView)dialog.findViewById(R.id.tvInfomation);
        Button btInfoYes=(Button)dialog.findViewById(R.id.btInfoYes);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_info);
        tvInfo.setText(InfoSentence);
        btInfoYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playOpenLayerSound(getActivity(),mediaPlayer);
                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(getActivity(), mediaPlayer);
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/3*2,metrics.widthPixels/3*2);
        dialog.show();

    }

    private void showDialogImgLarge(int positionButton){
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_large_layout);
        ImageView imgLarge=(ImageView)dialog.findViewById(R.id.img_large);
        TextView tvCopyRight=(TextView)dialog.findViewById(R.id.tvCopyright);
        ViewGroup.LayoutParams paramImgLarge = imgLarge.getLayoutParams();
        paramImgLarge.width=metrics.widthPixels/4*3;
        paramImgLarge.height=metrics.widthPixels/4*3;
        imgLarge.setLayoutParams(paramImgLarge);
        imageLoader.displayImage(currentWord.getListImageLink().get(positionButton), imgLarge);
        imgLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvCopyRight.setText("Copyright:" + currentWord.getCopyRight().get(positionButton));
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/4*3,metrics.widthPixels/4*3);
        dialog.show();

    }

    private void revealLetter(){
        int positionReveal=0;
        if(currentCoin>=10) {
            currentCoin -= 10;
            tvNumberCoin.setText(currentCoin+"");
            for(int i=0;i<listButtonResult.size();i++){
                Button button=listButtonResult.get(i);
                if(button.getVisibility()==View.VISIBLE){
                    if(arrayPositionButtonResult[i]==INVALID_POSITION){
                        String letterReveal=currentWord.getResult().substring(positionReveal,positionReveal+1);
                        button.setText(letterReveal);
                        button.setTextColor(Color.GREEN);
                        arrayPositionButtonResult[i]=HINT_POSITION;
                        arrayStringResult[i]=letterReveal;
                        checkAnswer();
                        break;
                    }else {
                        positionReveal++;
                    }
                }
            }
        }else {
            showDialogInfo(getResources().getString(R.string.dont_enough_coin));
        }
    }

    private void deleteLetter(){
        boolean isDeleteLetter=false;
        if(currentCoin>=10) {
            currentCoin -= 10;
            tvNumberCoin.setText(currentCoin+"");
            do{
               Random random=new Random();
                int positionDeleteAnswer=random.nextInt(listKeyboard.size());
                Button button=listKeyboard.get(positionDeleteAnswer);
                CharSequence letter=button.getText();
                if(!currentWord.getResult().contains(letter)){
                    button.setText("");
                    button.setBackgroundResource(R.drawable.bt_letter_result_after_go);
                    arrayPositionAnswerButton[positionDeleteAnswer]=DELETE_LETTER_POSITION;
                    isDeleteLetter=true;
                }
            }while (!isDeleteLetter);
        }else {
            showDialogInfo(getResources().getString(R.string.dont_enough_coin));
        }
    }

    private void getCurrentCoin() {
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        currentCoin=pre.getInt(StaticVariable.CURRENT_COIN,0);
        tvNumberCoin.setText(currentCoin+"");
    }

    private void playSoundEffect(boolean isRightAnswer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        if(isRightAnswer){
            mediaPlayer=MediaPlayer.create(getActivity(),R.raw.success_level);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }else {
            mediaPlayer=MediaPlayer.create(getActivity(),R.raw.wrong_answer);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private void setSizeForButtonAnswer(){
        int buttonWidth= (int) (metrics.widthPixels/8*0.9);
        for(Button button:listKeyboard){
            ViewGroup.LayoutParams params = button.getLayoutParams();
            params.width = buttonWidth;
            params.height=buttonWidth;
            button.setLayoutParams(params);
        }
        for(Button button:listButtonResult){
            ViewGroup.LayoutParams params = button.getLayoutParams();
            params.width = buttonWidth;
            params.height=buttonWidth;
            button.setLayoutParams(params);
        }
        ViewGroup.LayoutParams paramReveal = buttonRevealLetter.getLayoutParams();
        paramReveal.width = buttonWidth;
        paramReveal.height=buttonWidth;
        buttonRevealLetter.setLayoutParams(paramReveal);
        ViewGroup.LayoutParams paramDelete = buttonDeleteLetter.getLayoutParams();
        paramDelete.width = buttonWidth;
        paramDelete.height=buttonWidth;
        buttonDeleteLetter.setLayoutParams(paramDelete);
        ViewGroup.LayoutParams paramLayoutImage = layoutImage.getLayoutParams();
        int layoutImageSize=(int)(metrics.widthPixels/4*3);
        paramLayoutImage.width = layoutImageSize;
        paramLayoutImage.height=layoutImageSize;
        layoutImage.setLayoutParams(paramLayoutImage);

    }

    private void saveStateBeforeLeave(){
        setCurrentCoin();
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        for(int i=0;i<arrayPositionButtonResult.length;i++){
            editor.putInt("arrayPositionButtonResult"+i,arrayPositionButtonResult[i]);
        }
        for(int i=0;i<arrayPositionAnswerButton.length;i++){
            editor.putInt("arrayPositionAnswerButton"+i,arrayPositionAnswerButton[i]);
        }
        for(int i=0;i<arrayStringResult.length;i++){
            editor.putString("arrayStringResult" + i, arrayStringResult[i]);
        }
        for(int i=0;i<arrayImageOpen.length;i++){
            editor.putInt("arrayImageOpen" + i, arrayImageOpen[i]);
        }
        editor.commit();
    }

    private void getState(){
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
        for(int i=0;i<arrayPositionButtonResult.length;i++){
            arrayPositionButtonResult[i]=pre.getInt("arrayPositionButtonResult"+i,INVALID_POSITION);
        }
        for(int i=0;i<arrayPositionAnswerButton.length;i++){
            arrayPositionAnswerButton[i]=pre.getInt("arrayPositionAnswerButton" + i, 0);
        }
        for(int i=0;i<arrayStringResult.length;i++){
            arrayStringResult[i]=pre.getString("arrayStringResult" + i, "");
        }
        for(int i=0;i<arrayImageOpen.length;i++){
            arrayImageOpen[i]=pre.getInt("arrayImageOpen"+i, IMAGE_CLOSE);
        }
        for(int i=0;i<listButtonResult.size();i++){
            Button button=listButtonResult.get(i);
            button.setText(arrayStringResult[i]);
            if(arrayPositionButtonResult[i]==HINT_POSITION)
                button.setTextColor(Color.GREEN);

        }
        for(int i=0;i<listKeyboard.size();i++){
            Button button=listKeyboard.get(i);
            if(arrayPositionAnswerButton[i]==DELETE_LETTER_POSITION||arrayPositionAnswerButton[i]==GONE_LETTER_POSITION){
                button.setBackgroundResource(R.drawable.bt_letter_result_after_go);
                button.setText("");
            }
        }
        if(arrayImageOpen[0]==IMAGE_OPEN) {
            imageLoader.displayImage(currentWord.getListImageLink().get(0),buttonImgOne);
        }
        if(arrayImageOpen[1]==IMAGE_OPEN) {
            imageLoader.displayImage(currentWord.getListImageLink().get(1),buttonImgTwo);
        }
        if(arrayImageOpen[2]==IMAGE_OPEN) {
            imageLoader.displayImage(currentWord.getListImageLink().get(2),buttonImgThree);
        }
        if(arrayImageOpen[3]==IMAGE_OPEN) {
            imageLoader.displayImage(currentWord.getListImageLink().get(3),buttonImgFour);
        }

    }

    private void getNumberGetCoinPerday(){
        SharedPreferences pre = getActivity().getSharedPreferences
                (StaticVariable.PREF_NAME, getActivity().MODE_PRIVATE);
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

    private void showInfoGetCoin() {
        if(isSoundOn)
            SoundEffect.getInstance().playOpenLayerSound(getActivity(),mediaPlayer);
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        Button btYes=(Button)dialog.findViewById(R.id.btInfoYes);
        Button btExit=(Button)dialog.findViewById(R.id.bt_exit_dialog_info);
        TextView tvInfo=(TextView)dialog.findViewById(R.id.tvInfomation);
        tvInfo.setText(getResources().getString(R.string.cant_get_coin));
        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOn)
                    SoundEffect.getInstance().playCloseLayerSound(getActivity(), mediaPlayer);
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setLayout(metrics.widthPixels/4*3,metrics.widthPixels/4*3);
        dialog.show();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
