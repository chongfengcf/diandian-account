package my.mgw.dianaccount.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import my.mgw.dianaccount.R;
import my.mgw.dianaccount.activity.BottomActivity;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.core.MoneyTextWatcher;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Bill;
import my.mgw.dianaccount.model.Category;


public class BillFragment extends Fragment {

    //父布局上的控件
    private QMUITopBarLayout mTopBar;
    private QMUITabSegment mTabSegment;
    private ViewPager mContentViewPager;


    private EditText cost_class;
    private EditText income_class;

    private DaoSession mdaoSession;

    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private ContentPage mDestPage = ContentPage.Item1;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return ContentPage.SIZE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    };


    public BillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);  //注册事件接受者
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill, null);
        mTopBar = v.findViewById(R.id.topbar);
        mTabSegment = v.findViewById(R.id.tabSegment);
        mContentViewPager = v.findViewById(R.id.contentViewPager);


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTopBar.setTitle("新增账单");

        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.addTab(new QMUITabSegment.Tab("支出"));
        mTabSegment.addTab(new QMUITabSegment.Tab("收入"));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);

        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
//                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {

            //支出页面
            if(page == ContentPage.Item1) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cost, null);

                final EditText editText_money = view.findViewById(R.id.edittext_money);
                editText_money.addTextChangedListener(new MoneyTextWatcher(editText_money));
                Button button_class = view.findViewById(R.id.button_class);
                Button button_ok = view.findViewById(R.id.button_del);
                final EditText editText_text = view.findViewById(R.id.editText_text);
                cost_class = view.findViewById(R.id.show_class);

                //选择类别按钮点击事件
                button_class.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClassFragment fragment = new ClassFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("cost", true);
                        fragment.setArguments(bundle);

                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                //确认按钮点击事件
                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable moneyText = editText_money.getText();
                        Editable cost_classText = cost_class.getText();
                        Editable edittext = editText_text.getText();
                        if(!(TextUtils.isEmpty(moneyText) || TextUtils.isEmpty(cost_classText))) {
                            double money = Double.parseDouble(moneyText.toString());

                           String date = getNowTime();

                            Bill bill = new Bill(null, cost_classText.toString(), true, money, date, edittext.toString());

                            mdaoSession.getBillDao().insert(bill);

                            showTip(true);

                            ((BottomActivity)getActivity()).goHome();
                        } else {
                            showTip(false);
                        }
                    }
                });

                //收入页面
            } else if(page == ContentPage.Item2) {

                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);

                final EditText editText_money = view.findViewById(R.id.edittext_money);
                editText_money.addTextChangedListener(new MoneyTextWatcher(editText_money));
                Button button_class = view.findViewById(R.id.button_class);
                Button button_ok = view.findViewById(R.id.button_del);
                final EditText editText_text = view.findViewById(R.id.editText_text);

                income_class = view.findViewById(R.id.show_class);
                button_class.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClassFragment fragment = new ClassFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("cost", false);
                        fragment.setArguments(bundle);

                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable moneyText = editText_money.getText();
                        Editable income_classText = income_class.getText();
                        Editable edittext = editText_text.getText();
                        if(!(TextUtils.isEmpty(moneyText) || TextUtils.isEmpty(income_classText))) {
                            double money = Double.parseDouble(moneyText.toString());

                            String date = getNowTime();

                            Bill bill = new Bill(null, income_classText.toString(), false, money, date, edittext.toString());

                            mdaoSession.getBillDao().insert(bill);

                            showTip(true);

                            ((BottomActivity)getActivity()).goHome();
                        } else {
                            showTip(false);
                        }
                    }
                });

            }



//            TextView textView = new TextView(getContext());
//            textView.setGravity(Gravity.CENTER);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_description));
//
//            if (page == ContentPage.Item1) {
//                textView.setText("第一页");
//            } else if (page == ContentPage.Item2) {
//                textView.setText("第二页");
//            }
//
//            view = textView;
            mPageMap.put(page, view);
        }
        return view;
    }

    public enum ContentPage {
        Item1(0),
        Item2(1);
        public static final int SIZE = 2;
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                default:
                    return Item1;
            }
        }

        public int getPosition() {
            return position;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEvent(Category data) {
        if(data.getPay()) {
            cost_class.setText(data.getName());
        } else {
            income_class.setText(data.getName());
        }
    }

    public void showTip(boolean success) {
        final QMUITipDialog tipDialog;
        if(success==true) {
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                    .setTipWord("添加成功")
                    .create();
        } else {
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                    .setTipWord("请输入金额和类别")
                    .create();
        }


        tipDialog.show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 1500);
    }

    public String getNowTime() {
        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH)+1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        String date = year + "-" + month + "-" + day + " " + hour + ":" + minute;
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = dateformat.format(calendar.getTime());
        return date;
    }
}
