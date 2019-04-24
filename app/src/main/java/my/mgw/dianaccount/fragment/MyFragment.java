package my.mgw.dianaccount.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import my.mgw.dianaccount.R;


public class MyFragment extends Fragment {


    private QMUITopBarLayout mTopBar;
    private TextView mVersionTextView;
    private QMUIGroupListView mAboutGroupListView;
    private TextView mCopyrightTextView;

    public MyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
        mTopBar = view.findViewById(R.id.topbar);
        mVersionTextView = view.findViewById(R.id.version);
        mAboutGroupListView = view.findViewById(R.id.about_list);
        mCopyrightTextView = view.findViewById(R.id.copyright);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTopBar.setTitle("我的");

        // 切换其他情况的按钮
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, R.id.topbar_right_change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetList();
            }
        });

        mVersionTextView.setText("毛国望小姐姐");

        QMUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView("账单统计"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .addItemView(mAboutGroupListView.createItemView("个人资料"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .addTo(mAboutGroupListView);

        mCopyrightTextView.setText("点点理财  1.0.0");
    }

    private void showBottomSheetList() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("浙江理工大学科学与艺术学院")
                .addItem("机电系")
                .addItem("通讯工程")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }
}
