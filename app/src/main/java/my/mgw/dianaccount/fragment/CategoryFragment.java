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


public class CategoryFragment extends Fragment {

    private QMUITopBarLayout mTopBar;
    private PieChart mPieChart;
    private Button button_ok;
    private Button mtopBtn;

    private DaoSession mdaoSession;

    private boolean pay = true;

    private String year;
    private String month;

    private HashMap<String, Double> record;

    private PieDataSet dataSet;

    public CategoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mTopBar = view.findViewById(R.id.topbar);
        mPieChart = view.findViewById(R.id.pic_chart);
        mtopBtn = view.findViewById(R.id.topBtn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mTopBar.setTitle("账单统计");

        button_ok = mTopBar.addRightTextButton("切换", 0);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mtopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay = pay==true ? false : true;
                getItem();
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Double> next = iterator.next();
                    entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
                }

//设置数据
                mPieChart.getData().removeDataSet(dataSet);
                setData(entries);
                mPieChart.notifyDataSetChanged();
                mPieChart.invalidate();
            }
        });
        setTime();

        getItem();

        initView();
        initData();

    }

    private void initView() {
//饼状图
        mPieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        mPieChart.getDescription().setEnabled(false);//设置描述
        mPieChart.setExtraOffsets(5, 10, 5, 5);//设置饼状图距离上下左右的偏移量
        mPieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难
//设置中间文件
        mPieChart.setCenterText(generateCenterSpannableText());
// mPieChart.setCenterText("总学生数:100人");
        mPieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        mPieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        mPieChart.setTransparentCircleColor(Color.WHITE);//设置圆环的颜色
        mPieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        mPieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小
        mPieChart.setTransparentCircleRadius(61f);//设置圆环的半径值
        mPieChart.setDrawCenterText(true);//是否绘制中间的文字
        mPieChart.setRotationAngle(0);//设置饼状图旋转的角度
// 触摸旋转
        mPieChart.setRotationEnabled(true);;//设置饼状图是否可以旋转(默认为true)
        mPieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

    }
    private void initData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Double> next = iterator.next();
            entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
        }

//设置数据
        setData(entries);
        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
//设置每个tab的显示位置
        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);
// 输入标签样式
        mPieChart.setDrawEntryLabels(true);//设置是否绘制Label
        mPieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
        mPieChart.setEntryLabelTextSize(12f);//设置绘制Label的字体大小
    }
    //设置中间文字
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("分类统计\n\n不同分类所占比重");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 6, 0);
        return s;
    }
    //设置数据
    private void setData(ArrayList<PieEntry> entries) {
        dataSet = new PieDataSet(entries, "账单统计");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
//数据和颜色
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
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
//刷新
        mPieChart.invalidate();
    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM");
        String date = dateformat.format(calendar.getTime());
        String[] split = date.split("-");
        year = split[0];
        month = split[1];
    }

    public void showDatePicker() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        startDate.set(1970, 0, 0);

        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                String dateString = sdf.format(date);
                String[] split = dateString.split("-");
                year = split[0];
                month = split[1];
                getItem();
                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                Iterator<Map.Entry<String, Double>> iterator = record.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Double> next = iterator.next();
                    entries.add(new PieEntry(next.getValue().floatValue(), next.getKey()));
                }

//设置数据
                mPieChart.getData().removeDataSet(dataSet);
                setData(entries);
                mPieChart.notifyDataSetChanged();
                mPieChart.invalidate();


            }
        }).setType(new boolean[]{true, true, false, false, false, false})
                .setDate(endDate)
                .setRangDate(startDate, endDate)
                .build();
        pvTime.show();
    }

    public void getItem() {
        List<Bill> list = mdaoSession.getBillDao().queryBuilder()
                .where(new WhereCondition.StringCondition("strftime('%Y-%m',date)='" + year + "-" + month + "'"))
                .build()
                .list();

        record = new HashMap<>();

        for (Bill bill : list) {
            if(bill.getPay()==pay) {
                String category = bill.getCategory();
                if(record.containsKey(category)) {
                    double sum = record.get(category) + bill.getMoney();
                    record.put(category, sum);
                } else {
                    record.put(category, bill.getMoney());
                }
            }
        }
    }
}
