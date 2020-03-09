package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.SortAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.FileInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.PinyinUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.SDCardHelper;
import com.example.administrator.xiaoshuoyuedushenqi.util.SideBar;
import com.fingerth.supdialogutils.SYSDiaLogUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BookCataloActivity extends BaseActivity {
    private DatabaseManager mDbManager;
    private ListView listView;
    private SortAdapter sortadapter;
    private List<PersonBean> data;
    private SideBar sidebar;
    List sdcardList;
    private String cd_path="/sdcard/";
    HashMap hashMap=new HashMap();
    int i=0;
    @Override
    protected void doBeforeSetContentView() {
        sdcardList = SDCardHelper.GetStorageList(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bookcatalo;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
    }

    Handler handler =new Handler()
    {
        //在handle重写handleMessage方法
        public void handleMessage(Message msg) {
            //通过settext方法实现UI交互
            sortadapter = new SortAdapter(BookCataloActivity.this, data);
            listView.setAdapter(sortadapter);
            SYSDiaLogUtils.dismissProgress();   //停止
        };
    };


    @Override
    protected void initView() {
        hashMap.put(i,cd_path);
        sidebar = (SideBar) findViewById(R.id.sidebar);
        listView = (ListView) findViewById(R.id.listview);
        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = sortadapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        File file = new File("/sdcard/");
        data = getData(getLocalFileInfo(file));
        // 数据在放在adapter之前需要排序
        Collections.sort(data, new PinyinComparator());
        sortadapter = new SortAdapter(this, data);
        listView.setAdapter(sortadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i++;
                hashMap.put(i,data.get(position).getFilepath());
                if(data.get(position).getFiletype().equals("Directory")){
                    data = getData(getLocalFileInfo(new File(data.get(position).getFilepath())));
                    // 数据在放在adapter之前需要排序
                    Collections.sort(data, new PinyinComparator());
                    sortadapter = new SortAdapter(BookCataloActivity.this, data);
                    listView.setAdapter(sortadapter);
                }else{
                    Intent intent=new Intent(BookCataloActivity.this,MainActivity.class);
                    intent.putExtra("islaod","1");
                    intent.putExtra("personBean", (Serializable) data.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.iv_bookshelf_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils fileUtils=new FileUtils();
                        fileUtils.getSpecificTypeOfFile(BookCataloActivity.this, new String[]{".txt"});
                        data=getData(fileUtils.getFileInfoList());
                        Collections.sort(data, new PinyinComparator());
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                SYSDiaLogUtils.showProgressDialog(BookCataloActivity.this,
                        SYSDiaLogUtils.SYSDiaLogType.IosType, "扫描中...", false, new DialogInterface.OnCancelListener() { @Override public void onCancel(DialogInterface dialog) {
                    //Toast.makeText(MainActivity.this, "點擊消失", Toast.LENGTH_SHORT).show();
                } });
//                new Thread(){
//                    //新建一个子线程
//                    public void run() {
//                        data=getData(getLocalTxtFileInfo(new File("/sdcard/")));
//                        Collections.sort(data, new PinyinComparator());
//                        handler.sendEmptyMessage(1);
//                    };
//                }.start();//调用start方法
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --i;
                if(i==-1){
                   finish();
                }else {
                Log.e("AAA", "onClick: "+(String) hashMap.get(i)+" "+i);
                data = getData(getLocalFileInfo(new File((String) hashMap.get(i))));
                // 数据在放在adapter之前需要排序
                Collections.sort(data, new PinyinComparator());
                sortadapter = new SortAdapter(BookCataloActivity.this, data);
                listView.setAdapter(sortadapter);
            }
            }
        });
    }

    private List<PersonBean> getData(List<FileInfo> data) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < data.size(); i++) {
            String pinyin = PinyinUtils.getPingYin(data.get(i).getFilename());
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(data.get(i).getFilename());
            person.setPinYin(pinyin);
            person.setFiletype(data.get(i).getFiletype());
            person.setFilepath(data.get(i).getFilepath());
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }

    @Override
    protected void doAfterInit() {

    }

    //遍历文件或文件家分别把路径保存好
    public List<FileInfo> getLocalFileInfo(File path) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        File[] files = path.listFiles();// 读取
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    fileInfoList.add(new FileInfo(file.getName(), file.getPath(), "Directory", "0"));
                } else if (file.getName().endsWith(".txt")) {
                    fileInfoList.add(new FileInfo(file.getName(), file.getPath(), "txt", "0"));
                }

            }
        }
        return fileInfoList;
    }
    List<FileInfo> fileInfoList = new ArrayList<>();
    //遍历文件或文件家分别把路径保存好
    public List<FileInfo> getLocalTxtFileInfo(File path) {
        File[] files = path.listFiles();// 读取
        if (files != null) {
            // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    getLocalTxtFileInfo(new File(file.getPath()));
                } else {
                    if (file.getName().endsWith(".txt")) {
                        fileInfoList.add(new FileInfo(file.getName(), file.getPath(), "txt", "0"));
                    }
                }

            }
        }
        return fileInfoList;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    public class PinyinComparator implements Comparator<PersonBean> {

        public int compare(PersonBean o1, PersonBean o2) {
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            if (o2.getFirstPinYin().equals("#")) {
                return -1;
            } else if (o1.getFirstPinYin().equals("#")) {
                return 1;
            } else {
                return o1.getFirstPinYin().compareTo(o2.getFirstPinYin());
            }
        }
    }
}
