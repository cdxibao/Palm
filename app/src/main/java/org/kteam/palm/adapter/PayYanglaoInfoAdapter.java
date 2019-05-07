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
import org.kteam.palm.network.response.PersonalYanglaoPayInfo;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-16 23:49
 */
public class PayYanglaoInfoAdapter extends BaseListAdapter<PersonalYanglaoPayInfo> {

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        PersonalYanglaoPayInfo payInfo = getItem(position);
        if (!TextUtils.isEmpty(payInfo.aae034)) {
            itemViewHolder.tvPayTime.setText(payInfo.aae034);
        }
        if (!TextUtils.isEmpty(payInfo.zac002) && payInfo.zac002.length() >= 4) {
            itemViewHolder.tvPayYear.setText(payInfo.zac002.substring(0, 4));
        }
        if (!TextUtils.isEmpty(payInfo.cab182)) {
            itemViewHolder.tvPayMonths.setText(itemViewHolder.itemView.getContext().getString(R.string.month_num, payInfo.cab182));
        }
        if (!TextUtils.isEmpty(payInfo.aab180)) {
            itemViewHolder.tvPaySum.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, payInfo.aab180));
        }
        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2  == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_yanglao_info, null);
        return new ItemViewHolder(view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvPayTime;
        TextView tvPayYear;
        TextView tvPayMonths;
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

            this.tvPayTime = (TextView) itemView.findViewById(R.id.tv_pay_time);
            this.tvPayYear = (TextView) itemView.findViewById(R.id.tv_pay_year);
            this.tvPayMonths = (TextView) itemView.findViewById(R.id.tv_pay_months);
            this.tvPaySum = (TextView) itemView.findViewById(R.id.tv_pay_sum);
        }
    }
}
