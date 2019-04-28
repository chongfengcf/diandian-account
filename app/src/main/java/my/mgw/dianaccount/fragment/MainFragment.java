package my.mgw.dianaccount.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.BillAdapter;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.greendao.BillDao;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Bill;


public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private QMUICollapsingTopBarLayout mCollapsingTopBarLayout;
    private QMUITopBar mTopBar;
    private List<Bill> bills;
    private BillAdapter adapter;

    private DaoSession mdaoSession;

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
        Button button_search = mTopBar.addRightTextButton("搜索", 0);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog();
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView)getView().findViewById(R.id.recycler_view);

        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

//        bills = mdaoSession.getBillDao().loadAll();

        bills = mdaoSession.getBillDao().queryBuilder()
                .orderDesc(BillDao.Properties.Date, BillDao.Properties.Id)
                .list();



        //创建布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //创建列表的适配器
        adapter = new BillAdapter(R.layout.bill_list, bills);

        //列表item的点击事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bill item = (Bill) adapter.getItem(position);

                EditAndDelFragment fragment = new EditAndDelFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("id", item.getId());
                bundle.putString("category", item.getCategory());
                bundle.putBoolean("pay", item.getPay());
                bundle.putDouble("money", item.getMoney());
                bundle.putString("date", item.getDate());
                bundle.putString("text", item.getText());
                fragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //开启切换动画
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //动画多次显示
        adapter.isFirstOnly(false);


        recyclerView.setAdapter(adapter);

        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
    }

    private void searchDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());

        builder.setTitle("搜索账单")
                .setPlaceholder("在此输入要搜索的内容")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("搜索", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if(TextUtils.isEmpty(text)) {
                            dialog.dismiss();
                        } else {
                            String keyWord = "%" + text.toString() + "%";
                            List<Bill> list = mdaoSession.getBillDao().queryBuilder()
                                    .whereOr(BillDao.Properties.Money.like(keyWord),
                                            BillDao.Properties.Category.like(keyWord),
                                            BillDao.Properties.Text.like(keyWord),
                                            BillDao.Properties.Date.like(keyWord))
                                    .list();
                            bills.clear();
                            bills.addAll(list);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        }
                    }
                })
                .create(R.style.QMUI_Dialog).show();
    }

}
