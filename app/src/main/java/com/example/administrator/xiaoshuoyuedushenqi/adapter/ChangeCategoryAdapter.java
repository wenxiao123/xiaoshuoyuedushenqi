package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Categorys_one;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author
 * Created on 2019/11/11
 */
public class ChangeCategoryAdapter extends RecyclerView.Adapter<ChangeCategoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private List<Categorys_one> categorys_ones;
    private HistoryAdapterListener mListener;
    int position=-1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface HistoryAdapterListener {
        void clickWord(int word);    // 点击历史搜索词语
    }

    public void setOnHistoryAdapterListener(HistoryAdapterListener listener) {
        mListener = listener;
    }

    public ChangeCategoryAdapter(Context mContext, List<Categorys_one> categorys_ones) {
        this.mContext = mContext;
        this.categorys_ones = categorys_ones;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_changecategory, null));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position) {
        Categorys_one categorys_one=categorys_ones.get(position);
        String s;
        if(getPosition()==-1) {
            s = categorys_one.getCaptername();
        }else {
            if(categorys_one.getText()!=null) {
                s = categorys_one.getText().get(getPosition()).getChapter_name();
            }else {
                s="";
            }
        }
        // 如果超过 8 个字符，只取前 8 个
        if (s!=null&&s.length() > 38) {
            s = s.substring(0, 38) + "...";
        }
        if(s==null){
            holder.itemView.setVisibility(View.GONE);
        }
        holder.content.setText(s);
        holder.tv_laiyuan.setText("来源："+categorys_one.getReurl());
        holder.tv_total_catagorys.setText("共"+categorys_one.getChapter_sum()+"章");
        try {
            holder.tv_updata_time.setText("更新于:"+getTimeFormatText(ConverToDate(categorys_one.getUpdate_time())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickWord(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorys_ones.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView content,tv_updata_time,tv_total_catagorys,tv_laiyuan;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_title);
            tv_updata_time = itemView.findViewById(R.id.tv_updata_time);
            tv_total_catagorys = itemView.findViewById(R.id.tv_total_catagorys);
            tv_laiyuan = itemView.findViewById(R.id.tv_laiyuan);
        }
    }
    /**
     * 时间差
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Date date) {
        long minute = 60 * 1000;// 1分钟
        long hour = 60 * minute;// 1小时
        long day = 24 * hour;// 1天
        long month = 31 * day;// 月
        long year = 12 * month;// 年

        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(strDate);
    }
//    public void updateList(List<String> list) {
//        mContentList = list;
//    }
}
