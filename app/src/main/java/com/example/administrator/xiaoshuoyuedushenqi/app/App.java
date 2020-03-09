package com.example.administrator.xiaoshuoyuedushenqi.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.administrator.xiaoshuoyuedushenqi.util.CrashHandler;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * @author WX
 * Created on 2020/2/28
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // 为应用设置异常处理
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init();

        context = getApplicationContext();

//        Bmob.initialize(this, "ce63bdbbd4197409b82920b0835a42eb");
//        BmobUpdateAgent.setUpdateCheckConfig(false);
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

}
