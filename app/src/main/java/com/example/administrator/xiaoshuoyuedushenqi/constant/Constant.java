package com.example.administrator.xiaoshuoyuedushenqi.constant;

import android.os.Environment;

import com.example.administrator.xiaoshuoyuedushenqi.app.App;

import java.util.Arrays;
import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class Constant {
    /* 热门榜单相关 */
    public static final int male=1;//男
    public static final int female=2;//男
    // 男生热门榜单的榜单数
    public static final int MALE_HOT_RANK_NUM = 5;
    public static int TIME_MAX = 6;    // 最大次数
    public static int ADM_MAX=3;
    public static String text_adress1="font/方正卡通简体.ttf";
    public static String text_name1="方正卡通简体";

    public static String text_adress2="font/方正楷体.ttf";
    public static String text_name2="方正楷体";

    public static String text_adress3="font/流行体简体.ttf";
    public static String text_name3="流行体简体";

    public static String text_adress4="font/华康圆体W7.ttf";
    public static String text_name4="华康圆体W7";

    public static String text_name0="系统字体";
    // 女生热门榜单的榜单数
    public static final int FEMALE_HOT_RANK_NUM = 3;
    // 女生热门榜单的 id
    private static String sKHotRankId = "550b841715db45cd4b022107";    // 17K订阅榜
    private static String sNZYHotRankId = "564d80d0e8c613016446c5aa";    // 掌阅热销榜
    private static String sNSQHotRankId = "564d81151109835664770ad7";    // 书旗热搜榜
    public static final List<String> FEMALE_HOT_RANK_ID = Arrays.asList(
            sKHotRankId, sNZYHotRankId, sNSQHotRankId);
    // 女生热门榜单的榜单名字
    public static final List<String> FEMALE_HOT_RANK_NAME = Arrays.asList(
            "17K订阅榜", "掌阅热销榜", "书旗热搜榜");

    /* 错误信息 */
    // 小说源 api 没有找到相关小说
    public static final String NOT_FOUND_NOVELS = "没有找到相关小说";
    // 没有获取到相关目录信息
    public static final String NOT_FOUND_CATALOG_INFO = "没有找到相关目录";
    // json 格式错误
    public static final String JSON_ERROR = "json 格式错误";
    // 该小说已从本地删除
    public static final String NOT_FOUND_FROM_LOCAL = "该小说已从本地删除";

    /* 数据库相关 */
    // 数据库名
    public static final String DB_NAME = "FReader.db";
    // 历史记录表
    public static final String TABLE_HISTORY = "TABLE_HISTORY";
    // 网站记录表
    public static final String TABLE_WEBSITE = "TABLE_WEBSITE";
    public static final String WEBSITE_URL = "WEBSITE_URL";
    public static final String WEBSITE_TYPE = "WEBSITE_TYPE";
    // 历史记录表的记录
    public static final String TABLE_HISTORY_ID = "TABLE_HISTORY_ID";       // 自增 id（主键）
    public static final String TABLE_HISTORY_WORD = "TABLE_HISTORY_WORD";   // 搜索词
    // 书架书籍表
    public static final String TABLE_BOOKSHELF_NOVEL = "TABLE_BOOKSHELF_NOVEL";
    public static final String TABLE_COLALOG_NOVEL="TABLE_COLALOG_NOVEL";
    public static final String CREATE_TABLE_COLALOG_NOVEL= "TABLE_COLAG_NOVEL";
    // 书架阅读记录表
    public static final String TABLE_READCORDE_NOVEL = "TABLE_READCORDE_NOVEL";
    // 书架书籍表
    public static final String TABLE_LINE_BOOKSHELF_NOVEL = "TABLE_LINE_BOOKSHELF_NOVEL";
    // 书籍书签表
    public static final String TABLE_BOOKMARK_NOVEL = "TABLE_BOOKMARK_NOVEL";
    // 书架书籍表的记录
    public static final String TABLE_BOOKSHELF_NOVEL_NOVEL_URL = "TABLE_BOOKSHELF_NOVEL_NOVEL_URL"; // 小说 URL（主键）
    public static final String TABLE_BOOKMARK_ID = "TABLE_BOOKMARK_ID"; // 小说 URL（主键）
    public static final String TABLE_BOOKSHELF_NOVEL_NAME = "TABLE_BOOKSHELF_NOVEL_NAME"; // 小说名
    public static final String TABLE_BOOKSHELF_NOVEL_WIGH = "TABLE_BOOKSHELF_NOVEL_WIGH"; // 小说名
    public static final String TABLE_BOOKSHELF_STATUS = "TABLE_BOOKSHELF_STATUS"; //
    public static final String TABLE_BOOKSHELF_IS_CHE = "TABLE_BOOKSHELF_IS_CHE"; // chapter_name
    public static final String TABLE_BOOKSHELF_CHAPTER_NAME="TABLE_BOOKSHELF_CHAPTER_NAME";
    public static final String TABLE_BOOKSHELF_AUTHOR="TABLE_BOOKSHELF_AUTHOR";
    public static final String TABLE_BOOKSHELF_NOVEL_ID = "TABLE_BOOKSHELF_NOVEL_ID"; //
    public static final String TABLE_BOOKSHELF_NOVEL_FUBEN_ID = "TABLE_BOOKSHELF_NOVEL_FUBEN_ID"; //
    public static final String TABLE_BOOKSHELF_NOVEL_COVER = "TABLE_BOOKSHELF_NOVEL_COVER"; // 小说封面
    public static final String TABLE_BOOKSHELF_NOVEL_CONTENT = "TABLE_BOOKSHELF_NOVEL_CONTENT"; // 书签内容
    // 章节索引：网络小说和本地 epub 小说为目录索引，本地 txt 小说无需该属性
    public static final String TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX = "TABLE_BOOKSHELF_NOVEL_CHAPTER_INDEX";
    // 位置索引（用于跳转到上一次进度）：网络小说和 txt 是 String 文本的位置
    public static final String TABLE_BOOKSHELF_NOVEL_POSITION = "TABLE_BOOKSHELF_NOVEL_POSITION";
    // 第二位置索引（epub 解析用）
    public static final String TABLE_BOOKSHELF_NOVEL_SECOND_POSITION = "TABLE_BOOKSHELF_NOVEL_SECOND_POSITION";
    // 时间
    public static final String BOOKSHELF_TIME = "BOOKSHELF_TIME";
    // 类型：0 为网络小说， 1 为本地 txt 小说, 2 为本地 epub 小说
    public static final String TABLE_BOOKSHELF_NOVEL_TYPE = "TABLE_BOOKSHELF_NOVEL_TYPE";


    /* 文件存储 */
    public static final String EPUB_SAVE_PATH = App.getContext().getFilesDir() + "/epubFile";
    public static final  String FONT_ADRESS= Environment.getExternalStorageDirectory() + "/" + "NovalReader";
    public static final  String BOOK_ADRESS= Environment.getExternalStorageDirectory() + "/" + "NovalReader/Book";
    /* 分类小说相关 */
    // gender
    public static final String CATEGORY_GENDER_MALE = "male";   // 男生
    public static final String CATEGORY_GENDER_FEMALE = "female";   // 女生
    public static final String CATEGORY_GENDER_PRESS = "press";   // 出版
    public static final String CATEGORY_GENDER_MALE_TEXT = "男生";
    public static final String CATEGORY_GENDER_FEMALE_TEXT  = "女生";
    public static final String CATEGORY_GENDER_PRESS_TEXT  = "出版";
    // type
    public static final String CATEGORY_TYPE_HOT = "hot";   // 热门
    public static final String CATEGORY_TYPE_NEW = "new";   // 新书
    public static final String CATEGORY_TYPE_REPUTATION = "reputation";   // 好评
    public static final String CATEGORY_TYPE_OVER = "over";   // 完结
    public static final String CATEGORY_TYPE_MONTH = "month";   // 包月
    public static final String CATEGORY_TYPE_HOT_TEXT = "最热";
    public static final String CATEGORY_TYPE_NEW_TEXT = "最新";
    public static final String CATEGORY_TYPE_REPUTATION_TEXT = "评分";
    public static final String CATEGORY_TYPE_OVER_TEXT = "完结";
    public static final String CATEGORY_TYPE_MONTH_TEXT = "包月";
    /* 全部小说 */
    public static final int NOVEL_PAGE_NUM = 10;    // 每页的小说数
}
