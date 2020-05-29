package com.novel.collection.util;

import android.os.Environment;

import com.novel.collection.constant.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDowned {
    static   String  path = Constant.FONT_ADRESS+"/Font/";
    /**
     * 此文件支持下载docx,pdf,xls,jpg,
     * @param urlStr
     * @param fileName
     * @return
     *         -1:文件下载出错
     *         0:文件下载成功
     *         1:文件已经存在
     */
    public static int downFile(String urlStr,  String fileName) {
        InputStream inputStream = null;
        HttpURLConnection urlConn = null;
        try {
            // 判断文件是否存在
            if (new File(path + fileName).exists()) {
                return 1;
            } else {
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                // 设置请求方式为"GET"
                urlConn.setRequestMethod("GET");
                // 超时响应时间为5秒
                urlConn.setConnectTimeout(5 * 1000);

                // 得到io输入流，即从url读取到的数据
                inputStream = urlConn.getInputStream();
                // 从input流中将文件写入SD卡中
                File resultFile = write2SDFromInput(path, fileName, inputStream);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param inputStream
     * @return
     */
    public static File write2SDFromInput(String path, String fileName, InputStream inputStream) {
        File file = null;
        OutputStream outputStream = null;
        try {
            // 创建文件，父目录若无，则会去先创建
            file = new File(path + fileName);
            if(!file.exists()){
                file.mkdirs();
            }
            // 开启输出流，准备写入文件
            outputStream = new FileOutputStream(file);
            // 缓冲区
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            // 使用一个输入流从buffer里把数据读取出来
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // outputStream.write(buffer);
                // 每次读多少，写多少进去,如果没有加bytesRead,则会出现随机将每一行后面的空数据也写入，造成一些文件格式的损坏和文件大小增大。
                outputStream.write(buffer, 0, bytesRead);
            }
            // 关闭输入流
            inputStream.close();
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
