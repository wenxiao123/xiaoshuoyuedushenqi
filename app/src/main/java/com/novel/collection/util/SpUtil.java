package com.novel.collection.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.novel.collection.R;
import com.novel.collection.app.App;
import com.novel.collection.entity.bean.Website;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author
 * Created on 2019/12/8
 */
public class SpUtil {
    private static final String NAME = "freader_data";
    private static final String KEY_TEXT_SIZE = "key_text_size";    // 文字大小
    private static final String KEY_ROW_SPACE = "key_row_space";    // 行距
    private static final String KEY_THEME = "key_theme";            // 阅读主题
    private static final String KEY_IS_FIRST = "key_is_first";            // 阅读主题
    private static final String KEY_READ_FIRST = "key_read_first";            // 阅读主题
    private static final String KEY_WENSITE = "key_website";            // 阅读主题
    private static final String KEY_BRIGHTNESS = "key_brightness";  // 亮度
    private static final String KEY_IS_NIGHT_MODE= "key_is_night_mode";  // 是否为夜间模式
    private static final String KEY_IS_SYS_NIGHT_MODE= "key_is_sys_night_mode";  // 是否为夜间模式
    private static final String KEY_TURN_TYPE = "key_turn_type";  // 翻页模式
    private static final String KEY_TEXTSTYLE_TYPE = "key_textstyle";  // 字体模式
    private static final float DEFAULT_TEXT_SIZE = 16f;//App.getContext().getResources().getDimension(R.dimen.sp_8);
    private static final float DEFAULT_ROW_SPACE = 35f;
    private static final int DEFAULT_THEME = 0;
    private static final int IS_FIRST = 0;
    private static final int READ_FIRST = 0;
    private static final String WEBSITE = "";
    private static final float DEFAULT_BRIGHTNESS = -1f;
    private static final boolean DEFAULT_IS_NIGHT_MODE = false;
    private static final int DEFAULT_TURN_TYPE = 0;

    public static void saveTextSize(float textSize) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_TEXT_SIZE, textSize);
        editor.apply();
    }

    public static void saveIs_first(int isfirst) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_IS_FIRST, isfirst);
        editor.apply();
    }

    public static int getIsfirst() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_IS_FIRST, IS_FIRST);
    }

    public static void saveRead_first(int isfirst) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_READ_FIRST, isfirst);
        editor.apply();
    }

    public static int getReadfirst() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_READ_FIRST, READ_FIRST);
    }

    public static void saveTextStyle(String textSize_path) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TEXTSTYLE_TYPE, textSize_path);
        editor.apply();
    }

    public static float getTextSize() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getFloat(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
    }

    public static void saveWebsite(String textSize_path) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_WENSITE, textSize_path);
        editor.apply();
    }

    public static String getWebsite() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_WENSITE, WEBSITE);
    }

    public static void saveRowSpace(float rowSpace) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_ROW_SPACE, rowSpace);
        editor.apply();
    }

    public static float getRowSpace() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getFloat(KEY_ROW_SPACE, DEFAULT_ROW_SPACE);
    }

    public static void saveTheme(int theme) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, theme);
        editor.apply();
    }

    public static int getTheme() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_THEME, DEFAULT_THEME);
    }

    public static String getTextStyle() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TEXTSTYLE_TYPE, "-1");
    }

    public static void saveBrightness(float brightness) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_BRIGHTNESS, brightness);
        editor.apply();
    }

    public static float getBrightness() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getFloat(KEY_BRIGHTNESS, DEFAULT_BRIGHTNESS);
    }

    public static void saveIsNightMode(boolean isNightMode) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_NIGHT_MODE, isNightMode);
        editor.apply();
    }

    public static void saveIsSysNightMode(boolean isNightMode) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_SYS_NIGHT_MODE, isNightMode);
        editor.apply();
    }

    public static boolean getIsSysNightMode() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_IS_SYS_NIGHT_MODE, DEFAULT_IS_NIGHT_MODE);
    }


    public static boolean getIsNightMode() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_IS_NIGHT_MODE, DEFAULT_IS_NIGHT_MODE);
    }

    public static void saveTurnType(int turnType) {
        SharedPreferences.Editor editor = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_TURN_TYPE, turnType);
        editor.apply();
    }

    public static int getTurnType() {
        SharedPreferences sp = App.getContext()
                .getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_TURN_TYPE, DEFAULT_TURN_TYPE);
    }

    //private final static String FILE_NAME = "data_save";
    private final static String KEY = "admin";
    private final static String KEY2 = "url";

    /**
     * desc:保存对象
     * @param context
     * @param obj
     * modified:
     */
    public static void saveObject(Context context,Object obj){
        try {
            // 保存对象
            SharedPreferences.Editor sharedata = context.getSharedPreferences(NAME, 0).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            sharedata.putString(KEY, bytesToHexString);
            sharedata.commit();
        } catch (Exception e) {
        }
    }

    /**
     * desc:将数组转为16进制
     * @param bArray
     * @return
     * modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if(bArray == null){
            return null;
        }
        if(bArray.length == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:保存对象
     * @param context
     * @param obj
     * modified:
     */
    public static void saveObject2(Context context,Object obj){
        try {
            // 保存对象
            SharedPreferences.Editor sharedata = context.getSharedPreferences(NAME, 0).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            sharedata.putString(KEY2, bytesToHexString);
            sharedata.commit();
        } catch (Exception e) {
        }
    }


    /**
     * desc:获取保存的Object对象
     * @param context
     * @return
     * modified:
     */
    public static Object readObject(Context context){
        try {
            SharedPreferences sharedata = context.getSharedPreferences(NAME, 0);
            if (sharedata.contains(KEY)) {
                String string = sharedata.getString(KEY, "");
                if(TextUtils.isEmpty(string)){
                    return null;
                }else{
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is=new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (Exception e) {
        }
        //所有异常返回null
        return null;

    }

     /* desc:获取保存的Object对象
     * @param context
     * @return
             * modified:
            */
    public static Object readObject2(Context context){
        try {
            SharedPreferences sharedata = context.getSharedPreferences(NAME, 0);
            if (sharedata.contains(KEY2)) {
                String string = sharedata.getString(KEY2, "");
                if(TextUtils.isEmpty(string)){
                    return null;
                }else{
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is=new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (Exception e) {
        }
        //所有异常返回null
        return null;

    }

    public static byte[] StringToBytes(String data){
        String hexString=data.toUpperCase().trim();
        if (hexString.length()%2!=0) {
            return null;
        }
        byte[] retData=new byte[hexString.length()/2];
        for(int i=0;i<hexString.length();i++)
        {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch3;
            if(hex_char1 >= '0' && hex_char1 <='9')
                int_ch3 = (hex_char1-48)*16;   //// 0 的Ascll - 48
            else if(hex_char1 >= 'A' && hex_char1 <='F')
                int_ch3 = (hex_char1-55)*16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch4;
            if(hex_char2 >= '0' && hex_char2 <='9')
                int_ch4 = (hex_char2-48); //// 0 的Ascll - 48
            else if(hex_char2 >= 'A' && hex_char2 <='F')
                int_ch4 = hex_char2-55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch3+int_ch4;
            retData[i/2]=(byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }
}
