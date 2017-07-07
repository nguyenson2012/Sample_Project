package com.example.asus.catchpictureproject.model;

import java.util.ArrayList;

/**
 * Created by Asus on 6/18/2016.
 */
public class Word4Pic {
    private int positionQuestion;
    private ArrayList<String> listImageLink;
    private ArrayList<String> copyRight;
    private String result;
    private String positionLetterResult;

    public Word4Pic(int positionQuestion,ArrayList<String> listImageLink,ArrayList<String> copyRightLink, String result,String positionLetterResult) {
        this.positionQuestion=positionQuestion;
        this.listImageLink = listImageLink;
        this.copyRight=copyRightLink;
        this.result = result;
        this.positionLetterResult=positionLetterResult;
    }

    public Word4Pic(){

    }

    public ArrayList<String> getListImageLink() {
        return listImageLink;
    }

    public void setListImageLink(ArrayList<String> listImageLink) {
        this.listImageLink = listImageLink;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public int getPositionQuestion() {
        return positionQuestion;
    }

    public void setPositionQuestion(int positionQuestion) {
        this.positionQuestion = positionQuestion;
    }

    public ArrayList<String> getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(ArrayList<String> copyRight) {
        this.copyRight = copyRight;
    }

    public String getPositionLetterResult() {
        return positionLetterResult;
    }

    public void setPositionLetterResult(String positionLetterResult) {
        this.positionLetterResult = positionLetterResult;
    }
}
