package my.mgw.dianaccount.core;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.model.Bill;

public class BillAdapter extends BaseQuickAdapter<Bill, BaseViewHolder> {

    public BillAdapter(int layoutResId, @Nullable List<Bill> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Bill item) {
        helper.setText(R.id.tv_title, item.getCategory())
                .setText(R.id.tv_content, String.valueOf(item.getMoney()))
                .setImageResource(R.id.iv_img, R.drawable.add);
    }
}
