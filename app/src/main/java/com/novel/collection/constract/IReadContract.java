package com.novel.collection.constract;

import com.novel.collection.entity.data.DetailedChapterData;
import com.novel.collection.entity.epub.EpubData;
import com.novel.collection.entity.epub.OpfData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/25
 */
public interface IReadContract {
    interface View {
        void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList);
        void getChapterUrlListError(String errorMsg);
        void getDetailedChapterDataSuccess(DetailedChapterData data);
        void getDetailedChapterDataError(String errorMsg);
        void loadTxtSuccess(String text);
        void loadTxtError(String errorMsg);
        void getOpfDataSuccess(OpfData opfData);
        void getOpfDataError(String errorMsg);
        void getEpubChapterDataSuccess(List<EpubData> dataList);
        void getEpubChapterDataError(String errorMsg);
        void getReadRecordSuccess(String message);
        void getReadRecordError(String errorMsg);
    }
    interface Presenter {
        void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList);
        void getChapterUrlListError(String errorMsg);
        void getDetailedChapterDataSuccess(DetailedChapterData data);
        void getDetailedChapterDataError(String errorMsg);
        void loadTxtSuccess(String text);
        void loadTxtError(String errorMsg);
        void getOpfDataSuccess(OpfData opfData);
        void getOpfDataError(String errorMsg);
        void getEpubChapterDataSuccess(List<EpubData> dataList);
        void getEpubChapterDataError(String errorMsg);
        void getReadRecordSuccess(String message);
        void getReadRecordError(String errorMsg);

        void getChapterList(String url);         // 获取章节目录
        void getDetailedChapterData(String url);    // 获取具体章节信息
        void getDetailedChapterData(String bookid,String id);    // 获取具体章节信息
        void loadTxt(String filePath);      // 加载 txt 文本
        void getOpfData(String filePath);  // 解压 epub，得到 opf 文件中的数据
        void getEpubChapterData(String parentPath, String filePath);   // 解析 html/xhtml 文件，得到章节数据
        void setReadRecord(String token, String novel_id,String chapter_id);
        void setBookshelfadd(String token, String novel_id);
    }
    interface Model {
        void getChapterList(String url);         // 获取章节目录
        void getDetailedChapterData(String url);    // 获取具体章节信息
        void getDetailedChapterData(String bookid,String id);    // 获取具体章节信息
        void loadTxt(String filePath);      // 加载 txt 文本
        void getOpfData(String filePath);  // 解压 epub，得到 opf 文件中的数据
        void getEpubChapterData(String parentPath, String filePath);   // 解析 html/xhtml 文件，得到章节数据
        void setReadRecord(String token, String novel_id,String chapter_id);
        void setBookshelfadd(String token, String novel_id);
    }
}
