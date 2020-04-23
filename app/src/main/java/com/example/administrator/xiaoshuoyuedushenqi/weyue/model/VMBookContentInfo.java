package com.example.administrator.xiaoshuoyuedushenqi.weyue.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DetailedChapterData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.BookManager;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.BookSaveUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.BaseViewModel;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.IBookChapters;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.TxtChapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
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
    String title;

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
    public void loadContent(String bookId, List<TxtChapter> bookChapterList) {
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
           s=Integer.parseInt(bookId);
//        size=bookChapterList.size();
//        Log.e("QQQ", "loadContent: "+size);
//        for(int z=Integer.parseInt(bookId);z<bookChapterList.size();z++) {
            getDetailedChapterData(noval_id, (s+1)+"");
//        }
    }
    String div;
    public void loadContent2(int bookId, List<TxtChapter> bookChapterList,String div) {
             //bookid=bookChapterList.get(bookId).getBookId()+"";
        //首先判断是否Chapter已经存在
//        for (int i = 0; i < bookChapterList.size(); i++) {
            TxtChapter bookChapter = bookChapterList.get(0);
            this.div=div;
            new Thread(new LoadRunable(bookChapter.getLink())).start();
//        }
    }

    public void getDetailedChapterData(String bookid, String id) {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl() + "api/index/Books_Info";
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", bookid)
                .add("weigh", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
               // Log.e("qqq", "onResponse: "+bookid+" "+id+" "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if(jsonObject.isNull("data")){
                            iBookChapters.finishChapters();
                            return;
                        }else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            DetailedChapterData data = mGson.fromJson(object.toString(),DetailedChapterData.class);
                            //File file = BookManager.getBookFile(bookid, data.getTitle());
//                            if(file.exists()){
//                                iBookChapters.finishChapters();
//                            }else {
                            BookSaveUtils.getInstance().saveChapterInfo(bookid, data.getTitle(), data.getContent().replace("&nbsp"," "));
//                            }
                           // Log.e("QQQ", "onResponse: "+s);
//                            if(s<size-1) {
//                                handler.sendEmptyMessage(1);
//                            }else {
                                handler.sendEmptyMessage(2);
//                            }
                        }
                    } else {
                        iBookChapters.finishChapters();
//                        chapterError();
                        return;
                        // mPresenter.getDetailedChapterDataError("请求数据失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    iBookChapters.finishChapters();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                iBookChapters.finishChapters();
                // mPresenter.getDetailedChapterDataError(errorMsg);
            }
        });
        return ;
    }

    class LoadRunable implements Runnable {

        String svrInfo;

        public LoadRunable(String href) {
            this.svrInfo = href;
        }

        @Override

        public void run() {
            try {
                Analysisbiquge(svrInfo,div);
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
                getDetailedChapterData(noval_id, s+"");
            }else  if(msg.what==2){
                iBookChapters.finishChapters();
            }
        }
    };
    String content;

    private void Analysisbiquge(String svrInfo, String div) throws IOException {
        Document doc = Jsoup.connect(svrInfo).get();
        title = doc.body().select("h1").text();
        Elements elements;
        elements = doc.body().select(div);
        //Log.e("QQQ", "Analysisbiquge: "+elements.html());
        content = "转码阅读："+elements.html().replace("<p>","");
//        for (Element link : elements) {
//            String str=link.text()+"</p>";
//            content = content +str;
//        }
        BookSaveUtils.getInstance().saveChapterInfo2(noval_id, title, content.replace("</p>",""));
        iBookChapters.finishChapters();
    }
}
