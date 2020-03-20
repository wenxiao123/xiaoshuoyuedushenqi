package com.example.administrator.xiaoshuoyuedushenqi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/11
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static DatabaseManager mManager;
    private SQLiteDatabase mDb;

    private DatabaseManager() {
        SQLiteOpenHelper helper = new DatabaseHelper(
                App.getContext(), Constant.DB_NAME, null, 1);
        mDb = helper.getWritableDatabase();
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
     * 查询所有历史记录（较新的记录排前面）
     */
    public List<String> queryAllHistory() {
        List<String> res = new ArrayList<>();
        Cursor cursor = mDb.query(Constant.TABLE_HISTORY, null, null,
                null, null, null,null);
        if (cursor.moveToLast()) {
            do {
                res.add(cursor.getString(cursor.getColumnIndex(Constant.TABLE_HISTORY_WORD)));
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
     * 删除所有历史记录
     */
    public void deleteAllHistories() {
        mDb.delete(Constant.TABLE_HISTORY, null, null);
    }

    /**
     * 插入一条书架书籍数据
     */
    public void insertBookshelfNovel(BookshelfNovelDbData dbData) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH,dbData.getWeight());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());
        mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
    }
    /**
     * 插入一条阅读记录书籍数据
     */
    public void insertReadCordeNovel(Noval_Readcored dbData,String type) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovel_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH,dbData.getWeigh());
        values.put(Constant.TABLE_BOOKSHELF_STATUS,dbData.getStatus());
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
    public void updataReadCordeNovel(Noval_Readcored dbData,String type,String novelUrl) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovel_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH,dbData.getWeigh());
        values.put(Constant.TABLE_BOOKSHELF_STATUS,dbData.getStatus());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapter_id());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getTitle());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getPic());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, type);
        values.put(Constant.TABLE_BOOKSHELF_AUTHOR, dbData.getAuthor());
        values.put(Constant.TABLE_BOOKSHELF_CHAPTER_NAME, dbData.getChapter_name());
        values.put(Constant.TABLE_BOOKSHELF_IS_CHE, dbData.getIs_che());
        //mDb.insert(Constant.TABLE_BOOKSHELF_NOVEL, null, values);
        mDb.update(Constant.TABLE_READCORDE_NOVEL,values,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{novelUrl});
    }
    /**
     * 插入一条书架书籍数据
     */
    public void updataBookshelfNovel(BookshelfNovelDbData dbData,String novelUrl) {
        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL, dbData.getNovelUrl());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_ID, dbData.getChapterid());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_WIGH,dbData.getWeight());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_NAME, dbData.getName());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_COVER, dbData.getCover());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX, dbData.getChapterIndex());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_POSITION, dbData.getPosition());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_TYPE, dbData.getType());
        values.put(Constant.TABLE_BOOKSHELF_NOVEL_SECOND_POSITION, dbData.getSecondPosition());
        mDb.update(Constant.TABLE_BOOKSHELF_NOVEL,values,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL + " = ?",
                new String[]{novelUrl});
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
                null, null ,null);
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
                res.add(new BookshelfNovelDbData(novelUrl, name, cover,
                             position, type, secondPosition,chperid,weight,status));
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
                null, null ,null);
        BookshelfNovelDbData res = null;
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
                if(novelUrl.equals(url)){
                    res= new BookshelfNovelDbData(novelUrl, name, cover,
                            position, type, secondPosition,chperid,weight,status);
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
                null, null ,null);
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
                        title, author, pic,is_che,chapter_name,weigh));
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
                null, null ,null);
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
                if(novelUrl.equals(bookname)) {
                res.add(new BookmarkNovelDbData(novelUrl, name, cover,
                        chapterIndex, position, type, secondPosition,chpter_id));
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
        Cursor cursor = mDb.query(Constant.TABLE_BOOKSHELF_NOVEL, null,
                Constant.TABLE_BOOKSHELF_NOVEL_NOVEL_URL+ " = ?", new String[]{novelUrl},
                null,null, null ,null);
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
