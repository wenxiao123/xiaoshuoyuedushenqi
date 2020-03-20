package com.example.administrator.xiaoshuoyuedushenqi.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.common.RequestEnum;
import com.arialyy.aria.core.task.DownloadTask;
import com.arialyy.aria.util.CommonUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;

import java.io.File;

public class AnyRunnModule {
    String TAG = "AnyRunnModule";
    private Context mContext;
    private String mUrl;
    private long mTaskId = -1;
    TextView textView;
    public AnyRunnModule(Context context) {
        Aria.download(this).register();
        mContext = context;
    }
    public void checkPemission() {
            boolean isGranted = true;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //如果没有写sd卡权限
                    isGranted = false;
                }
                if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                }
                Log.i("cbs","isGranted == "+isGranted);
                if (!isGranted) {
                    ((Activity)mContext).requestPermissions(
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                    .ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            102);
                }
            }

    }
    @Download.onWait void onWait(DownloadTask task) {
        Log.d(TAG, "wait ==> " + task.getDownloadEntity().getFileName());
    }

    @Download.onPre protected void onPre(DownloadTask task) {
        Log.d(TAG, "onPre");
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        Log.d(TAG, "onStart");
    }

    @Download.onTaskRunning protected void running(DownloadTask task) {
        Log.d(TAG, "running"+task.getPercent());
        if(textView!=null&&task!=null) {
            textView.setText(task.getPercent() + "%");
        }
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        Log.d(TAG, "resume");
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        Log.d(TAG, "stop");
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        Log.d(TAG, "cancel");
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        Log.d(TAG, "fail"+task.getKey());
        checkPemission();
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        Log.d(TAG, "ok");
        textView.setVisibility(View.GONE);
        SpUtil.saveTextStyle(mUrl);
        if(mContext!=null){
            ((ReadActivity)mContext).listText();
        }
    }


   public void start(String url,String filepath,TextView t1) {
        textView=t1;
        mUrl = filepath;
        mTaskId = Aria.download(this)
                .load(url)
                .setFilePath(filepath)
                .resetState()
                .create();
    }

    public void stop() {
        Aria.download(this).load(mTaskId).stop();
    }

    public void cancel() {
        Aria.download(this).load(mTaskId).cancel();
    }

    public void unRegister() {
        Aria.download(this).unRegister();
    }
}
