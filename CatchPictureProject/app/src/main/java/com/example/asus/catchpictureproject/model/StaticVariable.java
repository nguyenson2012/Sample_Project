package com.example.asus.catchpictureproject.model;

import java.util.ArrayList;

/**
 * Created by Asus on 6/18/2016.
 */
public class StaticVariable {
    private static StaticVariable instance;
    private ArrayList<Word4Pic> listQuestion;
    public static final String PREF_NAME="find_word_data";
    public static final String DATABASE="database";
    public static final String CURRENT_COIN="current_coin";
    public static final String CURRENT_LEVEL="current_level";
    public static final String LAST_DAY="last_day";
    public static final String NUM_GET_COIN="num_get_coin";
    public static final String SOUND_SETTING="sound_setting";

    public StaticVariable(){
        listQuestion=new ArrayList<Word4Pic>();
        setDefaultQuestion();
    }

    public static StaticVariable getInstance(){
        if(instance==null){
            instance=new StaticVariable();
        }
        return instance;
    }

    private void setDefaultQuestion(){
        ArrayList<String> imgLinkCold=new ArrayList<String>();
        ArrayList<String> copyrightLinkCold=new ArrayList<String>();
        imgLinkCold.add(new String("http://i.imgur.com/rtbfmtt.jpg"));
        copyrightLinkCold.add(new String(""));
        imgLinkCold.add(new String("http://i.imgur.com/vgYlt8H.jpg"));
        copyrightLinkCold.add(new String(""));
        imgLinkCold.add(new String("http://i.imgur.com/MvrZcYq.jpg"));
        copyrightLinkCold.add(new String(""));
        imgLinkCold.add(new String("http://i.imgur.com/6lpand4.jpg"));
        copyrightLinkCold.add(new String(""));
        Word4Pic cold=new Word4Pic(1,imgLinkCold,copyrightLinkCold,"COLD","02041013");
        listQuestion.add(cold);
        ArrayList<String> imgLinkLoud=new ArrayList<String>();
        ArrayList<String> copyrightLinkLoud=new ArrayList<String>();
        imgLinkLoud.add(new String("http://i.imgur.com/Pyf6SwR.jpg"));
        copyrightLinkLoud.add(new String(""));
        imgLinkLoud.add(new String("http://i.imgur.com/HMz1JvM.jpg"));
        copyrightLinkLoud.add(new String(""));
        imgLinkLoud.add(new String("http://i.imgur.com/DwVYCRM.jpg"));
        copyrightLinkLoud.add(new String(""));
        imgLinkLoud.add(new String("http://i.imgur.com/BHxmlC9.jpg"));
        copyrightLinkLoud.add(new String(""));
        Word4Pic loud=new Word4Pic(2,imgLinkLoud,copyrightLinkLoud,"LOUD","03051113");
        listQuestion.add(loud);

    }

    public ArrayList<Word4Pic> getListQuestion() {
        return listQuestion;
    }

    public void setListQuestion(ArrayList<Word4Pic> listQuestion) {
        this.listQuestion = listQuestion;
    }
}
