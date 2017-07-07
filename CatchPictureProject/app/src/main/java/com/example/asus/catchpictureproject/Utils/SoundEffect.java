package com.example.asus.catchpictureproject.Utils;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;

import com.example.asus.catchpictureproject.R;

/**
 * Created by SON on 2/12/2016.
 */
public class SoundEffect implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    public static SoundEffect instance;
    public static SoundEffect getInstance(){
        if(instance==null){
            instance=new SoundEffect();
        }
        return instance;
    }
    public void playCloseLayerSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.close_layer);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }
    public void playInsertLetterSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.click);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }
    public void playRemoveLetterSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.click);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }
    public void playOpenLayerSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.open_layer);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
    }
}
