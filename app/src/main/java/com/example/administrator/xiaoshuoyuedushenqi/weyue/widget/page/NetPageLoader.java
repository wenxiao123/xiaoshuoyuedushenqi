package com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arialyy.aria.util.DbDataHelper;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Categorys_one;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Other_one;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Text;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.BookRecordBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.CollBookBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.helper.CollBookHelper;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.Charset;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.FileUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.LogUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by newbiechen on 17-5-29.
 * 网络页面加载器
 */

public class NetPageLoader extends PageLoader {
    private static final String TAG = "PageFactory";
    //编码类型
    private Charset mCharset;

    public NetPageLoader(PageView pageView) {
        super(pageView);
    }

    DatabaseManager databaseManager;

    //初始化书籍
    @Override
    public void openBook(CollBookBean collBook, BookRecordBean bookRecordBean) {
        super.openBook(collBook, bookRecordBean);
        databaseManager = DatabaseManager.getInstance();
        isBookOpen = false;
        if (collBook.getCataloginfos() != null) {
            if (collBook.getCataloginfos().size() == 0) {
                getCatalogData(mCollBook.get_id(), z, 1);
            } else {
                mChapterList = convertTxtChapter(collBook.getCataloginfos());
                mOrigChapterList = convertTxtChapter(collBook.getCataloginfos());
                if (mPageChangeListener != null) {
                    mPageChangeListener.onCategoryFinish(mChapterList);
                }
                loadCurrentChapter();
            }
        } else {
            getCatalogData(mCollBook.get_id(), z, 1);
        }
//        mChapterList = convertTxtChapter(collBook.getBookChapters());
        //设置目录回调
        //提示加载下面的章节
    }

    public List<Categorys_one> getCategorys_ones() {
        return categorys_ones;
    }

    int position;
    boolean is_of_all;

//    public void setCategorys_ones(List<Categorys_one> categorys_ones, int position, boolean is_all) {
//        this.categorys_ones = categorys_ones;
//        this.position = position;
//        is_website = true;
//        is_of_all = is_all;
//        mChapterList = convertTxtChapter2(categorys_ones, position);
//        mStatus = STATUS_LOADING;
//        Log.e("www", "setCategorys_ones: " + mChapterList.size());
//        loadCurrentChapter();
//    }

//    public void setCategorys() {
//        if (mCollBook.getCataloginfos() != null) {
//            if (mCollBook.getCataloginfos().size() == 0) {
//                getCatalogData(mCollBook.get_id(), z, 1);
//            } else {
//                mChapterList = convertTxtChapter(mCollBook.getCataloginfos());
//                if (mPageChangeListener != null) {
//                    mPageChangeListener.onCategoryFinish(mChapterList);
//                }
//                loadCurrentChapter();
//            }
//        } else {
//            getCatalogData(mCollBook.get_id(), z, 1);
//        }
//    }

    String other_id;
    String other_title="";

    public void setOtherCategorys_ones(String id, boolean is_all) {
        is_website = true;
        is_of_all = is_all;
        other_id = id;
        if(mChapterList!=null) {
            other_title = mChapterList.get(mCurChapterPos).title;
        }
        //Log.e("TAG", "handleMessage: " + other_title + " " + mCurChapterPos + " ");
        mStatus = STATUS_LOADING;
        mPageView.refreshPage();
        getOtherCatalogData(other_id, o, 1);
    }

    public  String format(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    List<Categorys_one> categorys_ones = new ArrayList<>();
    int z = 1, o = 1;

    public void getCatalogData(String id, int posion, int type) {
        Gson gson = new Gson();
        String url = UrlObtainer.GetUrl() + "/" + "/api/index/Books_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("type", type + "")
                .add("page", posion + "")
                .add("limit", "50")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {
                Log.e("QQQ", "onResponse: " + posion + " " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray object = jsonObject1.getJSONArray("data");
                        weigh = Integer.parseInt(jsonObject1.getString("total"));
                        setWeight(weigh);
                        List<Cataloginfo> catalogData = new ArrayList<>();
                        for (int i = 0; i < object.length(); i++) {
                            catalogData.add(gson.fromJson(object.getJSONObject(i).toString(), Cataloginfo.class));
                        }
                        getCatalogDataSuccess(catalogData);
                        timecount = 0;
                    } else {
                        getCatalogDataError("请求数据失败");
                    }

                } catch (JsonSyntaxException | JSONException e) {
                    if (timecount < 6) {
                        getCatalogData(mCollBook.get_id(), z, 1);
                    } else {
                        getCatalogDataError("Constant.JSON_ERROR");
                    }
                    timecount++;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (timecount < 6) {
                    getCatalogData(mCollBook.get_id(), z, 1);
                } else {
                    getCatalogDataError(errorMsg);
                }
                timecount++;
            }
        });
    }


    public void getOtherCatalogData(String id, int posion, int type) {
        Gson gson = new Gson();
        String url = UrlObtainer.GetUrl() + "/api/index/hua_book_chapter";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("type", type + "")
                .add("page", posion + "")
                .add("limit", "50")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {
                Log.e("QQQ2", "onResponse: " + id + " " + posion + " " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray object = jsonObject1.getJSONArray("data");
                        weigh = Integer.parseInt(jsonObject1.getString("total"));
                        setWeight(weigh);
                        List<Other_one> catalogData = new ArrayList<>();
                        for (int i = 0; i < object.length(); i++) {
                            catalogData.add(gson.fromJson(object.getJSONObject(i).toString(), Other_one.class));
                        }
                        getOtherCatalogDataSuccess(catalogData);
                        timecount = 0;
                    } else {
                        getCatalogDataError("请求数据失败");
                    }

                } catch (JsonSyntaxException | JSONException e) {
                    if (timecount < 6) {
                        getOtherCatalogData(other_id, o, 1);
                    } else {
                        getCatalogDataError("Constant.JSON_ERROR");
                    }
                    timecount++;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (timecount < 6) {
                    getOtherCatalogData(other_id, o, 1);
                } else {
                    getCatalogDataError(errorMsg);
                }
                timecount++;
            }
        });
    }


    List<Cataloginfo> catalogDataAll = new ArrayList<>();
    List<Other_one> other_ones = new ArrayList<>();
    int weigh;
    int timecount;

    private void getCatalogDataSuccess(List<Cataloginfo> catalogData) {
        if (catalogData.size() == 0) {
            chapterError();
            return;
        }
        if (weigh < 50 || (z == 1 && catalogData.size() < 50)) {
            catalogDataAll.addAll(catalogData);
            databaseManager.insertBookshelfNovel(catalogDataAll);
            handler.sendEmptyMessage(2);
        } else {
            if (z < weigh / 50) {
                LogUtils.e(z + " " + weigh / 50);
                catalogDataAll.addAll(catalogData);
                handler.sendEmptyMessage(1);
                if (z == 1) {
                    mChapterList = new ArrayList<>(weigh);
                    handler.sendEmptyMessage(2);
                }
            } else {
                if (z == weigh / 50 && weigh % 50 != 0) {
                    LogUtils.e(z + " " + weigh / 50);
                    catalogDataAll.addAll(catalogData);
                    handler.sendEmptyMessage(1);
                } else {
                    LogUtils.e(z + " " + weigh / 50);
                    catalogDataAll.addAll(catalogData);
                    databaseManager.insertBookshelfNovel(catalogDataAll);
                    handler.sendEmptyMessage(2);
                }
            }
        }
    }

    private void getOtherCatalogDataSuccess(List<Other_one> catalogData) {
        if (catalogData.size() == 0) {
            chapterError();
            return;
        }
        if (weigh < 50 || (o == 1 && catalogData.size() < 50)) {
            other_ones.addAll(catalogData);
            //databaseManager.insertBookshelfNovel(catalogDataAll);
            handler.sendEmptyMessage(4);
        } else {
            if (o < weigh / 50) {
                LogUtils.e(o + " " + weigh / 50);
                other_ones.addAll(catalogData);
                handler.sendEmptyMessage(3);
//                if(o==1) {
//                    mChapterList=new ArrayList<>(weigh);
//                    handler.sendEmptyMessage(4);
//                }
            } else {
                if (o == weigh / 50 && weigh % 50 != 0) {
                    LogUtils.e(o + "---" + weigh / 50);
                    other_ones.addAll(catalogData);
                    handler.sendEmptyMessage(3);
                } else {
                    LogUtils.e(o + "***" + weigh / 50);
                    other_ones.addAll(catalogData);
                    //databaseManager.insertBookshelfNovel(catalogDataAll);
                    handler.sendEmptyMessage(4);
                }
            }
        }
    }

    private void getCatalogDataError(String catalogData) {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                z++;
                getCatalogData(mCollBook.get_id(), z, 1);
            } else if (msg.what == 2) {
                mChapterList = convertTxtChapter(catalogDataAll);
                if (mPageChangeListener != null) {
                    mPageChangeListener.onCategoryFinish(mChapterList);
                }
                loadCurrentChapter();
            } else if (msg.what == 3) {
                o++;
                getOtherCatalogData(other_id, o, 1);
            } else if (msg.what == 4) {
                mChapterList = convertTxtChapter3(other_ones);
                for (int i = 0; i < mChapterList.size(); i++) {
                    if (format(other_title.trim()).equals(format(mChapterList.get(i).title.trim()))) {
                        mCurChapterPos = i;
                        break;
                    }
                }
                if (mPageChangeListener != null) {
                    mPageChangeListener.onCategoryFinish(mChapterList);
                }
                loadCurrentChapter();
            }
        }
    };

    private List<TxtChapter> convertTxtChapter(List<Cataloginfo> catalogDataAll) {
        List<TxtChapter> txtChapters = new ArrayList<>(catalogDataAll.size());
        for (Cataloginfo bean : catalogDataAll) {
            // Log.e("QQQ", "convertTxtChapter: "+bean.getTitle());
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = bean.getWeigh() + "";
            chapter.title = bean.getTitle();
            chapter.link = bean.getReurl();
            txtChapters.add(chapter);
        }
        return txtChapters;
    }

    private List<TxtChapter> convertTxtChapter3(List<Other_one> catalogDataAll) {
        List<TxtChapter> txtChapters = new ArrayList<>(catalogDataAll.size());
        for (Other_one bean : catalogDataAll) {
            // Log.e("QQQ", "convertTxtChapter: "+bean.getTitle());
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = bean.getChapter_num() + "";
            chapter.title = bean.getChapter_name();
            chapter.link = bean.getChapter_url();
            txtChapters.add(chapter);
        }
        return txtChapters;
    }

    private List<TxtChapter> convertTxtChapter2(List<Categorys_one> categorys_ones, int position) {
        List<TxtChapter> txtChapters = new ArrayList<>(catalogDataAll.size());
        for (Text bean : categorys_ones.get(position).getText()) {
            // Log.e("QQQ", "convertTxtChapter: "+bean.getTitle());
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = categorys_ones.get(position).getNovel_id() + "";
            chapter.title = bean.getChapter_name();
            chapter.link = bean.getChapter_url();
            txtChapters.add(chapter);
        }
        return txtChapters;
    }

    @Nullable
    @Override
    protected List<TxtPage> loadPageList(int chapter) {
        if (mChapterList == null || mChapterList.size() == 0) {
            return null;
        }
        File file;
        TxtChapter txtChapter = mChapterList.get(chapter);
        if (is_website == false) {
            file = new File(Constant.BOOK_CACHE_PATH + mCollBook.get_id()
                    + File.separator + txtChapter.title.replace(" ", "") + FileUtils.SUFFIX_WY);
            Log.e("zzz", "loadPageList: orghi " + txtChapter.title);
        } else {
            if (is_of_all == true) {
                mChapterList = mOrigChapterList;
                if(mChapterList!=null) {
                    for (int i = 0; i < mChapterList.size(); i++) {
                        if (format(txtChapter.title.trim()).equals(format(mChapterList.get(i).title.trim()))) {
                            mCurChapterPos = i;
                            break;
                        }
                    }
                }
                is_website = false;
            } else {
                is_website = true;
            }
            file = new File(Constant.BOOK_OTHER_CACHE_PATH + mCollBook.get_id()
                    + File.separator + txtChapter.title.replace(" ", "") + FileUtils.SUFFIX_WY);
            Log.e("zzz", "loadPageList: other " + txtChapter.title);
        }
        if (!file.exists()) {
            return null;
        }
        Reader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        return loadPages(txtChapter, br, chapter);
    }

    //装载上一章节的内容
    @Override
    boolean prevChapter() {
        boolean hasPrev = super.prevChapter();
        if (!hasPrev) return false;

        if (mStatus == STATUS_FINISH) {
            loadCurrentChapter();
            return true;
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
            return false;
        }
        return false;
    }

    //装载下一章节的内容
    @Override
    boolean nextChapter() {
        boolean hasNext = super.nextChapter();
        // if (!hasNext) return false;
        if (mStatus == STATUS_FINISH) {
            //Log.e("QQQ", "nextChapter: "+222);
            loadNextChapter();
            //loadCurrentChapter();

        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();

        } else {
            loadCurrentChapter();

        }
        //return false;
        return hasNext;
    }

    //跳转到指定章节
    public void skipToChapter(int pos) {
        super.skipToChapter(pos);

        //提示章节改变，需要下载
        loadCurrentChapter();
    }

    private void loadPrevChapter() {
        //提示加载上一章
        if (mPageChangeListener != null) {
            //  提示加载前面3个章节（不包括当前章节）
            int current = mCurChapterPos;
            int prev = current - 3;
            if (prev < 0) {
                prev = 0;
            }
            mPageChangeListener.onLoadChapter(mChapterList.subList(current, prev), mCurChapterPos);
        }
    }

    private void loadCurrentChapter() {
        if (mPageChangeListener != null && mChapterList != null) {
            List<TxtChapter> bookChapters = new ArrayList<>(5);
            //提示加载当前章节和前面两章和后面两章
            int current = mCurChapterPos;
            if (current < 0 || current > mChapterList.size()) {
                return;
            }
            if (current >= mChapterList.size()) {
                Log.e("www", "loadCurrentChapter: " + mChapterList.size() + " ");
                bookChapters.add(mChapterList.get(mChapterList.size() - 1));
            } else {
                bookChapters.add(mChapterList.get(current));
            }
            //如果当前已经是最后一章，那么就没有必要加载后面章节
            if (current < mChapterList.size()) {
                int begin = current + 1;
                int next = begin + 2;
                if (next > mChapterList.size()) {
                    next = mChapterList.size();
                }
                bookChapters.addAll(mChapterList.subList(begin, next));
            }

            //如果当前已经是第一章，那么就没有必要加载前面章节
            if (current != 0) {
                int prev = current - 2;
                if (prev < 0) {
                    prev = 0;
                }

                bookChapters.addAll(mChapterList.subList(prev, current));
            }
            //Log.e("QQQ", "loadCurrentChapter: "+222);
            mPageChangeListener.onLoadChapter(bookChapters, mCurChapterPos);
        }
    }

    private void loadNextChapter() {
        //提示加载下一章
        if (mPageChangeListener != null) {
            //提示加载当前章节和后面3个章节
            int current = mCurChapterPos + 1;
            int next = mCurChapterPos + 3;
            if (next > mChapterList.size()) {
                next = mChapterList.size();
            }
            //chpter_id++;
            mPageChangeListener.onLoadChapter(mChapterList.subList(current, next), mCurChapterPos);
        }
    }


    @Override
    public void saveRecord() {
        super.saveRecord();
        if (mCollBook != null && isBookOpen) {
            //表示当前CollBook已经阅读
            mCollBook.setUpdate(false);
            mCollBook.setLastRead(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            //直接更新
            CollBookHelper.getsInstance().saveBook(mCollBook);
//            BookRepository.getInstance()
//                    .saveCollBook(mCollBook);
        }
    }
}

