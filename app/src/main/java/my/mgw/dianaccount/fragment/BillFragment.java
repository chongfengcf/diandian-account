package my.mgw.dianaccount.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import java.util.HashMap;
import java.util.Map;


import my.mgw.dianaccount.R;


public class BillFragment extends Fragment {


    private QMUITopBarLayout mTopBar;
    private QMUITabSegment mTabSegment;
    private ViewPager mContentViewPager;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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



        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.addTab(new QMUITabSegment.Tab("支出"));
        mTabSegment.addTab(new QMUITabSegment.Tab("收入"));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
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
            if(page == ContentPage.Item1) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cost, null);
                EditText mett = view.findViewById(R.id.edittext_name);
                mett.setText("chongfengcf");
                Button button = view.findViewById(R.id.button_register);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(page == ContentPage.Item2) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_income, null);
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
}
