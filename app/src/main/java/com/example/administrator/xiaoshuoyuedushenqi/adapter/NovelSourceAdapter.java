package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadrecoderActivity;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/9
 */
public class NovelSourceAdapter extends
        RecyclerView.Adapter<NovelSourceAdapter.NovelSourceViewHolder> {

    private static final String TAG = "NovelSourceAdapter";
    private Login_admin login_admin;
    private Context mContext;
    private List<Noval_Readcored> mNovelSourceDataList;
    private NovelSourceListener mListener;
    DatabaseManager databaseManager;
    public void setOnNovelSourceListener(NovelSourceListener listener) {
        mListener = listener;
    }

    public interface NovelSourceListener {
        void clickItem(int position);
        void longclickItem(int position);
    }

    public NovelSourceAdapter(Context mContext, List<Noval_Readcored> mNovelSourceDataList) {
        this.mContext = mContext;
        this.mNovelSourceDataList = mNovelSourceDataList;
        databaseManager= DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(mContext);
    }

    @NonNull
    @Override
    public NovelSourceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NovelSourceViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_novel_source, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NovelSourceViewHolder novelSourceViewHolder, final int i) {
        String href="";
        if(mNovelSourceDataList.get(i).getPic()!=null) {
            if (mNovelSourceDataList.get(i).getPic().contains("http")) {
                href = mNovelSourceDataList.get(i).getPic();
            } else {
                href = UrlObtainer.GetUrl() + mNovelSourceDataList.get(i).getPic();
            }
        }
        Glide.with(mContext)
                .load(href)
                .apply(new RequestOptions().error(R.drawable.cover_error))
                .into(novelSourceViewHolder.cover);
        novelSourceViewHolder.name.setText(mNovelSourceDataList.get(i).getTitle());
        // 作者可能为空
        String str = mNovelSourceDataList.get(i).getAuthor();
        String author = (str == null || str.equals(""))? "未知" : str;
        if(mNovelSourceDataList.get(i).getStatus().equals("1")){
            novelSourceViewHolder.author.setText(author+" | 已完本 _ "+mNovelSourceDataList.get(i).getWeigh()+"章");
        }else {
            novelSourceViewHolder.author.setText(author+" | 连载中 _ "+mNovelSourceDataList.get(i).getWeigh()+"章");
        }
        novelSourceViewHolder.introduce.setText("阅读至"+mNovelSourceDataList.get(i).getChapter_name());
        //novelSourceViewHolder.webSite.setText(mNovelSourceDataList.get(i).getUrl());
        if(databaseManager.isExistInBookshelfNovel(mNovelSourceDataList.get(i).getNovel_id())){
            novelSourceViewHolder.tv_item_bookshelf.setText("已加入书架");
            novelSourceViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.gray));
            novelSourceViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_btn));
        }else {
            novelSourceViewHolder.tv_item_bookshelf.setText("加入书架");
            novelSourceViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.red));
            novelSourceViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_red));
            novelSourceViewHolder.tv_item_bookshelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookshelfNovelDbData dbData;
                    dbData = new BookshelfNovelDbData(mNovelSourceDataList.get(i).getNovel_id() + "", mNovelSourceDataList.get(i).getTitle(),
                            mNovelSourceDataList.get(i).getPic(), 1, 0, 0, 1+"", Integer.parseInt(mNovelSourceDataList.get(i).getWeigh()), mNovelSourceDataList.get(i).getSerialize() + "");
                    databaseManager.insertOrUpdateBook(dbData);
                    Intent intent_recever = new Intent("com.zhh.android");
                    intent_recever.putExtra("type",1);
                    mContext.sendBroadcast(intent_recever);
                    if(login_admin!=null){
                        setBookshelfadd(login_admin.getToken(),mNovelSourceDataList.get(i).getNovel_id());
                    }
                    //((ReadrecoderActivity)mContext).finish();
                    novelSourceViewHolder.tv_item_bookshelf.setText("已加入书架");
                    novelSourceViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.gray));
                    novelSourceViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_btn));
                }
            });
        }
        novelSourceViewHolder.tv_delect_bookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TipDialog tipDialog = new TipDialog.Builder(mContext)
                        .setContent("是否清除阅读记录")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                if (login_admin != null) {
                                    getDelectReadcoredData(login_admin.getToken(), mNovelSourceDataList.get(i).getNovel_id(), 1);
                                }

                                if (databaseManager.isExistInReadCoderNovel(mNovelSourceDataList.get(i).getNovel_id())) {
                                    databaseManager.deleteBookReadcoderNovel(mNovelSourceDataList.get(i).getNovel_id());
                                }
                                //requestPost();
                                ((ReadrecoderActivity)mContext).requestPost();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        });

        novelSourceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
        novelSourceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.longclickItem(i);
                return true;
            }
        });
    }

    public void getDelectReadcoredData(String token, String novel_id,int type) {
        if(novel_id==null){
            return;
        }
        String url = UrlObtainer.GetUrl()+"api/Lookbook/del";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("type", type+"")
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        String msg=jsonObject.getString("msg");
                        getDelectReadcoredDataSuccess(msg);
                    }else {
                       getDelectReadcoredDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getDelectReadcoredDataError(errorMsg);
            }
        });
    }

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl()+"api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        String message=jsonObject.getString("msg");
                        //mPresenter.getReadRecordSuccess(message);
                    }else {
                        ToastUtil.showToast(mContext,"请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                ToastUtil.showToast(mContext,errorMsg);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mNovelSourceDataList.size();
    }
    public void getDelectReadcoredDataSuccess(String msg) {
        ((ReadrecoderActivity)mContext).getDelectReadcoredDataSuccess(msg);
    }


    public void getDelectReadcoredDataError(String errorMsg) {
        ToastUtil.showToast(mContext,errorMsg);

    }

    class NovelSourceViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name;
        TextView author;
        TextView introduce,tv_delect_bookshelf;
        TextView webSite,tv_item_bookshelf;

        public NovelSourceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_delect_bookshelf=itemView.findViewById(R.id.tv_delect_bookshelf);
            cover = itemView.findViewById(R.id.iv_item_novel_source_cover);
            name = itemView.findViewById(R.id.tv_item_novel_source_name);
            author = itemView.findViewById(R.id.tv_item_novel_source_author);
            introduce = itemView.findViewById(R.id.tv_item_novel_source_introduce);
            webSite = itemView.findViewById(R.id.tv_item_novel_source_web_site);
            tv_item_bookshelf=itemView.findViewById(R.id.tv_item_bookshelf);
        }
    }
}
