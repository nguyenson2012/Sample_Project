package com.example.asus.catchpictureproject.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asus.catchpictureproject.model.Word4Pic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asus on 6/22/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "picture.db";
    public static final String PICTURE_TABLE="table_picture";
    public static final String POSITION_QUESTION="position_question";
    public static final String IMG_LINK_ONE="img_link_one";
    public static final String IMG_LINK_TWO="img_link_two";
    public static final String IMG_LINK_THREE="img_link_three";
    public static final String IMG_LINK_FOUR="img_link_four";
    public static final String IMG_COPYRIGHT_ONE="img_copyright_one";
    public static final String IMG_COPYRIGHT_TWO="img_copyright_two";
    public static final String IMG_COPYRIGHT_THREE="img_copyright_three";
    public static final String IMG_COPYRIGHT_FOUR="img_copyright_four";
    public static final String RESULT="result";
    public static final String POSITION_RESULT="position_result";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table table_picture (" +
                POSITION_QUESTION + " integer primary key, " + IMG_LINK_ONE + " text, " + IMG_LINK_TWO + " text, " +
                IMG_LINK_THREE + " text, " + IMG_LINK_FOUR + " text, " + IMG_COPYRIGHT_ONE + " text, " + IMG_COPYRIGHT_TWO + " text, " +
                IMG_COPYRIGHT_THREE + " text, " + IMG_COPYRIGHT_FOUR + " text, " + RESULT + " text, " + POSITION_RESULT + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS table_picture");
        onCreate(db);
    }

    public boolean insertWord4pic(Word4Pic word4Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POSITION_QUESTION, word4Pic.getPositionQuestion());
        contentValues.put(IMG_LINK_ONE, word4Pic.getListImageLink().get(0));
        contentValues.put(IMG_LINK_TWO, word4Pic.getListImageLink().get(1));
        contentValues.put(IMG_LINK_THREE, word4Pic.getListImageLink().get(2));
        contentValues.put(IMG_LINK_FOUR, word4Pic.getListImageLink().get(3));
        contentValues.put(IMG_COPYRIGHT_ONE, word4Pic.getCopyRight().get(0));
        contentValues.put(IMG_COPYRIGHT_TWO, word4Pic.getCopyRight().get(1));
        contentValues.put(IMG_COPYRIGHT_THREE, word4Pic.getCopyRight().get(2));
        contentValues.put(IMG_COPYRIGHT_FOUR, word4Pic.getCopyRight().get(3));
        contentValues.put(RESULT, word4Pic.getResult());
        contentValues.put(POSITION_RESULT, word4Pic.getPositionLetterResult());
        long rowEffect=db.insert(PICTURE_TABLE, null, contentValues);
        if(rowEffect==1)
            return true;
        else
            return false;
    }

    public boolean isExistQuestion(int positionQuestion){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] column={"position_question"};
        Cursor findEntry = db.query(PICTURE_TABLE, column, "position_question=?", new String[] { Integer.toString(positionQuestion) }, null, null, null);
        if(findEntry.isAfterLast()==false)
            return true;
        else
            return false;
    }

    public boolean updateWord4Pic(Word4Pic word4Pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMG_LINK_ONE, word4Pic.getListImageLink().get(0));
        contentValues.put(IMG_LINK_TWO, word4Pic.getListImageLink().get(1));
        contentValues.put(IMG_LINK_THREE, word4Pic.getListImageLink().get(2));
        contentValues.put(IMG_LINK_FOUR, word4Pic.getListImageLink().get(3));
        contentValues.put(IMG_COPYRIGHT_ONE, word4Pic.getCopyRight().get(0));
        contentValues.put(IMG_COPYRIGHT_TWO, word4Pic.getCopyRight().get(1));
        contentValues.put(IMG_COPYRIGHT_THREE, word4Pic.getCopyRight().get(2));
        contentValues.put(IMG_COPYRIGHT_FOUR, word4Pic.getCopyRight().get(3));
        contentValues.put(RESULT, word4Pic.getResult());
        contentValues.put(POSITION_RESULT, word4Pic.getPositionLetterResult());
        if(db.update(PICTURE_TABLE, contentValues, POSITION_QUESTION + "=?", new String[]{Integer.toString(word4Pic.getPositionQuestion())})==1)
            return true;
        else
            return false;
    }

    public Word4Pic getPicture(int positionPicture){
        Word4Pic word4Pic=new Word4Pic();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from table_picture", null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            int position=res.getInt(res.getColumnIndex(POSITION_QUESTION));
            if(position==positionPicture){
                word4Pic.setPositionQuestion(position);
                ArrayList<String> listImgLink=new ArrayList<String>();
                String linkImg1=res.getString(res.getColumnIndex(IMG_LINK_ONE));
                listImgLink.add(linkImg1);
                String linkImg2=res.getString(res.getColumnIndex(IMG_LINK_TWO));
                listImgLink.add(linkImg2);
                String linkImg3=res.getString(res.getColumnIndex(IMG_LINK_THREE));
                listImgLink.add(linkImg3);
                String linkImg4=res.getString(res.getColumnIndex(IMG_LINK_FOUR));
                listImgLink.add(linkImg4);
                word4Pic.setListImageLink(listImgLink);
                ArrayList<String> listImgLinkCopyright=new ArrayList<String>();
                String linkImgcopyright1=res.getString(res.getColumnIndex(IMG_COPYRIGHT_ONE));
                listImgLinkCopyright.add(linkImgcopyright1);
                String linkImgcopyright2=res.getString(res.getColumnIndex(IMG_COPYRIGHT_TWO));
                listImgLinkCopyright.add(linkImgcopyright2);
                String linkImgcopyright3=res.getString(res.getColumnIndex(IMG_COPYRIGHT_THREE));
                listImgLinkCopyright.add(linkImgcopyright3);
                String linkImgcopyright4=res.getString(res.getColumnIndex(IMG_COPYRIGHT_FOUR));
                listImgLinkCopyright.add(linkImgcopyright4);
                word4Pic.setCopyRight(listImgLinkCopyright);
                String result=res.getString(res.getColumnIndex(RESULT));
                word4Pic.setResult(result);
                String positionResult=res.getString(res.getColumnIndex(POSITION_RESULT));
                word4Pic.setPositionLetterResult(positionResult);
            }
            res.moveToNext();
        }
        return word4Pic;
    }

}

