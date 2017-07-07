package com.example.asus.catchpictureproject.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.asus.catchpictureproject.R;
import com.example.asus.catchpictureproject.fragment.MainGameFragment;

/**
 * Created by Asus on 6/16/2016.
 */
public class MainGameActivity extends AppCompatActivity {
    int[] gridData;
    private MainGameFragment mainGameFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game_activity);
        fragmentManager=getFragmentManager();
        addMainGameFragment();
    }

    private void addMainGameFragment() {
        mainGameFragment= MainGameFragment.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.fr_main_game, mainGameFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.zoom_exit);
    }
}
