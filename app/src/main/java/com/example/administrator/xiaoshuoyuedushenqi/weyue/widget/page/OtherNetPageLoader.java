package com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Categorys_one;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Text;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.CollBookBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.helper.CollBookHelper;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.Charset;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.FileUtils;
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

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by newbiechen on 17-5-29.
 * 网络页面加载器
 */

public class OtherNetPageLoader extends PageLoader{
    private static final String TAG = "PageFactory";
    //编码类型
    private Charset mCharset;
    public OtherNetPageLoader(PageView pageView) {
        super(pageView);
    }

    //初始化书籍
    @Override
    public void openBook(CollBookBean collBook){
        super.openBook(collBook);
        isBookOpen = false;
        //if (collBook.getBookChapters() == null) return;
        //mChapterList = convertTxtChapter(collBook.getBookChapters());
        //设置目录回调
        Log.e("WWW", "openBook: "+222);
        getCategorys(mCollBook.get_id());
        //提示加载下面的章节
        //getDetailedChapterData(mCollBook.get_id(),chpter_id+"",1);
    }
    int z=0;
    List<Categorys_one> categorys_ones = new ArrayList<>();
    Gson mGson=new Gson();
    private void getCategorys(String id) {
        String url = UrlObtainer.GetUrl() + "api/index/hua_book";
        RequestBody requestBody = new FormBody.Builder()
                .add("novel_id", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQQ", "onResponse: " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if (jsonObject.isNull("data")) {
                            getCatalogDataError();
                        } else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            categorys_ones.clear();
                            for (int z = 0; z < jsonArray.length(); z++) {
                                categorys_ones.add(mGson.fromJson(jsonArray.getJSONObject(z).toString(), Categorys_one.class));
                            }
                            // Log.e("qqq", "onResponse: " + categorys_ones.size());
                            getCatalogDataSuccess(categorys_ones);
                        }
                    } else {
                        getCatalogDataError();
                        //mPresenter.getDetailedChapterDataError("请求数据失败");
                    }
                } catch (JSONException e) {
                    getCatalogDataError();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCatalogDataError();
            }
        });
    }
    private void getCatalogDataSuccess(List<Categorys_one>  catalogData) {
        mChapterList = convertTxtChapter(catalogData);
        if (mPageChangeListener != null){
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
        loadCurrentChapter();
    }
    private void getCatalogDataError() {

    }
    //    public void getCatalogData(String id,int posion,int type) {
//        String url = UrlObtainer.GetUrl()+"api/index/Books_List";
//        RequestBody requestBody = new FormBody.Builder()
//                .add("id", id)
//                .add("type", type+"")
//                .add("page",posion+"")
//                .add("limit","50")
//                // .add("limit", id)
//                .build();
//        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
//            @Override
//            public void onResponse(String json) {
//                //Log.e("QQQ2", "onResponse: "+json);
//                try {
//                    JSONObject jsonObject=new JSONObject(json);
//                    String code=jsonObject.getString("code");
//                    if(code.equals("1")){
//                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
//                        JSONArray object=jsonObject1.getJSONArray("data");
//                        List<Cataloginfo> catalogData=new ArrayList<>();
//                        for(int i=0;i<object.length();i++){
//                            catalogData.add(mGson.fromJson(object.getJSONObject(i).toString(),Cataloginfo.class));
//                        }
//                        mPresenter.getCatalogDataSuccess(catalogData);
//                    }else {
//                        mPresenter.getCatalogDataError("请求数据失败");
//                    }
//
//                } catch (JsonSyntaxException | JSONException e) {
//                    mPresenter.getCatalogDataError(com.example.administrator.xiaoshuoyuedushenqi.constant.Constant.JSON_ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                mPresenter.getCatalogDataError(errorMsg);
//            }
//        });
//    }
    private List<TxtChapter> convertTxtChapter(List<Categorys_one>  catalogData){
        List<TxtChapter> txtChapters = new ArrayList<>(catalogData.get(0).getText().size());
        for (Text bean : catalogData.get(0).getText()){
            Log.e("QQQ", "convertTxtChapter: "+bean.getChapter_url());
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = catalogData.get(0).getId()+"";
            chapter.title = bean.getChapter_name();
            chapter.link = bean.getChapter_url();
            txtChapters.add(chapter);
        }
        return txtChapters;
    }
    @Nullable
    @Override
    protected List<TxtPage> loadPageList(int chapter) {
//        if (mChapterList == null){
//            throw new IllegalArgumentException("chapter list must not null");
//        }
        //获取要加载的文件
       // Log.e("QQQ", "loadPageList: "+data);
//        TxtChapter txtChapter = new TxtChapter();
//        txtChapter.setBookId(data.getId());
//        txtChapter.setTitle(data.getTitle());
//        txtChapter.setStart(0);
//        txtChapter.setEnd(data.getContent().length());
//        byte[] content = data.getContent().replace("&nbsp"," ").replace("</br>","\n").getBytes();
//        ByteArrayInputStream bais = new ByteArrayInputStream(content);
//        BufferedReader br = null;
//        br = new BufferedReader(new InputStreamReader(bais));
//        return loadPages(txtChapter,br);
//        if (mChapterList == null){
//            throw new IllegalArgumentException("chapter list must not null");
//        }

        //获取要加载的文件
        TxtChapter txtChapter = mChapterList.get(chapter);
        File file = new File(Constant.BOOK_CACHE_PATH + mCollBook.get_id()
                + File.separator + txtChapter.title + FileUtils.SUFFIX_WY);
        //Log.e("QQQ", "convertTxtChapter: "+file.getPath());
        if (!file.exists()) return null;
        Reader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);

        return loadPages(txtChapter,br);
    }

//    public void getDetailedChapterData(String bookid, String id) {
//        Gson mGson=new Gson();
//        String url = UrlObtainer.GetUrl() + "api/index/Books_Info";
//        RequestBody requestBody = new FormBody.Builder()
//                .add("uid", bookid)
//                .add("weigh", id+"")
//                .build();
//        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
//            @Override
//            public void onResponse(String json) {   // 得到 json 数据
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    String code = jsonObject.getString("code");
//                    if (code.equals("1")) {
//                        if(jsonObject.isNull("data")){
//                          return;
//                        }else {
//                            JSONObject object = jsonObject.getJSONObject("data");
//                             data = mGson.fromJson(object.toString(),DetailedChapterData.class);
//                        }
//                    } else {
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//               // mPresenter.getDetailedChapterDataError(errorMsg);
//            }
//        });
//    }
    //装载上一章节的内容
    @Override
    boolean prevChapter(){

      boolean hasPrev = super.prevChapter();
      if (!hasPrev) return false;

        if (mStatus == STATUS_FINISH){
            loadCurrentChapter();
            return true;
        }
        else if (mStatus == STATUS_LOADING){
            loadCurrentChapter();
            return false;
        }
        return false;
    }

    //装载下一章节的内容
    @Override
    boolean nextChapter(){
       boolean hasNext = super.nextChapter();
       if (!hasNext) return false;
       if (mStatus == STATUS_FINISH){
            //Log.e("QQQ", "nextChapter: "+222);
           loadNextChapter();
            //loadCurrentChapter();
            return true;
        }
        else if (mStatus == STATUS_LOADING){
            loadCurrentChapter();
            return false;
        }
        return false;
    }

    //跳转到指定章节
    public void skipToChapter(int pos){
        super.skipToChapter(pos);

        //提示章节改变，需要下载
        loadCurrentChapter();
    }

    private void loadPrevChapter(){
        //提示加载上一章
        if (mPageChangeListener != null){
            //提示加载前面3个章节（不包括当前章节）
            int current = mCurChapterPos;
            int prev = current - 3;
            if (prev < 0){
                prev = 0;
            }
            mPageChangeListener.onLoadChapter(mChapterList.subList(prev,current),mCurChapterPos);
        }
    }

    private void loadCurrentChapter(){
        if (mPageChangeListener != null){
            List<TxtChapter> bookChapters = new ArrayList<>(5);
            //提示加载当前章节和前面两章和后面两章
            int current = mCurChapterPos;
            bookChapters.add(mChapterList.get(current));

            //如果当前已经是最后一章，那么就没有必要加载后面章节
            if (current != mChapterList.size()){
                int begin = current + 1;
                int next = begin + 2;
                if (next > mChapterList.size()){
                    next = mChapterList.size();
                }
                bookChapters.addAll(mChapterList.subList(begin,next));
            }

            //如果当前已经是第一章，那么就没有必要加载前面章节
            if (current != 0){
                int prev = current - 2;
                if (prev < 0){
                    prev = 0;
                }
                bookChapters.addAll(mChapterList.subList(prev,current));
            }
            Log.e("QQQ", "loadCurrentChapter: "+222);
            mPageChangeListener.onLoadChapter(bookChapters,mCurChapterPos);
        }
    }

    private void loadNextChapter(){
        //提示加载下一章
        if (mPageChangeListener != null){
            //提示加载当前章节和后面3个章节
            int current = mCurChapterPos + 1;
            int next = mCurChapterPos + 3;
            if (next > mChapterList.size()){
                next = mChapterList.size();
            }
            //chpter_id++;
            mPageChangeListener.onLoadChapter(mChapterList,mCurChapterPos);
        }
    }


    @Override
    public void saveRecord() {
        super.saveRecord();
        if (mCollBook != null && isBookOpen){
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

