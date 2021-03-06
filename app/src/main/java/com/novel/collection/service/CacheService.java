package com.novel.collection.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.novel.collection.app.App;
import com.novel.collection.constant.Constant;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.DownBean;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.weyue.utils.BookSaveUtils;

import org.greenrobot.greendao.annotation.Id;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.IDN;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CacheService extends Service {
    //为日志工具设置标签
    private static String TAG = "CacheService";
    int z = 1;
    int weigh;
    int serialize;
    String bookname, bookcover, id;
    String path;
    DatabaseManager mDbManager;
    private MyBinder mBinder = new MyBinder();
    Login_admin login_admin;
    //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
    @Override
    public void onCreate() {
        super.onCreate();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(id + "", bookname, bookcover, 1, weigh, 1 + "");
                BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(id, bookname,
                        bookcover, 0, 1, 0, 0 + "", weigh, serialize + "");
                bookshelfNovelDbData.setFuben_id(path+".txt");
                mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
                if (login_admin != null) {
                    setBookshelfadd(login_admin.getToken(), id);
                }else {
                    setBookshelfadd("", id);
                }
//                Intent intent_recever = new Intent("com.zhh.android");
//                intent_recever.putExtra("type", 1);
//                sendBroadcast(intent_recever);
                for(int i=0;i<downBeanList.size();i++){
                  if(downBeanList.get(i).getId().equals(id)){
                      downBeanList.remove(downBeanList.get(i));
                  }
                }
                 if(downBeanList.size()>0){
                     DownBean downBean=downBeanList.get(0);
                     weigh=downBean.getWeight();
                     serialize=downBean.getSerialize();
                     bookname=downBean.getTitle();
                     bookcover=downBean.getPic();
                     id=downBean.getId();
                     z=1;
                     path = Constant.BOOK_ADRESS + "/" +id+"/" +bookname;
                     postBooks_che();
                 }else {
                     App.getInstance().setPosition(null);
                 }
            } else if (msg.what == 2) {
                int j = msg.arg1;
                f = ((z-1)*post_num+(j+1));
                Log.e("qqq", "handleMessage: "+f+" "+weigh);
                if (post_num * z <= weigh && (j + 1) == post_num) {
                    z++;
                    postBooks_che();
                }
                float pressent = (float) f/ (weigh) * 100;
                App.getInstance().setPosition(id);
                String progress="";
                if (((z-1)*post_num+(j+1)) < weigh) {
                    //CacheService.this.callback.onDataChange("正在缓存:" +(int)pressent + "%",id);
                    progress="缓存中:" +(int)pressent + "%";
                } else {
                    progress="已缓存";
                    handler.sendEmptyMessage(1);
                }
                Intent intent_recever = new Intent("com.load.android");
                intent_recever.putExtra("load_position", id);
                intent_recever.putExtra("load_progresss", progress);
                sendBroadcast(intent_recever);
            }
        }
    };
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{0,9})[章节卷集部篇回](\\s*)";
    int f;

    public void postBooks_che() {
        String url = UrlObtainer.GetUrl() + "/api/index/Books_che";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id + "")
                .add("page", z + "")
                .add("limit", post_num + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
              //  Log.e("QQQ", "onResponse: "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String title = jsonArray.getJSONObject(i).getString("title").replace("</br>", "\n");;
                            String content = jsonArray.getJSONObject(i).getString("content").replace("</br>", "\n");;
//                            String load_title = title.replace("&nbsp", " ").replace("</br>", "\n");
//                            String load_content = content.replace("&nbsp", " ").replace("</br>", "\n");
//                            if (!title.startsWith("第")) {
//                                String title2 = "";
//                                for(int k=0;k<title.length();k++){
//                                  String c= String.valueOf(title.charAt(k));
//                                  if(c.equals("\\s+")||c.equals(":")||c.equals("_")){
//                                      title2 = title.replace(c, "章 ");
//                                      break;
//                                  }
//                                }
//                                if (title2.equals("")) {
//                                    title2 = (z*post_num+i)+"章 "+title;
//                                }
//                                addTxtToFileBuffered("<"+title2+">"+"\n");
//                                addTxtToFileBuffered(content + "\n");
//                                Log.e("vvv", "onResponse: "+title2);
//                            } else {
//                            addTxtToFileBuffered("<"+title+">"+"\n");
//                            addTxtToFileBuffered(content + "\n");
                            BookSaveUtils.getInstance().saveChapterInfo(id, title.replace(" ", ""), content.replace("&nbsp", " "));
                           // }
                            Message message = new Message();
                            message.what = 2;
                            message.arg1 = i;
                            handler.sendMessage(message);
                        }
                        if(jsonArray.length()<post_num&&f!=weigh){
                            handler.sendEmptyMessage(1);
                            Intent intent_recever = new Intent("com.load.android");
                            intent_recever.putExtra("load_position", id);
                            intent_recever.putExtra("load_progresss", "已缓存");
                            sendBroadcast(intent_recever);
                        }
                        time_count = 0;
                    }
                } catch (JSONException e) {
                    if (time_count < Constant.TIME_MAX) {
                        postBooks_che();
                    } else {
                        ToastUtil.showToast(App.getContext(), "errorMsg");
                    }
                    time_count++;
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("QQQ", "onFailure: "+111);
                if (time_count < Constant.TIME_MAX) {
                    postBooks_che();
                } else {
                    ToastUtil.showToast(App.getContext(), errorMsg);
                }
                time_count++;
            }
        });
    }
    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "/api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
                //.add("token", token)
                .add("type", 2+"")
                .add("novel_id", novel_id)
                .add("chapter_id", 1+"")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQW", "onResponse: "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        //getNovelsError("请求错误");
                    }
                    Intent intent_recever = new Intent("com.zhh.android");
                    intent_recever.putExtra("type", 1);
                    sendBroadcast(intent_recever);
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                // getNovelsError("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }
    public static boolean containSpace(CharSequence input){
        return Pattern.compile("\\s+").matcher(input).find();
    }
    int time_count = 0;
    int post_num=5;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    //其他对象通过bindService 方法通知该Service时该方法被调用
    @Override
    public IBinder onBind(Intent intent) {
        mDbManager = DatabaseManager.getInstance();
        login_admin=(Login_admin) SpUtil.readObject(App.getAppContext());
        return new MyBinder();
    }

    //其它对象通过unbindService方法通知该Service时该方法被调用
    @Override
    public boolean onUnbind(Intent intent) {
        //postBooks_che();
        return super.onUnbind(intent);
    }

    public String data = "下载中...";
    List<DownBean> downBeanList=new ArrayList<>();
    public class MyBinder extends Binder {
        public void setData(String data) {
            CacheService.this.data = data;
        }
        public void che(DownBean downBean){
            if(downBeanList.size()==0) {
                downBeanList.add(downBean);
                all_che(downBean);
            }else {
                for(int i=0;i<downBeanList.size();i++){
                  if(downBeanList.get(i).getId().equals(downBean.getId())){
                      downBeanList.remove(downBeanList.get(i));
                  }
                }
                downBeanList.add(downBean);
            }
        }
        public CacheService getService() {
            return CacheService.this;
        }
    }
    public void all_che(DownBean downBean){
          weigh=downBean.getWeight();
          serialize=downBean.getSerialize();
          bookname=downBean.getTitle();
          bookcover=downBean.getPic();
          id=downBean.getId();
          z=downBean.getPosition();
          path = Constant.BOOK_ADRESS + "/"+id+"/"+ bookname;
          postBooks_che();
    }
    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onDataChange(String data,String pid);
    }

    private Callback callback;

}