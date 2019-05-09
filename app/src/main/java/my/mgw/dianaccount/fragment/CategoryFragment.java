package my.mgw.dianaccount.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.greenrobot.greendao.query.WhereCondition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Bill;

//账单统计Fragment
public class CategoryFragment extends Fragment {

    //顶部标题栏
    private QMUITopBarLayout mTopBar;
    //饼图图表
    private PieChart mPieChart;
    //顶部切换按钮
    private Button button_ok;
    //收入/支出切换按钮
    private Button mtopBtn;

    //数据库连接初始化
    private DaoSession mdaoSession;

    //显示支出还是收入，默认为支出
    private boolean pay = true;

    //选择的年
    private String year;
    //选择的月
    private String month;

    //按类别分类的金额数据
    private HashMap<String, Double> record;

    //图表对应的数据集
    private PieDataSet dataSet;

    public CategoryFragment() {
        //需要一个空的构造方法
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //引入一个界面
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        //获得顶部标题栏
        mTopBar = view.findViewById(R.id.topbar);
        //获得饼图图表
        mPieChart = view.findViewById(R.id.pic_chart);
        //获得收入/支出切换按钮
        mtopBtn = view.findViewById(R.id.topBtn);
        //返回界面给activity
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //调用父类构造方法
        super.onActivityCreated(savedInstanceState);

        //获取一个数据库连接
        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        //顶部标题栏左侧新增后退按钮
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回到上一个Fragment
                getActivity().onBackPressed();
            }
        });

        //设置顶部标题栏标题
        mTopBar.setTitle("账单统计");

        //新增顶部标题栏右侧切换按钮
        button_ok = mTopBar.addRightTextButton("切换", 0);

        //切换按钮增加点击监听器
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示日期选择器
                showDatePicker();
            }
        });

        //收入/支出切换按钮增加点击监听事件
        mtopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //三目运算符，将收入变成支出，支出变成收入
                //先判断pay是否等于true
                //相等pay=false
                //不想等pay=true
                pay = pay==true ? false : true;
                //从数据库中获取数据
                getItem();
                //新建一个数组存放饼图的数据
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                //获得分类统计后的金额数据的迭代器
                Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
                //迭代器有下一个元素才循环
                while (iterator.hasNext()) {
                    //获得下一个元素
                    Map.Entry<String, Double> next = iterator.next();
                    //把该类别和金额都加入到饼图的数据中去
                    entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
                }

                //初始化饼图，先情况数据
                mPieChart.getData().removeDataSet(dataSet);
                //给饼图设置数据
                setData(entries);
                //通知饼图数据改变了
                mPieChart.notifyDataSetChanged();
                //更新饼图
                mPieChart.invalidate();
            }
        });

        //获取当前的年月
        setTime();

        //从数据库中获取数据
        getItem();

        //初始化饼图
        initView();

        //为饼图设置数据
        initData();

    }

    //初始饼图
    private void initView() {
        mPieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        mPieChart.getDescription().setEnabled(false);//设置描述
        mPieChart.setExtraOffsets(5, 10, 5, 5);//设置饼状图距离上下左右的偏移量
        mPieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难
        //设置中间的文字
        // mPieChart.setCenterText("总学生数:100人");
        mPieChart.setCenterText(generateCenterSpannableText());
        mPieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        mPieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        mPieChart.setTransparentCircleColor(Color.WHITE);//设置圆环的颜色
        mPieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        mPieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小
        mPieChart.setTransparentCircleRadius(61f);//设置圆环的半径值
        mPieChart.setDrawCenterText(true);//是否绘制中间的文字
        mPieChart.setRotationAngle(0);//设置饼状图旋转的角度
        mPieChart.setRotationEnabled(true);;//设置饼状图是否可以旋转(默认为true)
        mPieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

    }

    //初始化数据
    private void initData() {
        //新建一个空的饼图数据列表
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        //获得分类统计后的金额数据的迭代器
        Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
        //如果迭代器还有下一个元素
        while (iterator.hasNext()) {
            //获得下一个元素
            Map.Entry<String, Double> next = iterator.next();
            //把该类别和金额都加入到饼图的数据中去
            entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
        }

        //设置饼图数据
        setData(entries);
        //饼图的动画
        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mPieChart.getLegend();//设置每个图例的显示位置
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//显示在饼图下面
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);//显示在饼图左边
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);//图例水平排列
        l.setDrawInside(false);//图例在饼图外面
        l.setWordWrapEnabled(true);//图例文字过长自动折叠
        l.setXEntrySpace(0f);//设置tab之间x轴方向上的空白间距值
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);//设置与图表在y轴方向上的间距

        mPieChart.setDrawEntryLabels(true);//设置是否绘制Label
        mPieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
        mPieChart.setEntryLabelTextSize(12f);//设置绘制Label的字体大小
    }
    //设置中间文字
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("分类统计\n\n不同分类所占比重");//设置中间文字内容
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 6, 0);//中间文字四周间距
        return s; //返回中间文字给控件
    }

    //设置数据
    private void setData(ArrayList<PieEntry> entries) {
        dataSet = new PieDataSet(entries, "账单统计"); //给饼图设置数据集
        dataSet.setSliceSpace(3f);//设置每个饼状图之间的距离
        dataSet.setSelectionShift(5f);//设置选中态多出来的长度

        //给饼状图添加色彩
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //设置饼状图数据显示格式
        PieData data = new PieData(dataSet);
        //显示百分比
        data.setValueFormatter(new PercentFormatter());
        //数据显示字体大小
        data.setValueTextSize(11f);
        //数据显示字体颜色
        data.setValueTextColor(Color.BLACK);
        //设置数据样式
        mPieChart.setData(data);
        //设置高亮内容
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }

    //获取当前年月
    public void setTime() {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        //设置为固定格式 2019-04
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM");
        //将当前日期转换为固定格式
        String date = dateformat.format(calendar.getTime());
        //把-号前后的内容分割出来成为数组
        String[] split = date.split("-");
        year = split[0];//数组第一个就是年
        month = split[1];//数组第二个就是月
    }

    //显示时间选择控件
    public void showDatePicker() {
        //初始化可选择的开始时间
        Calendar startDate = Calendar.getInstance();
        //初始化可选择的最后时间为今天
        Calendar endDate = Calendar.getInstance();

        //可选择的开始时间为1970年1月1日
        startDate.set(1970, 0, 0);

        //开始构建一个日期选择器
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            //用户选择了一个日期后
            @Override
            public void onTimeSelect(Date date, View v) {
                //制定显示的日期格式为 2019-04
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                //将所有的日期都转换为该格式
                String dateString = sdf.format(date);
                //将字符串以-符号分割成数组
                String[] split = dateString.split("-");
                year = split[0];//数组第一个是年
                month = split[1];//数组第二个是月
                //从数据库中获取数据
                getItem();
                //创建新的饼图数据
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                //获得分类统计后的金额数据的迭代器
                Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
                //如果迭代器还有下一个元素
                while (iterator.hasNext()) {
                    //获取下一个元素
                    Map.Entry<String, Double> next = iterator.next();
                    //将分类统计的金额数据放入到饼图数据中
                    entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
                }

                //清空原数据
                mPieChart.getData().removeDataSet(dataSet);
                //设置新的数据
                setData(entries);
                //通知饼图数据已经改变
                mPieChart.notifyDataSetChanged();
                //刷新饼图
                mPieChart.invalidate();


            }
        }).setType(new boolean[]{true, true, false, false, false, false}) //时间选择器只选择年月
                .setDate(endDate) //设置当前选中的是本月
                .setRangDate(startDate, endDate) //设置可选的年月范围
                .build(); //构造时间选择器
        pvTime.show(); //显示时间选择器
    }

    //从数据库中获取分类统计金额数据
    public void getItem() {
        //构造一个条件查询
        //where中表示具体的条件
        //strftime('%Y-%m',date)是将date变成2019-04这种格式
        //然后再和我们所要求的年月去比较
        //如果该账单符合我们所查询的年月那么就返回
        //最终获取所有选中年月的账单
        List<Bill> list = mdaoSession.getBillDao().queryBuilder()
                .where(new WhereCondition.StringCondition("strftime('%Y-%m',date)='" + year + "-" + month + "'"))
                .build()
                .list();

        //以一个空的字典初始化
        record = new HashMap<>();

        //在数据库拿到的字典列表中循环
        for (Bill bill : list) {
            //看账单类型是否符合当前用户要求的支出或者收入
            //若用户选择了查看收入，则支出账单被忽略
            //若用户选择了查看支出，则收入账单被忽略
            if(bill.getPay()==pay) {
                //获取该账单的分类
                String category = bill.getCategory();
                //查看字典中有没有这个分类
                if(record.containsKey(category)) { //如果已经有这个分类
                    //把金额累加
                    double sum = record.get(category) + bill.getMoney();
                    //把累加后的金额存入该分类
                    record.put(category, sum);
                } else { //如果没有这个分类
                    //直接把分类和金额存入字典中
                    record.put(category, bill.getMoney());
                }
            }
        }
    }
}
