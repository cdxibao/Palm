package org.kteam.palm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.AccountInfo;
import org.kteam.palm.network.response.AccountYearInfo;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-19 22:15
 */
public class AccountYearInfoAdapter extends BaseListAdapter<AccountYearInfo> {

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;


    public void setItemWidth(int item1Width, int item2Width, int item3Width, int item4Width) {
        mItem1Width = item1Width;
        mItem2Width = item2Width;
        mItem3Width = item3Width;
        mItem4Width = item4Width;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_year_info, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        AccountYearInfo accountYearInfo = getItem(position);

        Context context = itemViewHolder.itemView.getContext();
        if (!TextUtils.isEmpty(accountYearInfo.aae003) && accountYearInfo.aae003.length() >= 6) {
            itemViewHolder.tvPayYearMonth.setText(context.getString(R.string.month, accountYearInfo.aae003.substring(4, 6)));
        } else {
            itemViewHolder.tvPayYearMonth.setText("");
        }
        itemViewHolder.tvPayMonthBase.setText(context.getString(R.string.yuan, accountYearInfo.aic020));
        itemViewHolder.tvPayRate.setText(accountYearInfo.aaa041);
        itemViewHolder.tvPaySum.setText(context.getString(R.string.yuan, accountYearInfo.aic021));

        Resources resources = context.getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvPayYearMonth;
        TextView tvPayMonthBase;
        TextView tvPayRate;
        TextView tvPaySum;

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
            this.layout4.getLayoutParams().width = mItem4Width;

            this.tvPayYearMonth = (TextView) itemView.findViewById(R.id.tv_pay_year_month);
            this.tvPayMonthBase = (TextView) itemView.findViewById(R.id.tv_pay_month_base);
            this.tvPayRate = (TextView) itemView.findViewById(R.id.tv_pay_rate);
            this.tvPaySum = (TextView) itemView.findViewById(R.id.tv_pay_sum);
        }
    }
}