package my.mgw.dianaccount.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.core.MoneyTextWatcher;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Bill;
import my.mgw.dianaccount.model.Category;


public class EditAndDelFragment extends Fragment {


    private QMUITopBarLayout mTopBar;
    private EditText edittext_money;
    private EditText edittext_text;
    private TextView edittext_date;
    private Button button_class;
    private TextView choice_class;
    private Button button_del;
    private Button button_ok;

    private Bill bill;
    private String cate;

    private DaoSession mdaoSession;

    public EditAndDelFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);  //注册事件接受者
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_and_del, container, false);
        mTopBar = view.findViewById(R.id.topbar);
        edittext_money = view.findViewById(R.id.edittext_money);
        choice_class = view.findViewById(R.id.choice_class);
        edittext_text = view.findViewById(R.id.editText_text);
        edittext_date = view.findViewById(R.id.editText_date);
        button_class = view.findViewById(R.id.button_class);
        button_del = view.findViewById(R.id.button_del);
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

        mTopBar.setTitle("修改账单");

        button_ok = mTopBar.addRightTextButton("保存", 0);

        initItem();
        initData();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initItem() {
        bill = new Bill();
        Bundle arguments = getArguments();
        bill.setId(arguments.getLong("id"));
        bill.setCategory(arguments.getString("category"));
        bill.setPay(arguments.getBoolean("pay"));
        bill.setMoney(arguments.getDouble("money"));
        bill.setDate(arguments.getString("date"));
        bill.setText(arguments.getString("text"));
    }


    private void initData() {
        edittext_money.addTextChangedListener(new MoneyTextWatcher(edittext_money));
        edittext_money.setText(String.valueOf(bill.getMoney()));
        if(cate==null) {
            choice_class.setText(bill.getCategory());
        } else {
            choice_class.setText(cate);
        }
        edittext_text.setText(bill.getText());
        edittext_date.setText(bill.getDate());


        button_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassFragment fragment = new ClassFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("cost", bill.getPay());
                fragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        button_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdaoSession.getBillDao().deleteByKey(bill.getId());
                successTip("删除成功");
                getActivity().onBackPressed();
            }
        });

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable moneyText = edittext_money.getText();
                CharSequence choiceText = choice_class.getText();
                CharSequence dateText = edittext_date.getText();
                Editable textText = edittext_text.getText();

                if(TextUtils.isEmpty(moneyText) || TextUtils.isEmpty(choiceText) ||TextUtils.isEmpty(dateText)) {
                    nullTip();
                } else {
                    bill.setMoney(Double.valueOf(moneyText.toString()));
                    bill.setCategory(choiceText.toString());
                    bill.setDate(dateText.toString());
                    bill.setText(textText.toString());
                    mdaoSession.getBillDao().update(bill);
                    successTip("保存成功");
                    getActivity().onBackPressed();
                }

            }
        });

        edittext_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void showDatePicker() {
        Calendar selectedDate = Calendar.getInstance();
        String dateStr = bill.getDate();
        String[] yearDayTime = dateStr.split(" ");
        String[] yearDay = yearDayTime[0].split("-");
        String[] time = yearDayTime[1].split(":");
        selectedDate.set(Integer.parseInt(yearDay[0]), Integer.parseInt(yearDay[1])-1, Integer.parseInt(yearDay[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]));

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        startDate.set(1970, 0, 0);

        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                edittext_date.setText(sdf.format(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .build();
        pvTime.show();
    }

    public void successTip(String opera) {
        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(opera)
                .create();

        tipDialog.show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 1500);
    }


    public void nullTip() {
        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("请输入完整的信息")
                .create();
        tipDialog.show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 1500);
    }

    @Subscribe
    public void onEvent(Category data) {
        cate = data.getName();
    }
}
