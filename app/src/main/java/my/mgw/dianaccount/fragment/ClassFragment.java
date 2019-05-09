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

//类别选择界面
public class ClassFragment extends Fragment {

    //顶部标题栏
    private QMUITopBarLayout mTopBar;
    //页面浮动布局
    private QMUIFloatLayout mFloatLayout;

    //数据库连接类
    private DaoSession mdaoSession;

    //分类信息的列表
    private List<Category> categories;

    //当前是收入还是支出里的类别
    private boolean cost;


    public ClassFragment() {
        //需要一个空的构造方法
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //引用该页面的布局
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_class, null);
        //获得标题栏
        mTopBar = view.findViewById(R.id.topbar);
        //获得浮动布局
        mFloatLayout = view.findViewById(R.id.qmui_floatlayout);

//        EventBus.getDefault().post("你好，这是来自ClassFragment的消息！");

        //返回布局给Activity
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //调用父类的构造方法
        super.onActivityCreated(savedInstanceState);

        //接受从前一个Fragment传来的参数
        Bundle arguments = getArguments();
        //获取boolean类型（true，false）的参数，名为cost。赋值给cost变量
        cost = arguments.getBoolean("cost");

        //标题栏左侧增加后退按钮
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击后退
                getActivity().onBackPressed();
            }
        });

        //设置标题栏标题
        mTopBar.setTitle("选择类别");

        //设置标题栏右侧增加按钮
        //设置单击事件监听
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_add, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //显示增加分类对话框
                       showAddDialog();
                    }
                });

        //设置浮动布局最多显示几行
        mFloatLayout.setMaxLines(Integer.MAX_VALUE);

        //获得数据库连接
        mdaoSession = ((DbConn) getActivity().getApplication()).getDaoSession();

        //构建数据库查询
        //where里是条件
        // CategoryDao.Properties.Pay.eq(cost)表示分类中的pay字段要和本类的cost变量的值要一样
        categories = mdaoSession.getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Pay.eq(cost))
                .list();

        //拿到类别数据循环
        for (Category c : categories) {
            //加入到浮动布局中
            addItemToFloatLayout(mFloatLayout, c);
        }

        //浮动布局行数变化的监听器
        mFloatLayout.setOnLineCountChangeListener(new QMUIFloatLayout.OnLineCountChangeListener() {
            @Override
            public void onChange(int oldLineCount, int newLineCount) {
                //产生日志信息，用于调试
                Log.i("Bill", "oldLineCount = " + oldLineCount + " ;newLineCount = " + newLineCount);
            }
        });

    }

    //增加项目到浮动布局
    private void addItemToFloatLayout(final QMUIFloatLayout floatLayout, final Category category) {
        //获得当前浮动布局中项目的编号
        final int currentChildCount = floatLayout.getChildCount();

        //创建一个圆角的按钮
        QMUIRoundButton roundButton = new QMUIRoundButton(getActivity());
        //用来编辑圆角按钮的样式
        QMUIRoundButtonDrawable drawable = (QMUIRoundButtonDrawable) roundButton.getBackground();
        //自动调整圆角大小
        drawable.setIsRadiusAdjustBounds(true);
        //设置圆角的长，高和颜色
        //QMUIDisplayHelper.dp2px(getContext(), 1)是将dp长度单位转换为px长度单位
        //ContextCompat.getColorStateList(getContext(), R.color.colorPrimary)是获取res中定义好的颜色
        drawable.setStrokeData( QMUIDisplayHelper.dp2px(getContext(), 1), ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
        //设置圆角按钮的文字为类别
        roundButton.setText(category.getName());
        //设置圆角按钮的文字颜色
        roundButton.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
        //按钮间间距为1dp，转换为px长度单位
        int padding = QMUIDisplayHelper.dp2px(getContext(), 1);
        //设置按钮间间距
        roundButton.setPadding(padding, padding, padding, padding);
        //设置按钮字体大小
        roundButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        //设置按钮标签来存储类别的id
        roundButton.setTag(category.getId());
        //设置按钮长事件
        //用户删除
        roundButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //显示是否删除的对话框
                showDelDialog((Long)v.getTag(), currentChildCount);
                //表示长按事件已经处理完
                return true;
            }
        });
        //设置按钮点击事件
        //用户选择
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据按钮标签里的id获取到整个分类对象
                Category chose = mdaoSession.getCategoryDao().load((Long) v.getTag());
                //将用户选择的分类发送给账单Fragment页面
                EventBus.getDefault().post(chose);
                //显示提示信息
                Toast.makeText(getActivity(), "选择了: " + chose.getName(), Toast.LENGTH_SHORT).show();
                //返回到账单Fragment页面
                getActivity().onBackPressed();
            }
        });
        //设置每一行的长度
        int width_dp = category.getName().getBytes().length * 10;
        //超过380则最长为380
        width_dp = width_dp > 380 ? 380 : width_dp;
        //设置子元素在浮动布局中的长和高
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(QMUIDisplayHelper.dp2px(getContext(), width_dp), QMUIDisplayHelper.dp2px(getContext(), 30));
        //浮动布局中增加按钮
        floatLayout.addView(roundButton, lp);
    }

    //显示新增分类对话框
    private void showAddDialog() {

        //初始化对话框构造器
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());

        builder.setTitle("新建分类") //设置标题
                .setPlaceholder("在此输入分类的名称") //设置提示信息
                .setInputType(InputType.TYPE_CLASS_TEXT) //设置可输入文字
                .addAction("取消", new QMUIDialogAction.ActionListener() { //增加取消按钮
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss(); //对话框关闭
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) { //增加确定按钮
                        CharSequence text = builder.getEditText().getText(); //获取输入的类别
                        if (text != null && text.length() > 0) { //输入内容不为空
                            for(Category c : categories) { //遍历类别
                                if(text.toString().equals(c.getName())) { //查找该类别是否已经存在
                                    //若存在则提示用户
                                    Toast.makeText(getActivity(), "类别已经存在", Toast.LENGTH_SHORT).show();
                                    //终止新增
                                    return;
                                }
                            }
                            //不存在则插入数据库，返回分类的id
                            long insert = mdaoSession.getCategoryDao().insert(new Category(null, text.toString(), cost));
                            //将新账单加入到浮动布局中
                            addItemToFloatLayout(mFloatLayout, new Category(insert, text.toString(), cost));
                            //提醒用户新增分类
                            Toast.makeText(getActivity(), "新增分类: " + text, Toast.LENGTH_SHORT).show();
                            //关闭对话框
                            dialog.dismiss();
                        } else {
                            //用户输入为空，提醒用户输入
                            Toast.makeText(getActivity(), "请填入名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(R.style.QMUI_Dialog).show(); //构造完成，显示对话框
    }

    //显示删除对话框
    private void showDelDialog(final Long id, final int currentChildCount) {
        new QMUIDialog.MessageDialogBuilder(getActivity()) //构造对话框
                .setTitle("删除分类") //设置标题
                .setMessage("确定要删除吗？") //设置提示信息
                .addAction("取消", new QMUIDialogAction.ActionListener() { //增加取消按钮
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭对话框
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) { //增加删除按钮
                        //提醒用户删除成功
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        //数据库根据id删除分类
                        mdaoSession.getCategoryDao().deleteByKey(id);
                        //从浮动布局上移除分类
                        mFloatLayout.removeView(mFloatLayout.getChildAt(currentChildCount));
                        //关闭对话框
                        dialog.dismiss();
                    }
                })
                .create(R.style.QMUI_Dialog).show(); //构造完成，显示对话框
    }

}
