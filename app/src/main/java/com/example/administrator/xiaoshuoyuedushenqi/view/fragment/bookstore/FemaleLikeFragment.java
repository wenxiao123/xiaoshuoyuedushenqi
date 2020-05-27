package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.bookstore;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookstoreAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleLikeContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Data;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.MaleLikePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.LogUtils;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.widget.VerticalTabLayout1;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class FemaleLikeFragment extends BaseFragment<MaleLikePresenter> implements IMaleLikeContract.View {
    VerticalTabLayout1 verticalTabLayout;
    List<Catagorys> dataList = new ArrayList<>();
    List<Catagorys> catagorys=new ArrayList<>();
    RecyclerView recyclerView;
    BookstoreAdapter  bookstoreAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_femalelike;
    }

    @Override
    protected void initData() {
    }
    List<String> titles=new ArrayList<>();
    @Override
    protected void initView() {
        verticalTabLayout = getActivity().findViewById(R.id.tablayout1);
        verticalTabLayout.addTab(new QTabView(getActivity()));
        verticalTabLayout.addOnTabSelectedListener(new VerticalTabLayout1.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                //getDataList(getPosition(dataList.get(position).getTitle()));
                //initBookstore();
                getCategoryNovels(dataList.get(position).getId());
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });

        recyclerView=getActivity().findViewById(R.id.grid_fenlei1);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

    }
    public  List<Catagorys> getDataList(int position){
        catagorys.clear();
        for(Catagorys c:dataList){
            if(c.getPid()==position){
                catagorys.add(c);
            }
        }
        return  catagorys;
    }
    public void getCategoryNovels(int pid) {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl()+"/api/index/Type_one";
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", pid+"")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    Log.e("ASA", "onResponse: " + json);
                    Data bean = mGson.fromJson(json, Data.class);
                    List<Catagorys> catagorysList = bean.getData();
                   getCategoryNovelsSuccess2(catagorysList);
                }catch (Exception ex){
                   getCategoryNovelsError2("获取分类小说失败");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCategoryNovelsError2("获取分类小说失败");
            }
        });
    }

    private void getCategoryNovelsError2(String s) {
        showShortToast(s);
    }

    private void getCategoryNovelsSuccess2(List<Catagorys> catagorysList) {
        catagorys.clear();
        catagorys=catagorysList;
        initBookstore();
    }

    public int getPosition(String title){
        int z=0;
        for(Catagorys c:dataList){
            if(c.getTitle().equals(title)){
                z=c.getId();
            }
        }
        return  z;
    }
    public void initBookstore(){
        bookstoreAdapter=new BookstoreAdapter(getContext(), catagorys, new BookstoreAdapter.BookshelfNovelListener() {
            @Override
            public void clickItem(int position) {
                Intent intent=new Intent(getActivity(), FenleiNovelActivity.class);
                intent.putExtra("category_id",catagorys.get(position).getId());
                intent.putExtra("category_name",catagorys.get(position).getTitle());
                getActivity().startActivity(intent);
            }

            @Override
            public void longClick(int position) {

            }
        });
        recyclerView.setAdapter(bookstoreAdapter);
    }
    @Override
    protected void doInOnCreate() {
        mPresenter.getCategoryNovels(Constant.female);
    }

    @Override
    protected MaleLikePresenter getPresenter() {
        return new MaleLikePresenter();
    }


    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void getCategoryNovelsSuccess(List<Catagorys> dataList) {
        this.dataList = dataList;
//        int z = -1;
//        for (int i = 0; i < dataList.size(); i++) {
//            if (dataList.get(i).getId()== Constant.female) {
//                z = dataList.get(i).getId();
//                break;
//            }
//        }
//        int finalZ = z;
//        for (int i = 0; i < dataList.size(); i++) {
//            if (dataList.get(i).getPid() == finalZ) {
//                titles.add(dataList.get(i).getTitle()) ;
//            }
//        }

        verticalTabLayout.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return dataList.size();
            }

            @Override
            public TabView.TabBadge getBadge(int position) {
                return null;
            }

            @Override
            public QTabView.TabIcon getIcon(int position) {
                return null;
            }

            @Override
            public QTabView.TabTitle getTitle(int position) {
                return new QTabView.TabTitle.Builder()
                        .setContent(dataList.get(position).getTitle())
                        .setTextColor(getContext().getResources().getColor(R.color.red_aa), getContext().getResources().getColor(R.color.grey_a1))
                        .setTextSize(14)
                        .build();
            }

            @Override
            public int getBackground(int position) {
                return R.color.white;
            }
        });
        //getDataList(getPosition(dataList.get(0).getTitle()));
        getCategoryNovels(dataList.get(0).getId());
    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {

    }
}
