package com.example.administrator.xiaoshuoyuedushenqi.weyue.utils;


import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.PageView;

/**
 * Created by newbiechen on 17-5-17.
 * 阅读器的配置管理
 */

public class ReadSettingManager {
    /*************实在想不出什么好记的命名方式。。******************/
    public static final int READ_BG_DEFAULT = 0;
    public static final int READ_BG_1 = 1;
    public static final int READ_BG_2 = 2;
    public static final int READ_BG_3 = 3;
    public static final int READ_BG_4 = 4;
    public static final int NIGHT_MODE = 5;

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_TEXT_ROW = "shared_read_text_row";
    public static final String SHARED_READ_TEXT_Style = "shared_read_text_style";
    public static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";

    private static volatile ReadSettingManager sInstance;

    private SharedPreUtils sharedPreUtils;
    public static ReadSettingManager getInstance(){
        if (sInstance == null){
            synchronized (ReadSettingManager.class){
                if (sInstance == null){
                    sInstance = new ReadSettingManager();
                }
            }
        }
        return sInstance;
    }

    private ReadSettingManager(){
        sharedPreUtils = SharedPreUtils.getInstance();
    }

    public void setReadBackground(int theme){
        sharedPreUtils.putInt(SHARED_READ_BG,theme);
    }

    public void setBrightness(int progress){
        sharedPreUtils.putInt(SHARED_READ_BRIGHTNESS,progress);
    }

    public void setAutoBrightness(boolean isAuto){
        sharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO,isAuto);
    }

    public void setDefaultTextSize(boolean isDefault){
        sharedPreUtils.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public void setTextSize(int textSize){
        sharedPreUtils.putInt(SHARED_READ_TEXT_SIZE,textSize);
    }
    public void setTextRow(int textSize){
        sharedPreUtils.putInt(SHARED_READ_TEXT_ROW,textSize);
    }

    public void setTextStyle(String textSize){
        sharedPreUtils.putString(SHARED_READ_TEXT_Style,textSize);
    }

    public void setPageMode(int mode){
        sharedPreUtils.putInt(SHARED_READ_PAGE_MODE,mode);
    }

    public void setNightMode(boolean isNight){
        sharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE,isNight);
    }

    public int getBrightness(){
        return sharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40);
    }

    public boolean isBrightnessAuto(){
        return sharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false);
    }

    public int getTextSize(){
        return sharedPreUtils.getInt(SHARED_READ_TEXT_SIZE,3);//App.getAppResources().getDimensionPixelOffset(R.dimen.dp_30)
    }

    public int getTextRow(){
        return sharedPreUtils.getInt(SHARED_READ_TEXT_ROW, 2);
    }

    public String getTextStyle(){
        return sharedPreUtils.getString(SHARED_READ_TEXT_Style,"");
    }
    public boolean isDefaultTextSize(){
        return sharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public int getPageMode(){
        return sharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageView.PAGE_MODE_COVER);
    }

    public int getReadBgTheme(){
        return sharedPreUtils.getInt(SHARED_READ_BG, READ_BG_4);
    }

    public boolean isNightMode(){
        return sharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn){
        sharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE,isTurn);
    }

    public boolean isVolumeTurnPage(){
        return sharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen){
        sharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN,isFullScreen);
    }

    public boolean isFullScreen(){
        return sharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN,false);
    }
}
