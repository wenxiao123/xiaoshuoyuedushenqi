package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainRecyleAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainSetAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.SortAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.FileInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.PinyinUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.RecycleViewDivider;
import com.example.administrator.xiaoshuoyuedushenqi.util.SDCardHelper;
import com.example.administrator.xiaoshuoyuedushenqi.util.SideBar;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TakePhotosDialog;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;
import com.fingerth.supdialogutils.SYSDiaLogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AdminSetActivity extends BaseActivity {
    MainSetAdapter mainRecyleAdapter1;
    MainSetAdapter mainRecyleAdapter2;
    MainSetAdapter mainRecyleAdapter3;
    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    String[] strings1 = {"修改头像", "修改昵称"};
    String[] ints1 = {"", ""};
    String[] strings2 = {"检查更新", "免责声明"};
    String[] ints2 = {"v 1.0.0", ""};
    String[] strings3 = {"清理缓存"};
    String[] ints3 = {"0.00"};

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.admin_set;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    LinearLayout mSettingBarCv;

    String img_name;

    @Override
    protected void initView() {
        recyclerView1 = findViewById(R.id.recycle_part1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        recyclerView2 = findViewById(R.id.recycle_part2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        recyclerView3 = findViewById(R.id.recycle_part3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);

        TextView camera = findViewById(R.id.camera);
        TextView picture = findViewById(R.id.picture);
        TextView consle = findViewById(R.id.consle);
        consle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSettingBar();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_name = "img" + System.currentTimeMillis() + "";
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(AdminSetActivity.this, Manifest.permission.CAMERA);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AdminSetActivity.this, new String[]{Manifest.permission.CAMERA}, 3);
                        return;
                    } else {
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
                        startActivityForResult(openCameraIntent, 3); // 参数常量为自定义的request code, 在取返回结果时有用
                    }
                } else {
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
                    startActivityForResult(openCameraIntent, 3); // 参数常量为自定义的request code, 在取返回结果时有用
                }
                hideSettingBar();
            }
        });
    }

    /**
     * 显示设置栏
     */
    private void showSettingBar() {
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mSettingBarCv.startAnimation(bottomAnim);
        mSettingBarCv.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏设置栏
     */
    private void hideSettingBar() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSettingBarCv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSettingBarCv.startAnimation(bottomExitAnim);
    }

    @Override
    protected void doAfterInit() {
        mainRecyleAdapter1 = new MainSetAdapter(this, ints1, strings1);
        recyclerView1.setAdapter(mainRecyleAdapter1);

        mainRecyleAdapter1.setOnCatalogListener(new MainSetAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        showSettingBar();
                        break;
                    case 1:
                        startActivity(new Intent(AdminSetActivity.this,MdifyNicknameActivity.class));
                        break;
                }
            }
        });
        mainRecyleAdapter2 = new MainSetAdapter(this, ints2, strings2);
        recyclerView2.setAdapter(mainRecyleAdapter2);

        mainRecyleAdapter2.setOnCatalogListener(new MainSetAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        break;
                }
            }
        });
        mainRecyleAdapter3 = new MainSetAdapter(this, ints3, strings3);
        recyclerView3.setAdapter(mainRecyleAdapter3);

        mainRecyleAdapter3.setOnCatalogListener(new MainSetAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        final TipDialog tipDialog = new TipDialog.Builder(AdminSetActivity.this)
                                .setContent("是否清除缓存")
                                .setCancel("取消")
                                .setEnsure("确定")
                                .setOnClickListener(new TipDialog.OnClickListener() {
                                    @Override
                                    public void clickEnsure() {
                                        FileUtil.clearLocalCache();
                                    }

                                    @Override
                                    public void clickCancel() {

                                    }
                                })
                                .build();
                        tipDialog.show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                final String path = getRealPathFromURI(uri);
                File file = new File(path);
                img_name = "img" + System.currentTimeMillis() + "";
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
                return;
            }
            hideSettingBar();
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap thumbnail = null;
                    if (data.hasExtra("data")) {
                        thumbnail = data.getParcelableExtra("data");

                    }
                    String path = saveBitmap(AdminSetActivity.this, thumbnail);
                    File file = new File(path);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
                return;
            }
           hideSettingBar();
        }
    }

    private static final String SD_PATH = "/sdcard/xsb/pic/";

    public String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + "/aaa/";
        }
        try {
            filePic = new File(savePath + img_name + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    private String getRealPathFromURI(Uri contentUri) { //传入图片uri地址
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

}
