package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.bookstore;

import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookstoreAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleLikeContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.MaleLikePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class MaleLikeFragment extends BaseFragment<MaleLikePresenter> implements IMaleLikeContract.View {
    VerticalTabLayout verticalTabLayout;
    List<Catagorys> dataList = new ArrayList<>();
    List<Catagorys> catagorys=new ArrayList<>();
    RecyclerView recyclerView;
    BookstoreAdapter bookstoreAdapter;
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
        verticalTabLayout = getActivity().findViewById(R.id.tablayout);

        verticalTabLayout.addTab(new QTabView(getActivity()));
        verticalTabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                getDataList(getPosition(titles.get(position)));
                initBookstore();
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
        mPresenter.getCategoryNovels();
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
        this.dataList = dataList;
        int z = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getTitle().equals("男生")) {
                z = dataList.get(i).getId();
                break;
            }
        }
        int finalZ = z;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getPid() == finalZ) {
                titles.add(dataList.get(i).getTitle()) ;
            }
        }

        verticalTabLayout.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return titles.size();
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
                        .setContent(titles.get(position))
                        .setTextColor(getContext().getResources().getColor(R.color.red_aa), Color.LTGRAY)
                        .setTextSize(16)
                        .build();
            }

            @Override
            public int getBackground(int position) {
                return R.color.white;
            }
        });

    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {

    }
}
