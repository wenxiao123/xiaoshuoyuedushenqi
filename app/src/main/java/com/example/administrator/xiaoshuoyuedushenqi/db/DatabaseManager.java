package com.example.administrator.xiaoshuoyuedushenqi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Website;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Created on 2019/11/11
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static DatabaseManager mManager;
    private SQLiteDatabase mDb;

    private DatabaseManager() {
        SQLiteOpenHelper helper = new DatabaseHelper(
                App.getContext(), Constant.DB_NAME, null, 1);
        mDb = helper.getWritableDatabase();
  //      String path = Constant.FONT_ADRESS;
//        File pathFile = new File(path);
//        File file = new File(path+"/read.db");
//        try{
//            if(!pathFile.exists()){
//                pathFile.mkdirs();
//            }
//            if(!file.exists()){
//                file.createNewFile();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mDb = SQLiteDatabase.openOrCreateDatabase(file,null);
    }

    public static DatabaseManager getInstance() {
        if (mManager == null) {
            mManager = new DatabaseManager();
        }
        return mManager;
    }

    /**
     * 插入一条新的历史记录
     */
    public void insertHistory(String content) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_HISTORY_WORD, content);
        mDb.insert(Constant.TABLE_HISTORY, null, values);
    }

    /**
     * 删除一条历史记录
     */
    public void deleteHistory(String content) {
        mDb.delete(Constant.TABLE_HISTORY,
                Constant.TABLE_HISTORY_WORD + " = ?", new String[]{content});
    }

    /**
     * 插入一条新的历史记录
     */
    public void insertWebsite(Website website) {
        ContentValues values = new ContentValues();
        values.put(Constant.WEBSITE_URL, website.getUrl());
        values.put(Constant.WEBSITE_TYPE, website.getType());
        mDb.insert(Constant.TABLE_WEBSITE, null, values);
    }

    /**
     * 删除一条历史记录
     */
    public void deleteWebsite() {
        mDb.delete(Constant.TABLE_WEBSITE, null, null);
    }

    /**
     * 查询所有历史记录（较新的记录排前面）
     */
    public List<String> queryAllHistory() {
        List<String> res = new ArrayList<>();
        Cursor cursor = mDb.query(Constant.TABLE_HISTORY, null, null,
                null, null, null, null);
        if (cursor.moveToLast()) {
            do {
                res.add(cursor.getString(cursor.getColumnIndex(Constant.TABLE_HISTORY_WORD)));
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有历史记录（较新的记录排前面）
     */
    public List<Website> queryAllWebsite() {
        List<Website> res = new ArrayList<>();
        Cursor cursor = mDb.query(Constant.TABLE_WEBSITE, null, null,
                null, null, null, null);
        if (cursor.moveToLast()) {
            do {
                String href=cursor.getString(cursor.getColumnIndex(Constant.WEBSITE_URL));
                int type=cursor.getInt(cursor.getColumnIndex(Constant.WEBSITE_TYPE));
                //Log.e("WWW2", "getWebsiteSuccess: "+href);
                res.add(new Website(href,type));
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 删除前 n 条历史记录
     */
    public void deleteHistories(int n) {
        String sql = "delete from " + Constant.TABLE_HISTORY +
                " where " + Constant.TABLE_HISTORY_ID + " in(" +
                "select " + Constant.TABLE_HISTORY_ID + " from " + Constant.TABLE_HISTORY +
                " order by " + Constant.TABLE_HISTORY_ID +
                " limit " + n + ")";
        mDb.execSQL(sql);
    }

    /**
     * 删除前 n 条历史记录
     */
    public void deleteReadCoder() {
//        String sql = "delete from " + Constant.TABLE_READCORDE_NOVEL + ";";
//        mDb.execSQL(sql);
        mDb.delete(Constant.TABLE_READCORDE_NOVEL, null, null);
    }

    /**
     * 删除所有历史记录
     */
    public void deleteAllHistories() {
        mDb.delete(Constant.TABLE_HISTORY, null, null);
    }

    /**
     * 插入一条书架书籍数据
     */
    //String novelUrl, String name, String cover, int position, int type, int secondPosition, String chapterid, int weight, String status
    public void insertBookshelfNovel(BookshelfNovelDbData dbData) {
        //Log.e("QQQ1", "updataBookshelfNovel: "+dbData);
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl().trim());//
        if (dbData.getWeight() != 0) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeight());
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());//
        if (dbData.getFuben_id() != null&&!dbData.getFuben_id().contains(".txt")) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID, dbData.getFuben_id());//
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());//
        values.put(Constant.TABLE_BOOKSHELF_STATUS, dbData.getStatus());//
        mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
    }
    public boolean insertOrUpdateBook(BookshelfNovelDbData dbData){
        Cursor cursor = mDb.query(
                Constant.TABLE_BOOKSHELF_NOVEL,
                new String[]{Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL},
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{dbData.getNovelUrl() + ""},
                null,
                null,
                null);

        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());//
        if (dbData.getWeight() != 0) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeight());
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());//
        if (dbData.getFuben_id() != null) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID, dbData.getFuben_id());//
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());//
        values.put(Constant.TABLE_BOOKSHELF_STATUS, dbData.getStatus());//
        long result = 0;
        try {
            if (cursor != null && cursor.getCount() > 0) {
               // Log.e("QQQ", "insertOrUpdateBook: "+"更新");
                //更新操作
                result = mDb.update(Constant.TABLE_BOOKSHELF_NOVEL, values, Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?", new String[]{dbData.getNovelUrl() + ""});
            }else{
               // Log.e("QQQ", "insertOrUpdateBook: "+"新增");
                //插入操作
                result = mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
            }
        } catch (Exception e) {

        } finally{
            if (null != cursor) {
                cursor.close();
            }
            cursor.close();
        }

        return result > 0;

    }


    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void insertBookshelfNovel1(BookshelfNovelDbData dbData) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeight());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());//
        if (!dbData.getNovelUrl().contains(".txt")) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID, dbData.getNovelUrl());
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());//
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());//
        values.put(Constant.TABLE_BOOKSHELF_STATUS, dbData.getStatus());//
        mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
    }

    /**
     * 插入一条阅读记录书籍数据
     */
    public void insertReadCordeNovel(Noval_Readcored dbData, String type) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovel_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeigh());
        values.put(Constant.TABLE_BOOKSHELF_STATUS, dbData.getStatus());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapter_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getTitle());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getPic());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, type);
        values.put(Constant.TABLE_BOOKSHELF_AUTHOR, dbData.getAuthor());
        values.put(Constant.TABLE_BOOKSHELF_CHAPTER_NAME, dbData.getChapter_name());
        values.put(Constant.TABLE_BOOKSHELF_IS_CHE, dbData.getIs_che());
        mDb.insert(Constant.TABLE_READCORDE_NOVEL, null, values);
    }

    /**
     * 更新一条阅读记录书籍数据
     */
    public void updataReadCordeNovel(Noval_Readcored dbData, String type, String novelUrl) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovel_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeigh());
        values.put(Constant.TABLE_BOOKSHELF_STATUS, dbData.getStatus());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapter_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getTitle());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getPic());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, type);
        values.put(Constant.TABLE_BOOKSHELF_AUTHOR, dbData.getAuthor());
        values.put(Constant.TABLE_BOOKSHELF_CHAPTER_NAME, dbData.getChapter_name());
        values.put(Constant.TABLE_BOOKSHELF_IS_CHE, dbData.getIs_che());
        //mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
        mDb.update(Constant.TABLE_READCORDE_NOVEL, values,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{novelUrl});
    }

    /**
     * 插入一条书架书籍数据
     */
    public void updataBookshelfNovel(BookshelfNovelDbData dbData, String id) {
        //Log.e("QQQ2", "updataBookshelfNovel: "+dbData);
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());
        if (dbData.getFuben_id() != null&&!dbData.getFuben_id().contains(".txt")) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID, dbData.getFuben_id());//
        }
        if (dbData.getWeight() != 0) {
            values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH, dbData.getWeight());
        }
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());
        mDb.update(Constant.TABLE_BOOKSHELF_NOVEL, values,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{id});
    }

    /**
     * 插入一条书签书籍数据
     */
    public void insertBookmarkNovel(BookmarkNovelDbData dbData) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CONTENT, dbData.getContent());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());
        values.put(Constant.BOOKSHELF_TIME, dbData.getTime());
        mDb.insert(Constant.TABLE_BOOKMARK_NOVEL, null, values);
    }

    /**
     * 查询所有书架书籍信息
     */
    public List<BookshelfNovelDbData> queryAllBookshelfNovel() {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null, null, null,
                null, null, null);
        List<BookshelfNovelDbData> res = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String cover = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_COVER));
                String chperid = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                String title1 = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID));
                String status = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_STATUS));
                int position = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_POSITION));
                int type = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_TYPE));
                int weight = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_WIGH));
                int secondPosition = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION));
                BookshelfNovelDbData bookshelfNovelDbData=new BookshelfNovelDbData(novelUrl, name, cover,
                        position, type, secondPosition, chperid, weight, status);
                bookshelfNovelDbData.setFuben_id(title1);
                res.add(bookshelfNovelDbData);
           // Log.e("QQQ", "isBookmarkNovel: "+bookshelfNovelDbData);
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有书架书籍信息
     */
    public BookshelfNovelDbData getBookshelfNovel(String url) {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null, null, null,
                null, null, null);
        BookshelfNovelDbData res = null;
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String fuben = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID));
                String name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String cover = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_COVER));
                String chperid = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                String status = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_STATUS));
                int position = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_POSITION));
                int type = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_TYPE));
                int weight = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_WIGH));
                int secondPosition = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION));
                if (url.equals(novelUrl)) {
                    res = new BookshelfNovelDbData(novelUrl, name, cover,
                            position, type, secondPosition, chperid, weight, status);
                    res.setFuben_id(fuben);
                    break;
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有书架书籍信息
     */
    public BookshelfNovelDbData selectBookshelfNovel(String url) {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null, null, null,
                null, null, null);
        BookshelfNovelDbData res = null;
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String cover = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_COVER));
                String fuben = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_FUBEN_ID));
                String chperid = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                String status = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_STATUS));
                int position = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_POSITION));
                int type = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_TYPE));
                int weight = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_WIGH));
                int secondPosition = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION));
                if (novelUrl.equals(url)) {
                    res = new BookshelfNovelDbData(novelUrl, name, cover,
                            position, type, secondPosition, chperid, weight, status);
                    res.setFuben_id(fuben);
                   // Log.e("WWW", "selectBookshelfNovel: "+fuben);
                    break;
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有书架书籍信息
     */
    public List<Noval_Readcored> queryAllReadcordefNovel() {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_READCORDE_NOVEL, null, null, null,
                null, null, null);
        List<Noval_Readcored> res = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String chapter_id = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                String status = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_STATUS));
                String title = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String author = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_AUTHOR));
                String pic = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_COVER));
                String is_che = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_IS_CHE));
                String chapter_name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_CHAPTER_NAME));
                String weigh = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_WIGH));
                res.add(new Noval_Readcored(novelUrl, chapter_id, status,
                        title, author, pic, is_che, chapter_name, weigh));
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有书架书签信息
     */
    public List<BookmarkNovelDbData> queryAllBookmarkNovel(String bookname) {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_BOOKMARK_NOVEL, null, null, null,
                null, null, null);
        List<BookmarkNovelDbData> res = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String cover = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_CONTENT));
                float chapterIndex = cursor.getFloat(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX));
                int position = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_POSITION));
                int type = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_TYPE));
                String secondPosition = cursor.getString(
                        cursor.getColumnIndex(Constant.BOOKSHELF_TIME));
                String chpter_id = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                if (novelUrl.equals(bookname)) {
                    res.add(new BookmarkNovelDbData(novelUrl, name, cover,
                            chapterIndex, position, type, secondPosition, chpter_id));
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询所有书架书签信息
     */
    public  List<BookmarkNovelDbData> isBookmarkNovel(String bookname) {
        // 查询表中所有数据
        Cursor cursor = mDb.query(Constant.TABLE_BOOKMARK_NOVEL, null, null, null,
                null, null, null);
        List<BookmarkNovelDbData> res = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                String novelUrl = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL));
                String name = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_NAME));
                String cover = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_CONTENT));
                float chapterIndex = cursor.getFloat(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX));
                int position = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_POSITION));
                int type = cursor.getInt(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_TYPE));
                String secondPosition = cursor.getString(
                        cursor.getColumnIndex(Constant.BOOKSHELF_TIME));
                String chpter_id = cursor.getString(
                        cursor.getColumnIndex(Constant.TABLE_BOOKSHELF_NOVEL_ID));
                if (novelUrl.equals(bookname)) {
                    res.add(new BookmarkNovelDbData(novelUrl, name, cover,
                            chapterIndex, position, type, secondPosition, chpter_id));
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 根据小说 url 删除一条书架书籍数据集
     */
    public void deleteBookReadcoderNovel(String novelUrl) {
        mDb.delete(Constant.TABLE_READCORDE_NOVEL,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{novelUrl});
    }

    /**
     * 根据小说 url 删除一条书架书籍数据集
     */
    public void deleteBookshelfNovel(String novelUrl) {
        mDb.delete(Constant.TABLE_BOOKSHELF_NOVEL,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{novelUrl});
    }


    /**
     * 根据书签 url 删除一条书架书籍数据集
     */
    public void deleteBookmarkNovel(String novelUrl) {
        mDb.delete(Constant.TABLE_BOOKMARK_NOVEL,
                Constant.BOOKSHELF_TIME + " = ?",
                new String[]{novelUrl});
    }

    /**
     * 查询 Bookshelf 表是否存在主键为 novelUrl 的记录
     */
    public boolean isExistInBookshelfNovel(String novelUrl) {
        if(TextUtils.isEmpty(novelUrl)){
            return false;
        }
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?", new String[]{novelUrl},
                null, null, null, null);
        boolean res = false;
        if (cursor.moveToLast()) {
            do {
                res = true;
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    public boolean isExistInBookshelfNovelname(String name) {
        if(TextUtils.isEmpty(name)){
            return false;
        }
        Log.e("QQQ", "isExistInBookshelfNovelname: "+name);
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null,
                Constant.TABLE_BOOKSHELF_NOVEL_NAME + " = ?", new String[]{name},
                null, null, null, null);
        boolean res = false;
        if (cursor.moveToLast()) {
            do {
                res = true;
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }

    /**
     * 查询 Bookshelf 表是否存在主键为 novelUrl 的记录
     */
    public boolean isExistInReadCoderNovel(String novelUrl) {
        if(TextUtils.isEmpty(novelUrl)){
            return false;
        }
        Cursor cursor = mDb.query(Constant.TABLE_READCORDE_NOVEL, null,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?", new String[]{novelUrl},
                null, null, null, null);
        boolean res = false;
        if (cursor.moveToLast()) {
            do {
                res = true;
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return res;
    }
}
