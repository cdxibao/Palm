package org.kteam.palm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.common.utils.APKUtils;
import org.kteam.palm.common.utils.DateUtils;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.Article;
import org.kteam.palm.model.LeftMenu;

import java.util.Date;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 21:08
 */
public class LeftMenuAdapter extends BaseListAdapter<LeftMenu> {

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_menu, parent, false);
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
            LeftMenu leftMenu = getItem(position);
            itemViewHolder.iv.setImageResource(leftMenu.iconResId);
            if (leftMenu.type == LeftMenu.LeftMenuType.CHECK_VERSION) {
                Context context = itemViewHolder.itemView.getContext();
                APKUtils.Version version = APKUtils.getLocalVersionCode(context);
                if (version != null) {
                    itemViewHolder.tvTitle.setText(context.getString(leftMenu.titleResId) + "(当前版本:v" + version.versionName + ")");
                } else {
                    itemViewHolder.tvTitle.setText(leftMenu.titleResId);
                }
            } else {
                itemViewHolder.tvTitle.setText(leftMenu.titleResId);
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        TextView tvTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.iv = (ImageView) itemView.findViewById(R.id.iv);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(LeftMenuAdapter.this, getAdapterPosition());
            }
        }
    }

    public class ItemPlaceHolder extends RecyclerView.ViewHolder {

        public ItemPlaceHolder(View itemView) {
            super(itemView);
        }
    }

}
