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

//首页fragment
public class MainFragment extends Fragment {

    //列表
    private RecyclerView recyclerView;
    //可伸缩的标题栏的布局
    private QMUICollapsingTopBarLayout mCollapsingTopBarLayout;
    //标题栏
    private QMUITopBar mTopBar;
    //存放账单数据的数组,是列表显示的数据
    private List<Bill> bills;
    //列表的适配器,列表怎么生成
    private BillAdapter adapter;
    //数据库连接
    private DaoSession mdaoSession;

    public MainFragment() {
        // 需要一个空的构造方法
    }

    //覆盖默认创建视图方法
    //默认的方法直接返回null
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //引入布局,拿到布局
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        //获取伸缩头部布局控件
        mCollapsingTopBarLayout = v.findViewById(R.id.collapsing_topbar_layout);
        //设置标题
        mCollapsingTopBarLayout.setTitle("点点理财");
        //获取标题栏控件
        mTopBar = v.findViewById(R.id.topbar);
        //动态生成一个标题栏右侧按钮,搜索按钮
        Button button_search = mTopBar.addRightTextButton("搜索", 0);
        //设置点击监听器
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行搜索的方法
                searchDialog();
            }
        });

        //将布局返回给activity
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //执行父类的构造方法
        super.onActivityCreated(savedInstanceState);

        //通过id找到列表
        recyclerView = (RecyclerView)getView().findViewById(R.id.recycler_view);

        //获取公用的，只有一个的数据库连接
        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

//        bills = mdaoSession.getBillDao().loadAll();

        //获取数据
        //创建一个查询构建器
        //先按照时间降序排序，时间相同则按照id降序排序
        //list方法执行查询
        bills = mdaoSession.getBillDao().queryBuilder()
                .orderDesc(BillDao.Properties.Date, BillDao.Properties.Id)
                .list();



        //创建列表的布局管理器
        //线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //设置方向，垂直排列
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //列表设置上面的布局管理器
        recyclerView.setLayoutManager(linearLayoutManager);

        //创建列表的适配器
        //第一个参数是列表的每一行怎么布局
        //第二个参数是全部的数据
        adapter = new BillAdapter(R.layout.bill_list, bills);

        //列表item的点击事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            //点击事件发生有三个参数，第一个是列表适配器，第二个是视图，第三个是点击的位置
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //根据点击的位置获取这一行的账单对象
                Bill item = (Bill) adapter.getItem(position);
                //新建一个修改界面的fragment
                EditAndDelFragment fragment = new EditAndDelFragment();
                //用来存放参数，到修改页面再获取
                Bundle bundle = new Bundle();
                //存入一个长整数叫id,内容是账单的id
                bundle.putLong("id", item.getId());
                //存入一个字符串叫category，内容是类别
                bundle.putString("category", item.getCategory());
                //存入一个布尔型（true或者false)，叫pay，内容是是否是支出
                bundle.putBoolean("pay", item.getPay());
                //存入一个双精度小数，叫money，内容是金额
                bundle.putDouble("money", item.getMoney());
                bundle.putString("date", item.getDate());
                bundle.putString("text", item.getText());

                //把存好的信息放入到修改界面的fragment
                fragment.setArguments(bundle);

                //获取fragment管理器，开始事物，替换界面，加入到返回栈中，提交
                //加入返回栈，用户点击返回时，会回到当前界面
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

        //列表设置适配器
        recyclerView.setAdapter(adapter);

        //QMUI框架设置标题栏为明亮模式，也就是标题栏图标为黑色
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
    }

    //点击搜索执行的方法
    private void searchDialog() {

        //构建一个提示框生成器
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());


        builder.setTitle("搜索账单")
                .setPlaceholder("在此输入要搜索的内容")
                .setInputType(InputType.TYPE_CLASS_TEXT)    //设置输入类型
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }) //取消事件
                .addAction("搜索", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) { //搜索事件
                        //获取搜索文字
                        CharSequence text = builder.getEditText().getText();
                        //判断是否输入为空
                        if(TextUtils.isEmpty(text)) {
                            //关闭对话框
                            dialog.dismiss();
                        } else {
                            //构建模糊查询字符串
                            //在关键字前后加入百分号即可模糊查询
                            String keyWord = "%" + text.toString() + "%";
                            //构造一个数据库查询
                            //whereor是或者的关系，并集
                            //使用like来模糊查询
                            //money中匹配到的+category中匹配到的+text中匹配到的+Date中匹配到的 = 查找到的
                            List<Bill> list = mdaoSession.getBillDao().queryBuilder()
                                    .whereOr(BillDao.Properties.Money.like(keyWord),
                                            BillDao.Properties.Category.like(keyWord),
                                            BillDao.Properties.Text.like(keyWord),
                                            BillDao.Properties.Date.like(keyWord))
                                    .list();

                            //列表先清空
                            bills.clear();
                            //把找到的所有内容加入到列表中
                            bills.addAll(list);
                            //通知适配器数据变了
                            //适配器是数据和显示列表的一个桥梁
                            adapter.notifyDataSetChanged();
                            //关闭对话框
                            dialog.dismiss();

                        }
                    }
                })
                .create(R.style.QMUI_Dialog).show(); //创建对话框并显示出来
    }

}
