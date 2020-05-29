package com.novel.collection.weyue.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.novel.collection.R;
import com.novel.collection.app.App;
import com.novel.collection.weyue.widget.theme.Theme;

/**
 * Created by dongjunkun on 2016/2/6.
 */
public class ThemeUtils {
    public static int getThemeColor2Array(Context context, int attrRes) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{attrRes});
        int color = typedArray.getColor(0, 0xffffff);
        typedArray.recycle();
        return color;
    }

    /**
     * 获取主题颜色（color）
     *
     * @return
     */
    public static int getThemeColor() {
        Theme theme = SharedPreUtils.getInstance().getCurrentTheme();
        switch (theme) {
            case Blue:
                return App.getAppResources().getColor(R.color.colorBluePrimary);
            case Red:
                return App.getAppResources().getColor(R.color.colorRedPrimary);
            case Brown:
                return App.getAppResources().getColor(R.color.colorBrownPrimary);
            case Green:
                return App.getAppResources().getColor(R.color.colorGreenPrimary);
            case Purple:
                return App.getAppResources().getColor(R.color.colorPurplePrimary);
            case Teal:
                return App.getAppResources().getColor(R.color.colorTealPrimary);
            case Pink:
                return App.getAppResources().getColor(R.color.colorPinkPrimary);
            case DeepPurple:
                return App.getAppResources().getColor(R.color.colorDeepPurplePrimary);
            case Orange:
                return App.getAppResources().getColor(R.color.colorOrangePrimary);
            case Indigo:
                return App.getAppResources().getColor(R.color.colorIndigoPrimary);
            case LightGreen:
                return App.getAppResources().getColor(R.color.colorLightGreenPrimary);
            case Lime:
                return App.getAppResources().getColor(R.color.colorLimePrimary);
            case DeepOrange:
                return App.getAppResources().getColor(R.color.colorDeepOrangePrimary);
            case Cyan:
                return App.getAppResources().getColor(R.color.colorCyanPrimary);
            case BlueGrey:
                return App.getAppResources().getColor(R.color.colorBlueGreyPrimary);

        }
        return App.getAppResources().getColor(R.color.colorCyanPrimary);
    }

    /**
     * 获取主题颜色（color）
     *
     * @return
     */
    public static int getThemeColorId() {
        Theme theme = SharedPreUtils.getInstance().getCurrentTheme();
        switch (theme) {
            case Blue:
                return R.color.colorBluePrimary;
            case Red:
                return R.color.colorRedPrimary;
            case Brown:
                return R.color.colorBrownPrimary;
            case Green:
                return R.color.colorGreenPrimary;
            case Purple:
                return R.color.colorPurplePrimary;
            case Teal:
                return R.color.colorTealPrimary;
            case Pink:
                return R.color.colorPinkPrimary;
            case DeepPurple:
                return R.color.colorDeepPurplePrimary;
            case Orange:
                return R.color.colorOrangePrimary;
            case Indigo:
                return R.color.colorIndigoPrimary;
            case LightGreen:
                return R.color.colorLightGreenPrimary;
            case Lime:
                return R.color.colorLimePrimary;
            case DeepOrange:
                return R.color.colorDeepOrangePrimary;
            case Cyan:
                return R.color.colorCyanPrimary;
            case BlueGrey:
                return R.color.colorBlueGreyPrimary;

        }
        return R.color.colorCyanPrimary;
    }
}
