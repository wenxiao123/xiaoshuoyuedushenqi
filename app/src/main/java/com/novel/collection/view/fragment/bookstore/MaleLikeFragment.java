package com.novel.collection.view.fragment.bookstore;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.collection.R;
import com.novel.collection.adapter.BookstoreAdapter;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IMaleLikeContract;
import com.novel.collection.entity.bean.Catagorys;
import com.novel.collection.entity.bean.Data;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.MaleLikePresenter;
import com.novel.collection.view.activity.FenleiNovelActivity;
import com.novel.collection.widget.VerticalTabLayout1;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
//import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class MaleLikeFragment extends BaseFragment<MaleLikePresenter> implements IMaleLikeContract.View {
    VerticalTabLayout1 verticalTabLayout;
    List<Catagorys> dataList = new ArrayList<>();
    List<Catagorys> catagorys=new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayout lin_male;
    ProgressBar pb_novel;
    BookstoreAdapter bookstoreAdapter;
    TextView tv_begain_reser;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_malelike;
    }

    @Override
    protected void initData() {
    }
    List<String> titles=new ArrayList<>();
    @Override
    protected void initView() {
        lin_male=getActivity().findViewById(R.id.lin_male);
        tv_begain_reser=getActivity().findViewById(R.id.tv_begain_reser);
        tv_begain_reser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getCategoryNovels(Constant.male);
            }
        });
        pb_novel=getActivity().findViewById(R.id.pb_novel);
        verticalTabLayout = getActivity().findViewById(R.id.tablayout_male);
        verticalTabLayout.addTab(new QTabView(getActivity()));
        verticalTabLayout.addOnTabSelectedListener(new VerticalTabLayout1.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
//                getDataList(getPosition(dataList.get(position).getTitle()));
//                initBookstore();
                getCategoryNovels(dataList.get(position).getId());
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
        recyclerView=getActivity().findViewById(R.id.grid_fenlei);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

    }

    @Override
    protected void doInOnCreate() {
        mPresenter.getCategoryNovels(Constant.male);
    }

    @Override
    protected MaleLikePresenter getPresenter() {
        return new MaleLikePresenter();
    }


    @Override
    protected boolean isRegisterEventBus() {
        return false;
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
        bookstoreAdapter=new BookstoreAdapter(getContext(),catagorys, new BookstoreAdapter.BookshelfNovelListener() {
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
    public void getCategoryNovelsSuccess(List<Catagorys> dataList) {
        tv_begain_reser.setVisibility(View.GONE);
        this.dataList = dataList;
//        int z = -1;
//        for (int i = 0; i < dataList.size(); i++) {
//            if (dataList.get(i).getId()==Constant.male) {
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
                QTabView.TabTitle tabTitle=new QTabView.TabTitle.Builder()
                        .setContent(dataList.get(position).getTitle())
                        .setTextColor(getContext().getResources().getColor(R.color.red_aa), getContext().getResources().getColor(R.color.grey_a1))
                        .setTextSize(14)
                        .build();
                return tabTitle;
            }

            @Override
            public int getBackground(int position) {
                return R.color.white;
            }
        });
        verticalTabLayout.setTabSelected(0);
//        getDataList(getPosition(dataList.get(0).getTitle()));
//        initBookstore();
          getCategoryNovels(dataList.get(0).getId());
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
        lin_male.setVisibility(View.VISIBLE);
        pb_novel.setVisibility(View.GONE);
        catagorys.clear();
        catagorys=catagorysList;
        initBookstore();
    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {
       lin_male.setVisibility(View.GONE);
       pb_novel.setVisibility(View.GONE);
       tv_begain_reser.setVisibility(View.VISIBLE);
       showShortToast("数据请求失败，请重新再试");
    }
}
