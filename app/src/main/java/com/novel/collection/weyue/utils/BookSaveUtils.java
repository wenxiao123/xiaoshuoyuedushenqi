package com.novel.collection.weyue.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Liang_Lu on 2017/12/12.
 * 保存书籍工具类
 */

public class BookSaveUtils {
    private static volatile BookSaveUtils sInstance;

    public static BookSaveUtils getInstance(){
        if (sInstance == null){
            synchronized (BookSaveUtils.class){
                if (sInstance == null){
                    sInstance = new BookSaveUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        File file = BookManager.getBookFile(folderName, fileName);
        //Log.e("QQQ", "convertTxtChapter: "+file.getPath());
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            String str=content.replace("</br>","\r\n");
            //Log.e("WWW", "saveChapterInfo: "+str);
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    /**
     * 存储当前章节
     *
     * @param folderName
     * @param content
     */
    public void saveNowChapterInfo(String folderName,  String content) {
        File file_parent = new File(Constant.BOOK_OTHER_CACHE_PATH);
        if(!file_parent.exists()){
            file_parent.mkdirs();
        }
        File file = BookManager.getBook(folderName);
        if(!file.exists()){
            file.mkdirs();
        }
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            String str=content.replace("</br>","\r\n");
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    public void saveNowChapterInfo2(String folderName, String content) {
        File file_parent = new File(Constant.BOOK_OTHER_CACHE_PATH);
        if(!file_parent.exists()){
            file_parent.mkdirs();
        }
        File file = BookManager.getBook2(folderName);
        if(!file.exists()){
            file.mkdirs();
        }
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            //String str=content.replace("</p>","\r\n");
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    public void saveChapterInfo2(String folderName, String fileName, String content) {
        File file1=new File(Constant.BOOK_OTHER_CACHE_PATH);
        if(!file1.isDirectory()){
            file1.delete();
        }
        File file = BookManager.getBookFile2(folderName, fileName);
       // LogUtils.e(file.getAbsolutePath()+" "+file.exists());
       // Log.e("QQQ", "convertTxtChapter: "+file.getPath());
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            //String str=content.replace("</p>","\r\n");
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

}
