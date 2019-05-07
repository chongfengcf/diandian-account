package my.mgw.dianaccount.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.greendao.query.WhereCondition;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Bill;


public class PropertyFragment extends Fragment {

    private QMUITopBarLayout mTopBar;
    private QMUIProgressBar mCircleProgressBar;
    private Button mtopBtn;
    private TextView textview_info;


    private DaoSession mdaoSession;

    private String year;
    private String month;

    private double cost;
    private double income;

    public PropertyFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_property, null);
        mTopBar = view.findViewById(R.id.topbar);
        mCircleProgressBar = view.findViewById(R.id.circleProgressBar);
        mtopBtn = view.findViewById(R.id.topBtn);
        textview_info = view.findViewById(R.id.textview_info);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        setTime();

        computing();

        int intcost = (int) (cost * 100);
        int intincome = (int) (income * 100);


        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();;
            }
        });

        mTopBar.setTitle("收支情况");

        mCircleProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
            @Override
            public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                return "本期盈余: " + (-(2 * value - maxValue)/100d);
            }
        });

        mCircleProgressBar.setMaxValue(intcost + intincome);
        try {
            mCircleProgressBar.setProgress(intcost);
        } catch (ArithmeticException e) {
            mCircleProgressBar.setMaxValue(2);
            mCircleProgressBar.setProgress(1);
        }

        mtopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM");
        String date = dateformat.format(calendar.getTime());
        String[] split = date.split("-");
        year = split[0];
        month = split[1];
    }

    public void computing() {
        List<Bill> list = mdaoSession.getBillDao().queryBuilder()
                .where(new WhereCondition.StringCondition("strftime('%Y-%m',date)='" + year + "-" + month + "'"))
                .build()
                .list();



        cost = 0;
        income = 0;

        for(Bill bill : list) {
            if(bill.getPay()) {
                cost = cost + bill.getMoney();
            } else {
                income = income + bill.getMoney();
            }
        }

        textview_info.setText("本月支出: " + cost + " 元\n本月收入: " + income + " 元");
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
                computing();
                int intcost = (int) (cost * 100);
                int intincome = (int) (income * 100);
                int total =intcost + intincome;
                if(total == 0) {
                    Toast.makeText(getContext(), "当月暂无数据", Toast.LENGTH_SHORT).show();

                    getActivity().onBackPressed();
                } else {
                    mCircleProgressBar.setMaxValue(intcost + intincome);
                    mCircleProgressBar.setProgress(intcost);
                }
            }
        }).setType(new boolean[]{true, true, false, false, false, false})
                .setDate(endDate)
                .setRangDate(startDate, endDate)
                .build();
        pvTime.show();
    }

}
