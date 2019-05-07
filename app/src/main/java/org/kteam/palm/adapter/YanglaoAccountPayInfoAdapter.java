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
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.AccountInfo;
import org.kteam.palm.network.response.AccountPayInfo;
import org.kteam.palm.network.response.YanglaoInfoJgsf;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 *
 * @Description 养老保险缴费记录、养老保险职业年金
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-19 22:15
 */
public class YanglaoAccountPayInfoAdapter extends BaseListAdapter<AccountPayInfo> {

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
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_pay_info, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        AccountPayInfo payInfo = getItem(position);
        itemViewHolder.tvNiandu.setText(payInfo.aae001);
        itemViewHolder.tvYueshu.setText(payInfo.aic079);
        itemViewHolder.tvYjfjs.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, payInfo.aic020));
        itemViewHolder.tvGrjfje.setText(itemViewHolder.itemView.getContext().getString(R.string.yuan, payInfo.aic040));

        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2 == 0 ? resources.getColor(R.color.item_2) : resources.getColor(R.color.item_1);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvNiandu;
        TextView tvYueshu;
        TextView tvYjfjs;
        TextView tvGrjfje;

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

            this.tvNiandu = (TextView) itemView.findViewById(R.id.tv_niandu);
            this.tvYueshu = (TextView) itemView.findViewById(R.id.tv_yueshu);
            this.tvYjfjs = (TextView) itemView.findViewById(R.id.tv_yjfjs);
            this.tvGrjfje = (TextView) itemView.findViewById(R.id.tv_grjfje);
        }

    }
}