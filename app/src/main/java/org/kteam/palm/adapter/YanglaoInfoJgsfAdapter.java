package org.kteam.palm.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.network.response.YanglaoInfoJgsf;
import org.kteam.palm.network.response.YiliaoPayHistoryContent;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 *
 * @Description 历年缴费记录...
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-19 22:15
 */
public class YanglaoInfoJgsfAdapter extends BaseListAdapter<YanglaoInfoJgsf> {

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;

    private OnRVItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setItemWidth(int item1Width, int item2Width, int item3Width) {
        mItem1Width = item1Width;
        mItem2Width = item2Width;
        mItem3Width = item3Width;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        YanglaoInfoJgsf content = getItem(position);

        if (!TextUtils.isEmpty(content.aae003) && content.aae003.length() >= 6) {
            String yearMonth = itemViewHolder.itemView.getContext().getString(R.string.year_month, content.aae003.substring(0, 4), content.aae003.substring(4, 6));
            itemViewHolder.tvPayYear.setText(yearMonth);
        } else {
            itemViewHolder.tvPayYear.setText(content.aae003);
        }
        itemViewHolder.tvPayMonth.setText(content.aic910);
        if (!TextUtils.isEmpty(content.aae010) && content.aae010.length() >= 8) {
            String bankNo = content.aae010.substring(0, 4) + "****" + content.aae010.substring(content.aae010.length() - 4, content.aae010.length());
            itemViewHolder.tvPaySalary.setText(bankNo);
        } else {
            itemViewHolder.tvPaySalary.setText(content.aae010);
        }

        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvPayYear;
        TextView tvPayMonth;
        TextView tvPaySalary;

        LinearLayout layout1;
        LinearLayout layout2;
        LinearLayout layout3;
        LinearLayout layout4;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.layout1 = (LinearLayout) itemView.findViewById(R.id.layout1);
            this.layout2 = (LinearLayout) itemView.findViewById(R.id.layout2);
            this.layout3 = (LinearLayout) itemView.findViewById(R.id.layout3);
            this.layout4 = (LinearLayout) itemView.findViewById(R.id.layout4);

            this.layout1.getLayoutParams().width = mItem1Width;
            this.layout2.getLayoutParams().width = mItem2Width;
            this.layout3.getLayoutParams().width = mItem3Width;

            this.tvPayYear = (TextView) itemView.findViewById(R.id.tv_pay_year);
            this.tvPayMonth = (TextView) itemView.findViewById(R.id.tv_pay_months);
            this.tvPaySalary = (TextView) itemView.findViewById(R.id.tv_pay_salary);
        }

    }
}