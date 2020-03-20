package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public interface IListNovelContract {
    interface View {
        void getNovelsSuccess(List<NovalInfo> dataList);
        void getNovelsError(String errorMsg);
    }
    interface Presenter {
        void getNovelsSuccess(List<NovalInfo> dataList);
        void getNovelsError(String errorMsg);

        void getNovels(String id,String type);    // 获取小说信息
    }
    interface Model {
        void getNovels(String id,String type);    // 获取小说信息
    }
}