package com.novel.collection.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novel.collection.R;
import com.novel.collection.base.BasePagingLoadAdapter;
import com.novel.collection.constant.Constant;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.view.activity.ReadrecoderActivity;
import com.novel.collection.widget.CornerTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/12/21
 */
public class NovelResultAdapter extends RecyclerView.Adapter {

    private Context mContext;
    List<NovalInfo> mList;
    NovelListener mListener;

    public void setmListener(NovelListener mListener) {
        this.mListener = mListener;
    }

    public NovelResultAdapter(Context mContext, List<NovalInfo> mList) {
        this.mList=mList;
        this.mContext=mContext;
        databaseManager= DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(mContext);
    }
    boolean isRating;

    public void setRating(boolean rating) {
        isRating = rating;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NovelViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_novel_result, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NovelViewHolder novelViewHolder = (NovelViewHolder) holder;
        novelViewHolder.title.setText(mList.get(position).getTitle());
        novelViewHolder.author.setText(mList.get(position).getAuthor());
        novelViewHolder.shortInfo.setText(mList.get(position).getContent());
//        String href;
//        if(mList.get(position).getPic().contains("http")){
//            href=mList.get(position).getPic();
//        }else {
//            href=UrlObtainer.GetUrl()+"/"+mList.get(position).getPic();
//        }
//        CornerTransform transformation = new CornerTransform(mContext, 10);
//        Glide.with(mContext)
//                .load(href)
//                .apply(new RequestOptions()
//                         .error(R.drawable.cover_error)
//                        .transform(transformation))
//                .into(novelViewHolder.cover);
        ImageLoaderUtils.display(mContext,novelViewHolder.cover,mList.get(position).getPic(),R.drawable.cover_error,R.drawable.cover_error);
        novelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(mList.get(position).getId());
            }
        });
        novelViewHolder.cata.setText(" | "+mList.get(position).getCategory_name());
//        if (isRating){
//            novelViewHolder.tv_item_rating.setVisibility(View.VISIBLE);
//            novelViewHolder.tv_item_rating.setText(mList.get(position).getRating()+"分");
//        }else {
//            novelViewHolder.tv_item_rating.setVisibility(View.GONE);
//        }
        if(databaseManager.isExistInBookshelfNovel(mList.get(position).getId()+"")){
            novelViewHolder.tv_item_bookshelf.setText("已加入书架");
            novelViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.gray));
            novelViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_btn));
        }else {
            novelViewHolder.tv_item_bookshelf.setText("加入书架");
            novelViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.red));
            novelViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_red));
            novelViewHolder.tv_item_bookshelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookshelfNovelDbData dbData;
                    dbData = new BookshelfNovelDbData(mList.get(position).getId() + "", mList.get(position).getTitle(),
                            mList.get(position).getPic(), 1, 0, 0, 1 + "", 10, mList.get(position).getSerialize() + "");
                    databaseManager.insertOrUpdateBook(dbData);
                    Intent intent_recever = new Intent("com.zhh.android");
                    intent_recever.putExtra("type",1);
                    mContext.sendBroadcast(intent_recever);
                    if (login_admin != null) {
                        setBookshelfadd(login_admin.getToken(), mList.get(position).getId() + "");
                    }
                    // ((Activity) mContext).finish();
                    novelViewHolder.tv_item_bookshelf.setText("已加入书架");
                    novelViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.gray));
                    novelViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_btn));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface NovelListener {
        void clickItem(int novelName);
    }

    DatabaseManager databaseManager;
    private Login_admin login_admin;

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl()+"/"+"/api/Userbook/add";
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
    class NovelViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title,tv_item_bookshelf;
        TextView author,cata;
        TextView shortInfo,tv_item_rating;

        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_novel_cover);
            title = itemView.findViewById(R.id.tv_item_novel_title);
            author = itemView.findViewById(R.id.tv_item_novel_author);
            shortInfo = itemView.findViewById(R.id.tv_item_novel_short_info);
            cata=itemView.findViewById(R.id.tv_item_novel_cata);
            tv_item_rating=itemView.findViewById(R.id.tv_item_rating);
            tv_item_bookshelf=itemView.findViewById(R.id.tv_item_bookshelf);
        }
    }
}
