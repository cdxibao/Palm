package org.kteam.palm.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.activity.PayTypeActivity;
import org.kteam.palm.common.view.OnRVItemClickListener;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 21:08
 */
public class PayBankAdapter extends BaseListAdapter<PayTypeActivity.BankData> {

    private PayTypeActivity.BankData mCheckedBankData;
    private OnRVItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    public PayTypeActivity.BankData getCheckedBankData() {
        return mCheckedBankData;
    }

    public void setCheckedBankData(PayTypeActivity.BankData mCheckedBankData) {
        this.mCheckedBankData = mCheckedBankData;
    }

    public OnRVItemClickListener getmItemClickListener() {
        return mItemClickListener;
    }

    public void setmItemClickListener(OnRVItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_bank, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        PayTypeActivity.BankData bankData = getItem(position);
        itemViewHolder.tvBank.setText(bankData.label);
        itemViewHolder.ivBank.setImageResource(ViewUtils.getResource(itemViewHolder.itemView.getContext(), bankData.iconName));

        itemViewHolder.cbBank.setChecked(mCheckedBankData != null && bankData.id.equals(mCheckedBankData.id));
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivBank;
        TextView tvBank;
        CheckBox cbBank;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.ivBank = (ImageView) itemView.findViewById(R.id.iv_bank);
            this.tvBank = (TextView) itemView.findViewById(R.id.tv_bank);
            this.cbBank = (CheckBox) itemView.findViewById(R.id.cb_bank);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(PayBankAdapter.this, getAdapterPosition());
            }
        }
    }
}
