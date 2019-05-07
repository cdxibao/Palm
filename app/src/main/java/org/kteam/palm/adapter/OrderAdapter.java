package org.kteam.palm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.common.utils.DateUtils;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.Order;

import java.util.List;


/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-27 17:18
 */
public class OrderAdapter extends BaseListAdapter<Order> {

    private OnRVItemClickListener mItemClickListener;
    private boolean mOtherPayed = false;
    private List<BaseData> mPayedStatusList;

    public OrderAdapter(boolean otherPayed) {
        mOtherPayed = otherPayed;
        mPayedStatusList = new BaseDataManager().getPayStatusList();
    }

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Context context = itemViewHolder.itemView.getContext();
        Order order = getItem(position);
        String category = "1".equals(order.category) ? context.getString(R.string.pension) : context.getString(R.string.yiliao);
        itemViewHolder.tvPayedCategory.setText(context.getString(R.string.order_category, category));
        itemViewHolder.tvOrderNo.setText(context.getString(R.string.order_no, order.aadjno));
        itemViewHolder.tvPayedYear.setText(context.getString(R.string.pay_begin_year_month2, order.zac001, order.zac002));
        itemViewHolder.tvPayedMonth.setText(context.getString(R.string.pay_end_year_month2, order.end_year, order.zac003));
        itemViewHolder.tvPayedMoney.setText(context.getString(R.string.order_payed_money, order.total_fee));
        itemViewHolder.tvPayedState.setText(context.getString(R.string.order_pay_status, getPayStatus(order.pay_status)));
        if (!TextUtils.isEmpty(order.pay_way)) {
            String payType = "";
            switch (Integer.parseInt(order.pay_way)) {
                case 1:
                    payType = context.getString(R.string.ccbpay);
                    break;
                case 2:
                    payType = context.getString(R.string.unionpay);
                    break;
                case 3:
                    payType = context.getString(R.string.abcpay);
                    break;
                case 4:
                    payType = context.getString(R.string.alipay);
                    break;
                case 5:
                    payType = context.getString(R.string.weixinpay);
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(payType)) {
                itemViewHolder.tvPayedWay.setText(context.getString(R.string.order_pay_way, payType));
                itemViewHolder.tvPayedWay.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.tvPayedWay.setVisibility(View.GONE);
            }
        } else {
            itemViewHolder.tvPayedWay.setVisibility(View.GONE);
        }
        try {
            if (!TextUtils.isEmpty(order.pay_date)) {
                itemViewHolder.tvPayedTime.setText(context.getString(R.string.order_payed_time, DateUtils.getOrderDateStr(Long.parseLong(order.pay_date))));
                itemViewHolder.tvPayedTime.setVisibility(View.VISIBLE);
                itemViewHolder.layoutTime.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.tvPayedTime.setVisibility(View.GONE);
                itemViewHolder.layoutTime.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            itemViewHolder.tvPayedTime.setVisibility(View.GONE);
            itemViewHolder.layoutTime.setVisibility(View.GONE);
        }
        if (mOtherPayed) {
            itemViewHolder.tvPayedUser.setVisibility(View.VISIBLE);
            itemViewHolder.tvPayedUser.setText(context.getString(R.string.order_username, order.aac003));
        } else {
            itemViewHolder.tvPayedUser.setVisibility(View.GONE);
        }
    }

    private String getPayStatus(int status) {
        if (mPayedStatusList != null) {
            for (BaseData baseData : mPayedStatusList) {
                if (String.valueOf(status).equals(baseData.value)) {
                    return baseData.label;
                }
            }
        }
        return "";
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvOrderNo;
        TextView tvPayedCategory;
        TextView tvPayedYear;
        TextView tvPayedMonth;
        TextView tvPayedMoney;
        TextView tvPayedState;
        TextView tvPayedWay;
        TextView tvPayedTime;
        TextView tvPayedUser;
        View layoutTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.tvOrderNo = (TextView) itemView.findViewById(R.id.tv_order_no);
            this.tvPayedCategory = (TextView) itemView.findViewById(R.id.tv_payed_category);
            this.tvPayedYear = (TextView) itemView.findViewById(R.id.tv_payed_year);
            this.tvPayedMonth = (TextView) itemView.findViewById(R.id.tv_payed_month);
            this.tvPayedMoney = (TextView) itemView.findViewById(R.id.tv_payed_money);
            this.tvPayedState = (TextView) itemView.findViewById(R.id.tv_payed_state);
            this.tvPayedWay = (TextView) itemView.findViewById(R.id.tv_payed_way);
            this.tvPayedTime = (TextView) itemView.findViewById(R.id.tv_payed_time);
            this.tvPayedUser = (TextView) itemView.findViewById(R.id.tv_payed_user);
            this.layoutTime = itemView.findViewById(R.id.layout_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(OrderAdapter.this, getAdapterPosition());
            }
        }
    }
}
