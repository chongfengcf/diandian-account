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

//添加账单页面
public class BillFragment extends Fragment {

    //顶部标题栏
    private QMUITopBarLayout mTopBar;
    //选项卡控件
    private QMUITabSegment mTabSegment;
    //选项卡下面的容器PageView，可以按选项卡的选择显示不同的界面
    private ViewPager mContentViewPager;


    //支出类别文本显示
    private EditText cost_class;
    //收入类别文本显示
    private EditText income_class;

    //数据库连接类
    private DaoSession mdaoSession;

    //存储不同选项卡对应的视图
    private Map<ContentPage, View> mPageMap = new HashMap<>();
    //当前选项卡选择的页面PageView，默认为第一个页面（支出页面）
    private ContentPage mDestPage = ContentPage.Item1;
    //选项卡下面容器动态生成页面的PageView的适配器
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        //判断pager的一个view是否和instantiateItem方法返回的object有关联。
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //设置可选页面数量
        @Override
        public int getCount() {
            return ContentPage.SIZE;
        }

        //往PageView里添加自己需要的page。
        //返回的object会到isViewFromObject()方法中，判断这个view是不是这个object。
        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            //根据选项卡的选择找到要显示的页面
            ContentPage page = ContentPage.getPage(position);
            //拿到该页面的布局
            View view = getPageView(page);
            //设置PageView里的内容的长和高匹配PageView的长和高
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //把拿到的页面布局放入到PageView中
            container.addView(view, params);
            //返回view的id，官方推荐以view作为id
            return view;
        }

        //从PageView中移除页面
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //从PageView中移除页面
            container.removeView((View) object);
        }

    };


    public BillFragment() {
        // 需要一个空的构造方法
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);  //注册事件接受者
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //引入布局
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bill, null);
        //获取标题栏
        mTopBar = v.findViewById(R.id.topbar);
        //获取选项卡
        mTabSegment = v.findViewById(R.id.tabSegment);
        //获取选项卡下面的PageView
        mContentViewPager = v.findViewById(R.id.contentViewPager);
        //将布局返回给activity
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //执行父类构造方法
        super.onActivityCreated(savedInstanceState);

        //设置标题栏标题
        mTopBar.setTitle("新增账单");

        //获取数据库连接实例
        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        //设置ViewPage的适配器
        mContentViewPager.setAdapter(mPagerAdapter);
        //初始化ViewPage，显示第一个页面（添加消费）
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        //选项卡增加支出选项
        mTabSegment.addTab(new QMUITabSegment.Tab("支出"));
        //选项卡增加收入选项
        mTabSegment.addTab(new QMUITabSegment.Tab("收入"));
        //将选项卡和下面的PageView关联在一起
        //根据选项卡选择PageView展示不同的页面
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        //两个选项卡宽度各占二分之一
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);

    }

    //获取页面视图的方法
    private View getPageView(ContentPage page) {
        //从存储的字典中拿到页面对应的视图
        View view = mPageMap.get(page);
        //假如字典里没有该视图，那么加载
        if (view == null) {

            //如果选择了支出页面
            if(page == ContentPage.Item1) {
                //从xml布局文件加载视图
                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cost, null);
                //获取金额输入框
                final EditText editText_money = view.findViewById(R.id.edittext_money);
                //设置金额输入框监听器，保留两位小数
                editText_money.addTextChangedListener(new MoneyTextWatcher(editText_money));
                //获取选择类别按钮
                Button button_class = view.findViewById(R.id.button_class);
                //获取确定按钮
                Button button_ok = view.findViewById(R.id.button_del);
                //获取备注输入框
                final EditText editText_text = view.findViewById(R.id.editText_text);
                //初始化消费类别选择
                cost_class = view.findViewById(R.id.show_class);

                //选择类别按钮点击事件监听
                button_class.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //创建类别选择Fragment
                        ClassFragment fragment = new ClassFragment();
                        //用来存放参数，到新的Fragment中获取
                        Bundle bundle = new Bundle();
                        //存放boolean类别（true、false）名为cost的参数，内容为true
                        bundle.putBoolean("cost", true);
                        //设置参数
                        fragment.setArguments(bundle);


                        getFragmentManager()//获取事务管理器
                                .beginTransaction()//开启事务
                                .replace(R.id.content, fragment)//切换页面
                                .addToBackStack(null)//添加到返回栈中，待会会返回到该界面
                                .commit(); //提交
                    }
                });

                //确认按钮点击事件
                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取输入框中的金额
                        Editable moneyText = editText_money.getText();
                        //获取选择的类别
                        Editable cost_classText = cost_class.getText();
                        //获取备注
                        Editable edittext = editText_text.getText();
                        //判断金额和类别都不能为空
                        if(!(TextUtils.isEmpty(moneyText) || TextUtils.isEmpty(cost_classText))) {
                            //金额从String转为double
                            double money = Double.parseDouble(moneyText.toString());
                            //获取当前时间的字符串 例如 2019-04-28 10:08
                            String date = getNowTime();

                            //使用账单构造方法新建一个账单类
                            //将用户输入的所有信息都作为构造方法的参数，传入
                            Bill bill = new Bill(null, cost_classText.toString(), true, money, date, edittext.toString());

                            //获取到账单的数据操纵类，将新生产的对象插入数据库中
                            mdaoSession.getBillDao().insert(bill);

                            //界面显示操作成功的消息
                            showTip(true);

                            //获取BottomActivity中的goHome()方法，返回首页
                            ((BottomActivity)getActivity()).goHome();
                        } else {
                            //假如金额和类别有一个为空，那么提醒输入
                            showTip(false);
                        }
                    }
                });

                //如果选择了收入页面
            } else if(page == ContentPage.Item2) {
                //从文件加载布局
                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);
                //获取金额输入框
                final EditText editText_money = view.findViewById(R.id.edittext_money);
                //设置金额输入框的监听器，保留两位小数
                editText_money.addTextChangedListener(new MoneyTextWatcher(editText_money));
                //获取选择类别按钮
                Button button_class = view.findViewById(R.id.button_class);
                //获取确定按钮
                Button button_ok = view.findViewById(R.id.button_del);
                //获取备注输入框
                final EditText editText_text = view.findViewById(R.id.editText_text);
                //初始化收入类别文本显示
                income_class = view.findViewById(R.id.show_class);
                //类别选择按钮增加点击监听器
                button_class.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //新建类别选择Fragment
                        ClassFragment fragment = new ClassFragment();
                        //用来存放参数，在新的Fragment中获取
                        Bundle bundle = new Bundle();
                        //存放boolean类型(true,false)参数，名称为cost，内容为false
                        bundle.putBoolean("cost", false);
                        //Fragment设置参数
                        fragment.setArguments(bundle);

                        getFragmentManager() //得到Fragment管理器
                                .beginTransaction() //开启事务
                                .replace(R.id.content, fragment) //切换页面
                                .addToBackStack(null) //添加当前页面到返回栈
                                .commit(); //提交
                    }
                });

                //确定按钮增加点击监听器
                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取金额输入框内容
                        Editable moneyText = editText_money.getText();
                        //获取类别
                        Editable income_classText = income_class.getText();
                        //获取备注
                        Editable edittext = editText_text.getText();
                        //如果金额和类别都不为空
                        if(!(TextUtils.isEmpty(moneyText) || TextUtils.isEmpty(income_classText))) {
                            //金额从String转为double
                            double money = Double.parseDouble(moneyText.toString());
                            //获取当前时间的字符串 例如 2019-04-28 10:08
                            String date = getNowTime();
                            //使用账单构造方法新建一个账单类
                            //将用户输入的所有信息都作为构造方法的参数，传入
                            Bill bill = new Bill(null, income_classText.toString(), false, money, date, edittext.toString());
                            //获取到账单的数据操纵类，将新生产的对象插入数据库中
                            mdaoSession.getBillDao().insert(bill);
                            //界面显示操作成功的消息
                            showTip(true);
                            //获取BottomActivity中的goHome()方法，返回首页
                            ((BottomActivity)getActivity()).goHome();
                        } else {
                            //假如金额和类别有一个为空，那么提醒输入
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


            //生成好的视图与Page对应，存储进字典
            mPageMap.put(page, view);
        }
        //返回视图
        return view;
    }

    //定义两个页面
    public enum ContentPage {
        //消费页面
        Item1(0),
        //支出页面
        Item2(1);
        //页面大小
        public static final int SIZE = 2;
        //当前选择的页面编号
        private final int position;

        //构造方法，参数pos作为当前页面选择的编号
        ContentPage(int pos) {
            position = pos;
        }

        //根据编号获取页面
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

        //获得当前选择的页面编号
        public int getPosition() {
            return position;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //取消注册监听
    }


    //接收到事件消息后执行的方法
    //参数date是用户选择的类别
    @Subscribe
    public void onEvent(Category data) {
        //如果是支出
        if(data.getPay()) {
            //设置支出选择类别的名称为用户选择的类别
            cost_class.setText(data.getName());
        } else {
            //如果是收入
            //设置收入选择类别的名称为用户选择的类别
            income_class.setText(data.getName());
        }
    }

    //显示界面提示
    //参数success如果是true则显示成功，false则显示失败
    public void showTip(boolean success) {
        //定义一个对话框
        final QMUITipDialog tipDialog;
        if(success==true) {
            //参数success如果是true则显示成功
            tipDialog = new QMUITipDialog.Builder(getContext()) //开始构造
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS) //设置图标
                    .setTipWord("添加成功") //设置文字
                    .create(); //创建
        } else {
            //false则显示失败
            tipDialog = new QMUITipDialog.Builder(getContext()) //开始构造
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL) //设置图标
                    .setTipWord("请输入金额和类别") //设置文字
                    .create(); //创建
        }


        tipDialog.show(); //显示对话框
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 1500); //设置1500毫秒（1.5秒）自动关闭对话框
    }

    //得到当前时间的字符串形式 2019-04-28 10:08
    public String getNowTime() {
        //获取当前时间
        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH)+1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        String date = year + "-" + month + "-" + day + " " + hour + ":" + minute;

        //设置时间格式为2019-04-28 10:08
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //将当前时间按固定格式转换为字符串
        String date = dateformat.format(calendar.getTime());
        //返回字符串
        return date;
    }
}
