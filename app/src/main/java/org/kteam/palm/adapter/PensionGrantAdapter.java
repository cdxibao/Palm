package org.kteam.palm.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.network.response.PensionGrantInfo;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 19:53
 */
public class PensionGrantAdapter extends BaseListAdapter<PensionGrantInfo> {

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    public void setItemWidth(int item1Width, int item2Width, int item3Width ,int item4Width) {
        mItem1Width = item1Width;
        mItem2Width = item2Width;
        mItem3Width = item3Width;
        mItem4Width = item4Width;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        PensionGrantInfo grantInfo = getItem(position);
        itemViewHolder.tvUserName.setText(grantInfo.aac003);
        if (!TextUtils.isEmpty(grantInfo.aae003) && grantInfo.aae003.length() >= 6) {
            itemViewHolder.tvYearMonth.setText(itemViewHolder.itemView.getContext().getString(R.string.year_month, grantInfo.aae003.substring(0, 4), grantInfo.aae003.substring(4, 6)));
        }
        itemViewHolder.tvPaySum.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, grantInfo.aic910));
        itemViewHolder.tvPayAccount.setText(grantInfo.aae010);
        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pension_grant, null);
        return new ItemViewHolder(view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvYearMonth;
        TextView tvPaySum;
        TextView tvPayAccount;

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

            this.tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
            this.tvYearMonth = (TextView) itemView.findViewById(R.id.tv_year_month);
            this.tvPaySum = (TextView) itemView.findViewById(R.id.tv_pay_sum);
            this.tvPayAccount = (TextView) itemView.findViewById(R.id.tv_pay_account);
        }
    }

}
