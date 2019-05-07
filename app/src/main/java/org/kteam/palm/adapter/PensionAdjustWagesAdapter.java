package org.kteam.palm.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.network.response.PensionAdjustWages;
import org.kteam.palm.network.response.PensionForm;
import org.w3c.dom.Text;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-17 00:10
 */
public class PensionAdjustWagesAdapter extends BaseListAdapter<PensionAdjustWages> {

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        PensionAdjustWages pensionForm = getItem(position);
        if (!TextUtils.isEmpty(pensionForm.aae002) && pensionForm.aae002.length() >= 6) {
            String year = itemViewHolder.itemView.getContext().getString(R.string.year, pensionForm.aae002.substring(0, 4));
            String money = itemViewHolder.itemView.getContext().getString(R.string.yuan, pensionForm.hj);
            itemViewHolder.tvAdjustYear.setText(year);
            itemViewHolder.tvAdjustMoney.setText(money);
        }
        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adjust_wages, null);
        return new ItemViewHolder(view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdjustYear;
        TextView tvAdjustMoney;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.tvAdjustYear = (TextView) itemView.findViewById(R.id.tv_adjust_year);
            this.tvAdjustMoney = (TextView) itemView.findViewById(R.id.tv_adjust_money);
            this.tvAdjustMoney.getLayoutParams().width = ViewUtils.getScreenInfo(itemView.getContext()).widthPixels;
        }
    }
}
