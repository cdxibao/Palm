package org.kteam.palm.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.LeftMenu;
import org.kteam.palm.model.SettingMenu;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 21:08
 */
public class SettingMenuAdapter extends BaseListAdapter<SettingMenu> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_PLACEHOLDER = 2;

    private OnRVItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.get(position).type != LeftMenu.LeftMenuType.UNKNOWN) {
            return TYPE_ITEM;
        } else {
            return TYPE_PLACEHOLDER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_menu, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_holder, parent, false);
            return new ItemPlaceHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            SettingMenu menu = getItem(position);
            itemViewHolder.iv.setImageResource(menu.iconResId);
            itemViewHolder.tvTitle.setText(menu.titleResId);
            if (!TextUtils.isEmpty(menu.subTitle)) {
                itemViewHolder.tvSubTitle.setVisibility(View.VISIBLE);
                itemViewHolder.tvSubTitle.setText(menu.subTitle);
            } else {
                itemViewHolder.tvSubTitle.setVisibility(View.GONE);
                itemViewHolder.tvSubTitle.setText("");
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        TextView tvTitle;
        TextView tvSubTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.iv = (ImageView) itemView.findViewById(R.id.iv);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.tvSubTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(SettingMenuAdapter.this, getAdapterPosition());
            }
        }
    }

    public class ItemPlaceHolder extends RecyclerView.ViewHolder {

        public ItemPlaceHolder(View itemView) {
            super(itemView);
        }
    }

}
