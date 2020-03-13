package com.example.administrator.xiaoshuoyuedushenqi.util;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author WX
 * Created on 2019/12/11
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 将 Uri 转换为 file path
     */
    public static String uri2FilePath(Activity activity, Uri uri) {
        String filePath = null;
//       // String[] proj = {MediaStore.Images.Media.DATA};
//        String[] proj = {MediaStore.Files.FileColumns.DATA};
//        Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
//        if (actualimagecursor == null) {
//            filePath = uri.getPath();
//        } else {
//            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            filePath = actualimagecursor.getString(actual_image_column_index);
//        }
        String[] pros = {MediaStore.Files.FileColumns.DATA};
        try {
            Cursor cursor = activity.managedQuery(uri, pros, null, null, null);
            int actual_txt_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(actual_txt_column_index);
        } catch (Exception e) {
            Toast.makeText(activity, "选择出错了", Toast.LENGTH_SHORT).show();
        }
        return filePath;
    }

    /**
     * 获取文件长度，以 M 为单位
     */
    public static double getFileSize(File file){
        long len = file.length();
        return (double) len / Math.pow(2, 20);
    }

    /**
     * 获取文件长度，以 M 为单位
     */
    public static double getFileSize(long len){
        return (double) len / Math.pow(2, 20);
    }

    /**
     * 通过图片的 filePath 加载本地图片
     */
    public static Bitmap loadLocalPicture(String filePath) {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将字符串写入到 /storage/emulated/0/1/data.txt，测试用
     */
    public static void writeTxtToLocal(String content) {
        //生成文件夹之后，再生成文件，不然会出错
        String filePath = "/storage/emulated/0/1/";
        String fileName = "data.txt";
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = content + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 获取本地缓存文件的大小
     */
    public static String getLocalCacheSize() {
        File file = new File(Constant.EPUB_SAVE_PATH);
        double len = getFileSize(getTotalSizeOfFiles(file));

        return String.valueOf((int)(len)) + "M";
    }

    // 递归方式 计算文件的大小
    private static long getTotalSizeOfFiles(File file) {
        if (file.isFile())
            return file.length();
        File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (File child : children)
                total += getTotalSizeOfFiles(child);
        return total;
    }

    /**
     * 清除本地缓存
     */
    public static void clearLocalCache() {
        File file = new File(Constant.EPUB_SAVE_PATH);
        deleteFile(file);
    }

    /**
     * 删除文件夹或文件
     */
    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            return;
        }
        // 删除文件
        if (file.isFile()) {
            file.delete();
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            deleteFile(f);
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
}
