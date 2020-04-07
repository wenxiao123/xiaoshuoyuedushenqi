package com.example.administrator.xiaoshuoyuedushenqi.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CacheService extends Service {
    //为日志工具设置标签
    private static String TAG = "CacheService";
    int z = 1;
    int weigh;
    String bookname,bookcover,id;
    String path;
    DatabaseManager mDbManager;
    private MyBinder mBinder = new MyBinder();
    //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
    @Override
    public void onCreate() {

        super.onCreate();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                //tv_book_add.setText("移除书架");
//                if(mDbManager.isExistInBookshelfNovel(id+"")||mDbManager.isExistInBookshelfNovel(path+".txt")) {
                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(id+"",bookname,bookcover,1,weigh,1+"");
                    bookshelfNovelDbData.setFuben_id(path+".txt");
                    bookshelfNovelDbData.setChapterid(1+"");
                    mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
//                    mDbManager.updataBookshelfNovel(bookshelfNovelDbData, id+"");
//                }else {
//                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path+".txt",bookname,bookcover,1,weigh,1+"");
//                    bookshelfNovelDbData.setFuben_id(id+"");
//                    bookshelfNovelDbData.setChapterid(1+"");
//                    mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
//                }
                Intent intent_recever = new Intent("com.zhh.android");
                sendBroadcast(intent_recever);
                stopSelf();
            }else if(msg.what==2){
                int j=msg.arg1;
                f=((z-1)*30+(j+1));
                //Log.e("AA2", "handleMessage: "+((z-1)*30+(j+1)));
                if(30*z<=weigh&&(j+1)==30){
                    z++;
                    postBooks_che();
                }
                float pressent = (float) (((z-1)*30+(j+1))) / (weigh) * 100;
                //tv_che.setText("正在缓存:" + (int) pressent + "%");
                if((int) pressent<100){
                    CacheService.this.callback.onDataChange("正在缓存:" + (int) pressent + "%");
                }else {
                    CacheService.this.callback.onDataChange("已缓存" );
                }

                if(((z-1)*30+(j+1))==weigh){
                    handler.sendEmptyMessage(1);
                }
            }
        }
    };
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{0,9})[章节卷集部篇回](\\s*)";
   int f;
   public void postBooks_che() {
        String url = UrlObtainer.GetUrl() + "api/index/Books_che";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id+"")
                .add("page", z + "")
                .add("limit", 30 + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String content = jsonArray.getJSONObject(i).getString("content");
                            String load_title=title.replace("&nbsp"," ").replace("</br>","\n");
                            String load_content=content.replace("&nbsp"," ").replace("</br>","\n");
                            if(!load_title.contains("第")||!load_title.startsWith("第")){
                                //String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
                                String s = Pattern.compile(ChapterPatternStr).matcher(title).replaceAll("");
                                String title2 = "";
                                if(load_title.contains(s)) {
                                    title2=load_title.replace(s, "第" + s + "章 ");
                                  //  Log.e("QQQ", "onResponse: " + s + "***" + title2);
                                }
//                                }
                                addTxtToFileBuffered( title2+ "\n");
                                addTxtToFileBuffered( load_content+ "\n");
                            }else {
                                addTxtToFileBuffered( load_title+ "\n");
                                addTxtToFileBuffered( load_content+ "\n");
                            }
//                            addTxtToFileBuffered(title.replace("&nbsp"," ").replace("</br>","\n") + "\n");
//                            addTxtToFileBuffered(content.replace("&nbsp"," ").replace("</br>","\n") + "\n");
                            Message message = new Message();
                            message.what = 2;
                            message.arg1 = i;
                            handler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtil.showToast(getApplicationContext(),errorMsg);
            }
        });
    }
    /**
     * 使用BufferedWriter进行文本内容的追加
     *
     * @param content
     */
    private void addTxtToFileBuffered(String content) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            File saveFile = new File(path + ".txt");
            if (!saveFile.exists()) {
                File dir = new File(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile, true), "gbk"));
            out.newLine();//换行
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("aa2", "onDestroy: "+112);
    }

    //其他对象通过bindService 方法通知该Service时该方法被调用
    @Override
    public IBinder onBind(Intent intent) {
        bookname = intent.getStringExtra("bookname");
        id = intent.getStringExtra("id");
        bookcover = intent.getStringExtra("bookcover");
        weigh = intent.getIntExtra("weigh", 1);
        path = Constant.BOOK_ADRESS + "/" + bookname;
        mDbManager=DatabaseManager.getInstance();
        postBooks_che();
        return new MyBinder();
    }

    //其它对象通过unbindService方法通知该Service时该方法被调用
    @Override
    public boolean onUnbind(Intent intent) {
        //postBooks_che();
        return super.onUnbind(intent);
    }
    public String data = "下载中...";

    public class MyBinder extends Binder {
        public void  setData(String data) {
            CacheService.this.data = data;
        }

        public CacheService getService() {
            return CacheService.this;
        }
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onDataChange(String data);
    }

    private Callback callback;

}