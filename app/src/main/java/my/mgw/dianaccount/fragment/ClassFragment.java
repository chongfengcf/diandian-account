package my.mgw.dianaccount.fragment;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.DbConn;
import my.mgw.dianaccount.greendao.CategoryDao;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.model.Category;


public class ClassFragment extends Fragment {

    private QMUITopBarLayout mTopBar;
    private QMUIFloatLayout mFloatLayout;

    private DaoSession mdaoSession;
    private List<Category> categories;
    private boolean cost;


    public ClassFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class, null);
        mTopBar = view.findViewById(R.id.topbar);
        mFloatLayout = view.findViewById(R.id.qmui_floatlayout);

//        EventBus.getDefault().post("你好，这是来自ClassFragment的消息！");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        cost = arguments.getBoolean("cost");

        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mTopBar.setTitle("选择类别");

        mTopBar.addRightImageButton(R.mipmap.icon_topbar_add, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       showAddDialog();
                    }
                });


        mFloatLayout.setMaxLines(Integer.MAX_VALUE);

        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        categories = mdaoSession.getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Pay.eq(cost))
                .list();

        for (Category c : categories) {
            addItemToFloatLayout(mFloatLayout, c);
        }

        mFloatLayout.setOnLineCountChangeListener(new QMUIFloatLayout.OnLineCountChangeListener() {
            @Override
            public void onChange(int oldLineCount, int newLineCount) {
                Log.i("Bill", "oldLineCount = " + oldLineCount + " ;newLineCount = " + newLineCount);
            }
        });

    }

    private void addItemToFloatLayout(final QMUIFloatLayout floatLayout, final Category category) {
        final int currentChildCount = floatLayout.getChildCount();


        QMUIRoundButton roundButton = new QMUIRoundButton(getActivity());
        QMUIRoundButtonDrawable drawable = (QMUIRoundButtonDrawable) roundButton.getBackground();
        drawable.setIsRadiusAdjustBounds(true);
        drawable.setStrokeData( QMUIDisplayHelper.dp2px(getContext(), 1), ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
        roundButton.setText(category.getName());
        roundButton.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
        int padding = QMUIDisplayHelper.dp2px(getContext(), 1);
        roundButton.setPadding(padding, padding, padding, padding);
        roundButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        roundButton.setTag(category.getId());
        roundButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDelDialog((Long)v.getTag(), currentChildCount);
                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category chose = mdaoSession.getCategoryDao().load((Long) v.getTag());
                EventBus.getDefault().post(chose);
                Toast.makeText(getActivity(), "选择了: " + chose.getName(), Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
        int width_dp = category.getName().getBytes().length * 10;
        width_dp = width_dp > 380 ? 380 : width_dp;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(QMUIDisplayHelper.dp2px(getContext(), width_dp), QMUIDisplayHelper.dp2px(getContext(), 30));
        floatLayout.addView(roundButton, lp);
    }

    private void showAddDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());

        builder.setTitle("新建分类")
                .setPlaceholder("在此输入分类的名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            for(Category c : categories) {
                                if(text.toString().equals(c.getName())) {
                                    Toast.makeText(getActivity(), "类别已经存在", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            long insert = mdaoSession.getCategoryDao().insert(new Category(null, text.toString(), cost));
                            addItemToFloatLayout(mFloatLayout, new Category(insert, text.toString(), cost));
                            Toast.makeText(getActivity(), "新增分类: " + text, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请填入名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(R.style.QMUI_Dialog).show();
    }

    private void showDelDialog(final Long id, final int currentChildCount) {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("删除分类")
                .setMessage("确定要删除吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        mdaoSession.getCategoryDao().deleteByKey(id);
                        mFloatLayout.removeView(mFloatLayout.getChildAt(currentChildCount));
                        dialog.dismiss();
                    }
                })
                .create(R.style.QMUI_Dialog).show();
    }

}
