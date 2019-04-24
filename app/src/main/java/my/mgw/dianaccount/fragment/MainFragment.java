package my.mgw.dianaccount.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.BillAdapter;
import my.mgw.dianaccount.model.Bill;


public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private QMUICollapsingTopBarLayout mCollapsingTopBarLayout;
    private QMUITopBar mTopBar;
    private List<Bill> bills;
    private BillAdapter adapter;
    private static final String TAG = "MainFragment";

    public MainFragment() {
        // 需要一个空的构造方法
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //覆盖默认创建视图方法
        //默认的方法直接返回null
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mCollapsingTopBarLayout = v.findViewById(R.id.collapsing_topbar_layout);
        mCollapsingTopBarLayout.setTitle("点点理财");
        mTopBar = v.findViewById(R.id.topbar);
        mTopBar.addRightTextButton("搜索", 0);

        Log.d(TAG, "onCreateView: 老子怎么被调用了");
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: 老子怎么被调用了");

        recyclerView = (RecyclerView)getView().findViewById(R.id.recycler_view);

        bills = new ArrayList<>();
        Bill bill;
        for(int i = 0; i < 15;i++) {
            bill = new Bill();
            bill.setCategory("第X类");
            bill.setMoney(1);
            bills.add(bill);
        }

        //创建布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //创建列表的适配器
        adapter = new BillAdapter(R.layout.recycler_layout, bills);

        //列表item的点击事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(getContext(), "你点击了第" + position + "个", Toast.LENGTH_SHORT).show();
            }
        });

        //开启切换动画
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //动画多次显示
        adapter.isFirstOnly(false);


        recyclerView.setAdapter(adapter);

        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
    }

}
