package com.example.administrator.xiaoshuoyuedushenqi.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.arialyy.aria.core.Aria;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.util.CrashHandler;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * @author
 * Created on 2020/2/28
 */
public class App extends Application {

    private static Context context;
    List<Cataloginfo> catalogDataAll=new ArrayList<>();
    boolean isNight;
    private static App app;

    public static Context getAppContext() {
        return app;
    }

    public static Resources getAppResources() {
        return app.getResources();
    }
    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public List<Cataloginfo> getCatalogDataAll() {
        return catalogDataAll;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String  content;

    public void setCatalogDataAll(List<Cataloginfo> catalogDataAll) {
        this.catalogDataAll = catalogDataAll;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 为应用设置异常处理
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init();
        app=this;
        context = getApplicationContext();
        Aria.init(this);
//        init(getContext());//
//        if(SpUtil.getIsSysNightMode()==true){
//            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );
//        }else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//        Bmob.initialize(this, "ce63bdbbd4197409b82920b0835a42eb");
//        BmobUpdateAgent.setUpdateCheckConfig(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CrashReport.initCrashReport(getApplicationContext(), "cc93699e71", false);
            }
        }).start();
    }

    public static Context getContext() {
        return context;
    }

    private static App instance;
    private static List<Activity> activityList = new LinkedList<Activity>();
    //单例模式中获取唯一的MyApplication实例
    public static App getInstance() {
        if(null == instance) {
            instance = new App();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity)  {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for(Activity activity:activityList) {
            activity.finish();
        }
        System.exit(0);
        activityList.clear();
    }
    public static void removeActivity(Activity acti) {
        int index = -1;
        if ((index = activityList.indexOf(acti)) != -1) {
            activityList.remove(index).finish();
        }
    }
    private static Resources sRes;
    public static void init(Context context) {
        sRes = context.getResources();
    }
    /**
     * 切换 夜间模式
     * @param on true 夜间， false  日间
     */
    public static void updateNightMode(boolean on) {
        DisplayMetrics dm = sRes.getDisplayMetrics();
        Configuration config = sRes.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        sRes.updateConfiguration(config, dm);
    }

}
