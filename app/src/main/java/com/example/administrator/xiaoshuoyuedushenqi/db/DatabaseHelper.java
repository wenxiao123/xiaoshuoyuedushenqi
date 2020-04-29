package com.example.administrator.xiaoshuoyuedushenqi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;

/**
 * @author
 * Created on 2019/11/11
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "fzh";

    // 创建历史记录表
    private static final String CREATE_TABLE_HISTORY = "create table " + Constant.TABLE_HISTORY
            + " (" + Constant.TABLE_HISTORY_ID + " integer primary key autoincrement, "
            + Constant.TABLE_HISTORY_WORD + " text)";

    // 创建历史记录表
    private static final String CREATE_TABLE_WEBSITE = "create table " + Constant.TABLE_WEBSITE
            + " (" + Constant.TABLE_HISTORY_ID + " integer primary key autoincrement, "
            + Constant.WEBSITE_TYPE + " text , "
            + Constant.WEBSITE_URL + " text)";

    // 创建书架书籍信息表
    private static final String CREATE_TABLE_BOOKSHELF_NOVEL = "create table " + Constant.TABLE_BOOKSHELF_NOVEL
            + " (" + Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " text primary key, "
            + Constant.TABLE_BOOKSHELF_NOVEL_NAME + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_ID + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_WIGH +" text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_COVER + " text, "
            + Constant.TABLE_BOOKSHELF_STATUS +" text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX + " int, "
            + Constant.TABLE_BOOKSHELF_NOVEL_TYPE + " int, "
            + Constant.TABLE_BOOKSHELF_NOVEL_POSITION + " int, "
            + Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION + " int)";

    private static final String CREATE_TABLE_COLALOG_NOVEL = "create table " + Constant.TABLE_COLALOG_NOVEL
            + " ( id int primary key, "
            + " title text , "
            + " weigh integer , "
            + " novalid text , "
            + " reurl text )";
    // 创建书架书签信息表
    private static final String CREATE_TABLE_BOOKMARK_NOVEL = "create table " + Constant.TABLE_BOOKMARK_NOVEL
            + " ("  + Constant.TABLE_BOOKMARK_ID + " integer primary key autoincrement, "
            + Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " text, "
            + Constant.TABLE_BOOKSHELF_NOVEL_ID + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_NAME + " text, "
            + Constant.TABLE_BOOKSHELF_NOVEL_CONTENT + " text, "
            + Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX + " float, "
            + Constant.TABLE_BOOKSHELF_NOVEL_TYPE + " int, "
            + Constant.TABLE_BOOKSHELF_NOVEL_POSITION + " int, "
            + Constant.BOOKSHELF_TIME + " text)";

    // 创建书架书籍信息表
    private static final String CREATE_TABLE_READCORDE = "create table " + Constant.TABLE_READCORDE_NOVEL
            + " (" + Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " text primary key, "
            + Constant.TABLE_BOOKSHELF_NOVEL_NAME + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_ID + " text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_WIGH +" text , " //
            + Constant.TABLE_BOOKSHELF_IS_CHE +" text , "
            + Constant.TABLE_BOOKSHELF_STATUS +" text , "
            + Constant.TABLE_BOOKSHELF_CHAPTER_NAME +" text , "
            + Constant.TABLE_BOOKSHELF_AUTHOR +" text , "
            + Constant.TABLE_BOOKSHELF_NOVEL_COVER + " text, "
            + Constant.TABLE_BOOKSHELF_NOVEL_TYPE + " int, "
            + Constant.TABLE_BOOKSHELF_NOVEL_POSITION + " int)";

    DatabaseHelper(Context context, String name,
                   SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COLALOG_NOVEL);
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CREATE_TABLE_WEBSITE);
        db.execSQL(CREATE_TABLE_BOOKSHELF_NOVEL);
        db.execSQL(CREATE_TABLE_BOOKMARK_NOVEL);
        db.execSQL(CREATE_TABLE_READCORDE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     if(oldVersion==1){
         db.execSQL("drop table if exists "+Constant.TABLE_COLALOG_NOVEL);
         db.execSQL("drop table if exists "+Constant.TABLE_HISTORY);
         db.execSQL("drop table if exists "+Constant.TABLE_WEBSITE);
         db.execSQL("drop table if exists "+Constant.TABLE_BOOKSHELF_NOVEL);
         db.execSQL("drop table if exists "+Constant.TABLE_BOOKMARK_NOVEL);
         db.execSQL("drop table if exists "+Constant.TABLE_READCORDE_NOVEL);
         onCreate(db);

     }
    }
}
