package my.mgw.dianaccount.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.fragment.BillFragment;
import my.mgw.dianaccount.fragment.MainFragment;
import my.mgw.dianaccount.fragment.MyFragment;


//底部导航栏和界面框架
public class BottomActivity extends AppCompatActivity {


    //fragment操作的事物，来保证fragment切换的原子性
    private FragmentTransaction transaction;
    //fragment管理器，用来管理多个fragment
    private FragmentManager fragmentManager;
    //底部导航栏对象
    private BottomNavigationView navigation;

    //新建一个底部导航栏选择事件的监听器
    //当一个菜单被选择，就会产生一个事件
    //类比成报警
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //重写(覆盖)默认的事件处理逻辑
        //传进来一个MenuItem类型的参数，通过参数可以获取到选中的菜单
        //@NotNull表示参数不能为空
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //获取到fragment管理器
            fragmentManager = getSupportFragmentManager();
            //开启事物，fragment开始切换
            transaction = fragmentManager.beginTransaction();

            //不同情况的区别对待
            //三岔路口
            switch (item.getItemId()) {
                //如果item的id等于首页
                case R.id.navigation_home:
                    //fragment替换为MainFragment
                    transaction.replace(R.id.content, new MainFragment());
                    //提交事物
                    transaction.commit();
                    //方法完成，返回true
                    return true;
                //如果item的id等于添加
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.content, new BillFragment());
                    transaction.commit();
                    return true;
                //如果item的id等于我的
                case R.id.navigation_notifications:
                    transaction.replace(R.id.content, new MyFragment());
                    transaction.commit();
                    return true;
            }

            //假如三个都没选择到，那么直接返回false
            return false;
        }
    };

    //活动创建执行的第一个方法
    //savedInstanceState是可以保存活动的实例，便于恢复，一般用不到
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //调用父类的onCreate方法
        //super是父类，this是自身
        super.onCreate(savedInstanceState);

        //引入界面
        setContentView(R.layout.activity_bottom);

        //调用显示首页的方法
        setDefaultFragment();

        //通过id找到导航栏部件
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //设置导航栏选择事件的监听器
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //QMUI框架实现沉浸式标题栏
        QMUIStatusBarHelper.translucent(this);
    }

    //模拟点击首页
    public void goHome() {
        //导航栏选中首页
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    // 设置默认进来是显示首页的方法
    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new MainFragment());
        transaction.commit();
    }
}
