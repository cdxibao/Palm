package org.kteam.palm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.AccountInfo;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-19 22:15
 */
public class AccountAdapter extends BaseListAdapter<AccountInfo> {

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    private OnRVItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setItemWidth(int item1Width, int item2Width, int item3Width, int item4Width) {
        mItem1Width = item1Width;
        mItem2Width = item2Width;
        mItem3Width = item3Width;
        mItem4Width = item4Width;
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
        AccountInfo accountInfo = getItem(position);

        try {
            itemViewHolder.tvPayYear.setText(Html.fromHtml("<u> <font color=\"#004198\">" + accountInfo.aae001 + "</font></u>"));
            Drawable drawable = itemViewHolder.itemView.getContext().getResources().getDrawable(R.mipmap.add);
            itemViewHolder.tvPayYear.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            itemViewHolder.tvPayYear.setTextColor(Color.BLUE);
            if (Integer.parseInt(accountInfo.aae001) <= 1996) {
                Drawable drawablePlaceHolder = itemViewHolder.itemView.getContext().getResources().getDrawable(R.mipmap.add_place_holder);
                itemViewHolder.tvPayYear.setCompoundDrawablesWithIntrinsicBounds(drawablePlaceHolder, null, null, null);
                itemViewHolder.tvPayYear.setTextColor(itemViewHolder.itemView.getContext().getResources().getColor(R.color.article_title));
                itemViewHolder.tvPayYear.setText(accountInfo.aae001);
            }
        } catch (Exception e) {

        }

        itemViewHolder.tvPayMonth.setText(itemViewHolder.itemView.getContext().getString(R.string.month_num, accountInfo.aic079));
        itemViewHolder.tvPaySalary.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, accountInfo.aic080));
        itemViewHolder.tvPayBalance.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, accountInfo.aic094));

        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvPayYear;
        TextView tvPayMonth;
        TextView tvPaySalary;
        TextView tvPayBalance;

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

            this.tvPayYear = (TextView) itemView.findViewById(R.id.tv_pay_year);
            this.tvPayMonth = (TextView) itemView.findViewById(R.id.tv_pay_months);
            this.tvPaySalary = (TextView) itemView.findViewById(R.id.tv_pay_salary);
            this.tvPayBalance = (TextView) itemView.findViewById(R.id.tv_pay_balance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (ViewUtils.isFastClick()) return;
            if (mItemClickListener != null) {
                try {
                    AccountInfo accountInfo = getItem(getAdapterPosition());
                    if (Integer.parseInt(accountInfo.aae001) <= 1996) {
                        return;
                    }
                } catch (Exception e) {

                }
                mItemClickListener.onItemClick(AccountAdapter.this, getAdapterPosition());
            }
        }
    }
}