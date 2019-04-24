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

public class BottomActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new MainFragment());
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.content, new MainFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.content, new BillFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.content, new MyFragment());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        setDefaultFragment();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        QMUIStatusBarHelper.translucent(this);
    }

}
