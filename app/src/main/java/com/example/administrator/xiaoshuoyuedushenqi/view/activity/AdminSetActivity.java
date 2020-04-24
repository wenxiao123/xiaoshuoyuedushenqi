package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainSetAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.AppUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileSizeUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.FileUtils;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

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
    Login_admin login_admin;
    TextView exit;
    DatabaseManager manager;

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
        login_admin = (Login_admin) SpUtil.readObject(this);
        manager = DatabaseManager.getInstance();
    }

    LinearLayout mSettingBarCv;

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    String img_name;

    @SuppressLint("WrongConstant")
    @Override
    protected void initView() {
        recyclerView1 = findViewById(R.id.recycle_part1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        recyclerView2 = findViewById(R.id.recycle_part2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        recyclerView3 = findViewById(R.id.recycle_part3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);
        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TipDialog tipDialog = new TipDialog.Builder(AdminSetActivity.this)
                        .setContent("是否退出登录？")
                        .setCancel("否")
                        .setEnsure("是")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                postExit();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        });
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
        findViewById(R.id.back2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        exit.setVisibility(View.GONE);
        backgroundAlpha(0.5f);
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
                exit.setVisibility(View.VISIBLE);
                backgroundAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSettingBarCv.startAnimation(bottomExitAnim);
    }

    private void showPupowindpw(View parent) {
        exit.setVisibility(View.GONE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_camera, null);
        TextView tv_camera = view.findViewById(R.id.camera);
        TextView tv_pivture = view.findViewById(R.id.picture);
        final PopupWindow popupWindow = new PopupWindow(view, (int) (parent.getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        // 使其聚集,可点击
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                popupWindow.dismiss();
            }
        });
        TextView consle = view.findViewById(R.id.consle);
        consle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_pivture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        popupWindow.setFocusable(false);
        popupWindow.setAnimationStyle(R.style.dialog_animation);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        backgroundAlpha(0.5f);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        //popupWindow.showAsDropDown(parent, (int) (parent.getWidth() * 0.7), 35);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                exit.setVisibility(View.VISIBLE);
                backgroundAlpha(1f);
            }
        });
    }

    @Override
    protected void doAfterInit() {
        mainRecyleAdapter1 = new MainSetAdapter(this, ints1, strings1);
        recyclerView1.setAdapter(mainRecyleAdapter1);

        mainRecyleAdapter1.setOnCatalogListener(new MainSetAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (login_admin == null) {
                    startActivity(new Intent(AdminSetActivity.this, LoginActivity.class));
                } else {
                    switch (position) {
                        case 0:
                            //showSettingBar();
                            showPupowindpw(recyclerView3);
                            break;
                        case 1:
                            startActivity(new Intent(AdminSetActivity.this, MdifyNicknameActivity.class));
                            break;
                    }
                }
            }
        });
        ints2[0] = "v " + getVersionName(this);
        mainRecyleAdapter2 = new MainSetAdapter(this, ints2, strings2);
        recyclerView2.setAdapter(mainRecyleAdapter2);
        mainRecyleAdapter2.setOnCatalogListener(new MainSetAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        Log.e("WWW", "clickItem: "+ AppUtils.getAppName(AdminSetActivity.this)+" "+AppUtils.getPackageName(AdminSetActivity.this)
                        +" "+AppUtils.getVersionName(AdminSetActivity.this)+" "+AppUtils.getVersionCode(AdminSetActivity.this));
                        showShortToast("暂无更新");
                        break;
                    case 1:
                        Intent intent = new Intent(AdminSetActivity.this, WebActivity.class);
                        intent.putExtra("type", "1");
                        intent.putExtra("title", "免责声明");
                        startActivity(intent);
                        break;
                }
            }
        });
        ints3[0] = FileSizeUtil.getFileOrFilesSize(Constant.BOOK_OTHER_CACHE_PATH, FileSizeUtil.SIZETYPE_MB) + " MB";
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
                                        //showShortToast("暂未开放");
                                        File file = new File(FileUtils.getCachePath() + File.separator);
                                        //file.delete();
                                        deleteDirWihtFile(file);
                                        ints3[0]="0.00 MB";
                                        mainRecyleAdapter3.notifyDataSetChanged();
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
        if (login_admin == null) {
            exit.setVisibility(View.GONE);
        }
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
                uploadpost2(file);
                //upload(file.getPath());
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
                    uploadpost2(file);
                    //upload(file.getPath());
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消操作", Toast.LENGTH_LONG).show();
                return;
            }
            hideSettingBar();
        }
    }
    //删除文件夹和文件夹里面的文件
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public void postModify(String nickname, String name) {
        String url = UrlObtainer.GetUrl() + "api/user/profile";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .add(nickname, name)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        showShortToast("修改成功");
                    } else {
                        showShortToast("修改失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }

    public void postExit() {
        String url = UrlObtainer.GetUrl() + "api/user/logout";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        SpUtil.saveObject(AdminSetActivity.this, null);
                        startActivity(new Intent(AdminSetActivity.this, LoginActivity.class));
                    } else {
                        showShortToast("退出失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }

    void uploadpost2(File file) {
        if (login_admin != null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("*/*"), file);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .addFormDataPart("token", login_admin.getToken())
                    .build();

            String url = UrlObtainer.GetUrl() + "api/index/upload";
            OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
                @Override
                public void onResponse(String json) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String code = jsonObject.getString("code");
                        if (code.equals("1")) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            String path = object.getString("path");
                            postModify("avatar", path);
                        } else {
                            showShortToast("上传失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    showShortToast(errorMsg);
                }
            });
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
                    + "/NovalReader/";
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

    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

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
