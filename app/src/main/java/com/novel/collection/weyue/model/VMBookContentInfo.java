package com.novel.collection.weyue.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.novel.collection.entity.data.DetailedChapterData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.view.activity.ReadActivity;
import com.novel.collection.weyue.utils.BookManager;
import com.novel.collection.weyue.utils.BookSaveUtils;
import com.novel.collection.weyue.utils.Constant;
import com.novel.collection.weyue.utils.FileUtils;
import com.novel.collection.weyue.utils.LogUtils;
import com.novel.collection.weyue.widget.BaseViewModel;
import com.novel.collection.weyue.widget.IBookChapters;
import com.novel.collection.weyue.widget.page.TxtChapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;
import javax.xml.transform.Transformer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Liang_Lu on 2017/12/11.
 */

public class VMBookContentInfo extends BaseViewModel {
    IBookChapters iBookChapters;
    String noval_id;

    public String getNoval_id() {
        return noval_id;
    }

    public void setNoval_id(String noval_id) {
        this.noval_id = noval_id;
    }

    Disposable mDisposable;
    String title,title1;

    public VMBookContentInfo(Context mContext, IBookChapters iBookChapters) {
        super(mContext);
        this.iBookChapters = iBookChapters;
    }

    public void loadChapters(String bookId) {
//        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
//                .bookChapters(bookId)
//                .compose(Transformer.switchSchedulers())
//                .subscribe(new RxObserver<BookChaptersBean>() {
//                    @Override
//                    protected void onError(String errorMsg) {
//
//                    }
//
//                    @Override
//                    protected void onSuccess(BookChaptersBean data) {
//                        if (iBookChapters != null) {
//                            iBookChapters.bookChapters(data);
//                        }
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        addDisposadle(d);
//                    }
//                });
    }

    /**
     * 加载正文
     *
     * @param bookId
     * @param bookChapterList
     */
    int s=0,size=0;
    List<TxtChapter> bookChapterList;
    public void loadContent(String bookId, List<TxtChapter> bookChapterList) {
        Log.e("TAG", "loadContent: "+bookId);
        this.bookChapterList=bookChapterList;
//        //取消上次的任务，防止多次加载
//        if (mDisposable != null) {
//            mDisposable.dispose();
//        }
//
//        List<Observable<ChapterContentBean>> chapterContentBeans = new ArrayList<>(bookChapterList.size());
//        ArrayDeque<String> titles = new ArrayDeque<>(bookChapterList.size());
//        //首先判断是否Chapter已经存在
//        for (int i = 0; i < size; i++) {
//            TxtChapter bookChapter = bookChapterList.get(i);
//            if (!(BookManager.isChapterCached(bookId, bookChapter.getTitle()))) {
//                Observable<ChapterContentBean> contentBeanObservable = RxHttpUtils
//                        .createApi(BookService.class).bookContent(bookChapter.getLink());
//                chapterContentBeans.add(contentBeanObservable);
//                titles.add(bookChapter.getTitle());
//            }
//            //如果已经存在，再判断是不是我们需要的下一个章节，如果是才返回加载成功
//            else if (i == 0) {
//                if (iBookChapters != null) {
//                    iBookChapters.finishChapters();
//                }
//            }
//        }
//        title = titles.poll();
//        Observable.concat(chapterContentBeans)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Consumer<ChapterContentBean>() {
//                            @Override
//                            public void accept(ChapterContentBean bean) throws Exception {
//                                BookSaveUtils.getInstance().saveChapterInfo(bookId, title, bean.getChapter().getCpContent());
//                                iBookChapters.finishChapters();
//                                title = titles.poll();
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                if (bookChapterList.get(0).getTitle().equals(title)) {
//                                    iBookChapters.errorChapters();
//                                }
//                            }
//                        }, new Action() {
//                            @Override
//                            public void run() throws Exception {
//
//                            }
//                        }, new Consumer<Disposable>() {
//                            @Override
//                            public void accept(Disposable disposable) throws Exception {
//                                mDisposable = disposable;
//                            }
//                        });
           if(bookChapterList.size()==0){
              return;
           }
           s=0;
           size=bookChapterList.size();
          // for(int i=0;i<bookChapterList.size();i++) {
               //if(i==0) {
           //s = Integer.parseInt(bookId);
//               }else {
//                   s++;
//               }
           //title1 = bookChapterList.get(s).getTitle();
           getDetailedChapterData(noval_id, bookChapterList.get(s).getBookId() + "", bookChapterList.get(s).getTitle());
    }
    String div,other_id;
    public void loadContent2(int bookId, List<TxtChapter> bookChapterList,String div,String other_website_id) {
            TxtChapter bookChapter = bookChapterList.get(0);
            this.div=div;
            title=bookChapter.getTitle();
            other_id=other_website_id;
            new Thread(new LoadRunable(bookChapter.getLink())).start();
    }

    public void getDetailedChapterData(String bookid, String id,String title) {
        File file = new File(Constant.BOOK_CACHE_PATH + bookid
                + File.separator + title.replace(" ", "") + FileUtils.SUFFIX_WY);
        if(file.exists()){
            handler.sendEmptyMessage(1);
            return;
        }
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl() + "/api/index/Books_Info";
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", bookid)
                .add("weigh", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQW4", "onResponse: "+bookid+" "+id+" "+url+" "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if(jsonObject.isNull("data")){
                            BookSaveUtils.getInstance().saveChapterInfo(bookid, title.replace(" ",""), "内容为空");
                           // iBookChapters.finishChapters();
                            handler.sendEmptyMessage(1);
                            return;
                        }else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            DetailedChapterData data = mGson.fromJson(object.toString(),DetailedChapterData.class);
                            BookSaveUtils.getInstance().saveChapterInfo(bookid, data.getTitle().replace(" ", ""), data.getContent().replace("&nbsp", " "));
                            // handler.sendEmptyMessage(2);
                            handler.sendEmptyMessage(1);
                        }
                    } else {
                        BookSaveUtils.getInstance().saveChapterInfo(bookid, title.replace(" ", ""), "内容为空");
                        //iBookChapters.finishChapters();
                        handler.sendEmptyMessage(1);
                        return;

                    }
                } catch (JSONException e) {
                    BookSaveUtils.getInstance().saveChapterInfo(bookid, title.replace(" ", ""), "内容为空");
                    handler.sendEmptyMessage(1);
                    //iBookChapters.finishChapters();
                    return;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                File file = BookManager.getBookFile(bookid, title.replace(" ", ""));
                if(!file.exists()||file.length()==0) {
                    BookSaveUtils.getInstance().saveChapterInfo(bookid, title.replace(" ", ""), "内容为空");
                }
                handler.sendEmptyMessage(1);
                //iBookChapters.finishChapters();
                return;
                // mPresenter.getDetailedChapterDataError(errorMsg);
            }
        });
        return ;
    }

    public void getGet(String bookid) {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl() + "/api/index/hua_status";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", bookid)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {// 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                File file = BookManager.getBookFile(bookid, title.replace(" ", ""));
                if(!file.exists()||file.length()==0) {
                    BookSaveUtils.getInstance().saveChapterInfo(bookid, title.replace(" ", ""), "内容为空");
                }
                handler.sendEmptyMessage(1);
                //iBookChapters.finishChapters();
                return;
                // mPresenter.getDetailedChapterDataError(errorMsg);
            }
        });
        return ;
    }

    class LoadRunable implements Runnable {

        String link;

        public LoadRunable(String link) {
            this.link = link;
        }

        @Override

        public void run() {
            try {
                Analysisbiquge(link,div);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                s++;
                if(s>=size){
                    iBookChapters.finishChapters();
                }else {
                    getDetailedChapterData(noval_id, bookChapterList.get(s).getBookId() + "", bookChapterList.get(s).getTitle());
                }
                //getDetailedChapterData(noval_id, s+"",title1);
            }else  if(msg.what==2){
                iBookChapters.finishChapters();
            }
        }
    };
    String content;

    private void Analysisbiquge(String link, String div) throws IOException {
        Log.e("QQQ", "Analysisbiquge: "+link+" "+div);
       try {
           Document doc = Jsoup.parse(new URL(link), 5000);
           Elements elements;
           if(div==null){
               div="#content";
           }
           elements = doc.body().select(div);
           Log.e("TAG", "Analysisbiquge: "+elements.html());
           content = "转码阅读：" +getTextFromHtml(elements.html());//
           if(getTextFromHtml(elements.html())==null||getTextFromHtml(elements.html()).trim().equals("")){
               content="该网站已失效，请换网址阅读";
               getGet(other_id);
           }
           BookSaveUtils.getInstance().saveChapterInfo2(noval_id, title.replace(" ",""), content);
           //BookSaveUtils.getInstance().saveNowChapterInfo2(noval_id, content.replace("</p>",""));
           iBookChapters.finishChapters();
       }catch (Exception ex){
           BookSaveUtils.getInstance().saveChapterInfo2(noval_id, title.replace(" ",""), "内容有误");
           //BookSaveUtils.getInstance().saveNowChapterInfo2(noval_id, content.replace("</p>",""));
           iBookChapters.finishChapters();
       }
    }
    /**
     * 根据网址返回网页的源码
     *
     * @param htmlUrl
     * @return
     */
    public String getHtmlSource(String htmlUrl) {
        URL url;
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(htmlUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream(), "UTF-8"));// 读取网页全部内容
            String temp;
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            in.close();
        } catch (MalformedURLException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 去除html代码中含有的标签
     * @param htmlStr
     * @return
     */
    public String delHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex="<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex="<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex="<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // 过滤&nbsp
        htmlStr = htmlStr.replaceAll("&nbsp", "");
        htmlStr = htmlStr.replaceAll("&nbsp;", "");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "\r\n");
        // 过滤空格等
        //htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串
    }
    /**
     * 获取HTML代码里的内容
     * @param htmlStr
     * @return
     */
    public String getTextFromHtml(String htmlStr){
        //去除html标签
        htmlStr = delHtmlTags(htmlStr);
        //去除空格" "
        htmlStr = htmlStr.replaceAll(" ","");
        htmlStr = htmlStr.replaceAll(";","");
        return htmlStr;
    }
}
