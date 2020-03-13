package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CatalogBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.DetailedChapterBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DetailedChapterData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.EpubUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author WX
 * Created on 2019/11/25
 */
public class ReadModel implements IReadContract.Model {
    private static final String TAG = "ReadModel";

    private IReadContract.Presenter mPresenter;
    private Gson mGson;

    private OpfData mOpfData;

    public ReadModel(IReadContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
        mGson = new Gson();
    }

    @Override
    public void getChapterList(String url) {
        OkhttpUtil.getRequest(url, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                CatalogBean bean = mGson.fromJson(json, CatalogBean.class);
                if (bean.getCode() != 0) {
                    mPresenter.getChapterUrlListError("未找到相关数据");
                    return;
                }
                List<String> chapterUrlList = new ArrayList<>();
                List<String> chapterNameList = new ArrayList<>();
                List<CatalogBean.ListBean> list = bean.getList();
                for (CatalogBean.ListBean item : list) {
                    chapterUrlList.add(item.getUrl());
                    chapterNameList.add(item.getNum());
                }
                mPresenter.getChapterUrlListSuccess(chapterUrlList, chapterNameList);
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getChapterUrlListError(errorMsg);
            }
        });
    }

    @Override
    public void getDetailedChapterData(String id) {
        String url = UrlObtainer.GetUrl() + "api/index/Books_Info";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
//                .add("uid", id)
//                .add("weigh", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if(jsonObject.isNull("data")){
                            mPresenter.getDetailedChapterDataError("请求数据失败");
                        }else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            String title = object.getString("title");
                            String content = object.getString("content");
                            String id=object.getString("weigh");
                            DetailedChapterData data = new DetailedChapterData(title,
                                    content);
                            data.setId(id);
                            mPresenter.getDetailedChapterDataSuccess(data);
                        }
                    } else {
                        mPresenter.getDetailedChapterDataError("请求数据失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getDetailedChapterDataError(errorMsg);
            }
        });
    }

    @Override
    public void getDetailedChapterData(String bookid, String id) {
        String url = UrlObtainer.GetUrl() + "api/index/Books_Info";
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", bookid)
                .add("weigh", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if(jsonObject.isNull("data")){
                            mPresenter.getDetailedChapterDataError("请求数据失败");
                        }else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            String title = object.getString("title");
                            String content = object.getString("content");
                            String id=object.getString("weigh");
                            DetailedChapterData data = new DetailedChapterData(title,
                                    content);
                            data.setId(id);
                            mPresenter.getDetailedChapterDataSuccess(data);
                        }
                    } else {
                        mPresenter.getDetailedChapterDataError("请求数据失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getDetailedChapterDataError(errorMsg);
            }
        });
    }

    @Override
    public void loadTxt(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(filePath);
                BufferedReader br = null;
                StringBuilder builder = null;
                String error = "";
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
                    builder = new StringBuilder();
                    String str;
                    while ((str = br.readLine()) != null) {
                        builder.append(str);
                        builder.append("\n");
                    }
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "e1 = " + e.getMessage());
                    e.printStackTrace();
                    error = Constant.NOT_FOUND_FROM_LOCAL;
                } catch (UnsupportedEncodingException e) {
                    Log.d(TAG, "e2 = " + e.getMessage());
                    e.printStackTrace();
                    error = e.getMessage();
                } catch (IOException e) {
                    Log.d(TAG, "e3 = " + e.getMessage());
                    e.printStackTrace();
                    error = e.getMessage();
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                final String finalError = error;
                final String text = builder == null ? "" : builder.toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalError.equals("")) {
                            mPresenter.loadTxtSuccess(text);
                        } else {
                            mPresenter.loadTxtError(finalError);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 从 epub 文件中读取到 Opf 文件信息
     *
     * @param filePath epub 路径
     */
    @Override
    public void getOpfData(final String filePath) {
        File file = new File(filePath);
        final String savePath = Constant.EPUB_SAVE_PATH + "/" + file.getName();
        File saveFile = new File(savePath);
        if (saveFile.exists()) {
            getOpfDataImpl(savePath);
            if (mOpfData != null) {
                getOpfDataSuccess(mOpfData);
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EpubUtils.unZip(filePath, savePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    getOpfDataError("解压失败，可能文件被加密");
                    return;
                }
                getOpfDataImpl(savePath);
                if (mOpfData != null) {
                    getOpfDataSuccess(mOpfData);
                }
            }
        }).start();
    }

    private void getOpfDataImpl(String savePath) {
        try {
            // 先得到 opf 文件的位置
            String opfPath = EpubUtils.getOpfPath(savePath);
            Log.d(TAG, "unZipEpub: opfPath = " + opfPath);
            // 解析 opf 文件
            mOpfData = EpubUtils.parseOpf(opfPath);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            getOpfDataError("Xml 解析出错");
        } catch (IOException e) {
            e.printStackTrace();
            getOpfDataError("I/O 错误");
        }
    }

    private void getOpfDataError(final String errorMsg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getOpfDataError(errorMsg);
            }
        });
    }

    private void getOpfDataSuccess(final OpfData opfData) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getOpfDataSuccess(opfData);
            }
        });
    }

    /**
     * 解析 html/xhtml 文件，得到 Epub 章节数据
     *
     * @param filePath 读取的文件路径
     */
    @Override
    public void getEpubChapterData(String parentPath, String filePath) {
        Log.d(TAG, "getEpubChapterData: filePath = " + filePath);
        List<EpubData> dataList = new ArrayList<>();
        try {
            dataList = EpubUtils.getEpubData(parentPath, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mPresenter.getEpubChapterDataError("解析 html/xhtml：I/O 错误");
                }
            });
        }

        final List<EpubData> finalDataList = dataList;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getEpubChapterDataSuccess(finalDataList);
            }
        });
    }
}
