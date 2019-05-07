package org.kteam.palm.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.kteam.palm.common.view.ModuleButton;
import org.kteam.palm.R;
import org.kteam.palm.common.view.AdView;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.Ad;
import org.kteam.palm.model.Module;

import java.util.List;

/**
 * @Package org.kteam.palm.home.adapter
 * @Project Palm
 * @Description 可控制是否滑动的ViewPager
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-11-29 17:38
 */
public class ModuleAdapter extends BaseListAdapter<Module> implements ModuleButton.OnModuleButtonClickListener {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 3;

    private OnRVItemClickListener mOnItemClickListener;

    private List<Ad> mHeaderData;

    private int mItemHeight;

    private AdView mAdView;

    public void setHeaderData(List<Ad> list) {
        mHeaderData = list;
    }

    public void setItemHeight(int itemHeight) {
        mItemHeight = itemHeight;
    }

    public void setOnItemCLickListener(OnRVItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_ITEM ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER )  {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
            return new FooterViewHolder(view);
        } else {
            throw new RuntimeException("There is no type that matches the type " + viewType + ", make sure your using types correctly");
        }

    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_ITEM;
        if (position == 0) {
            type = TYPE_HEADER;
        } else if (position == mDataList.size() - 1) {
            type = TYPE_FOOTER;
        }
        return type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            setFullSpan(holder);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mHeaderData != null) {
                mAdView = headerViewHolder.adView;
                headerViewHolder.adView.setAdList(mHeaderData);
            }
        } else if (holder instanceof ItemViewHolder) {
            Module module = mDataList.get(position);
            String name = module.name;
            if (!TextUtils.isEmpty(name) && name.contains("|")) {
                name = name.replace("|", "\n");
            }
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            holder.itemView.getLayoutParams().height = mItemHeight;
            itemViewHolder.moduleButton.getLayoutParams().height = mItemHeight;
            itemViewHolder.moduleButton.setText(name, getModuleBgResId(position));
            itemViewHolder.moduleButton.setPosition(position);
            itemViewHolder.moduleButton.setOnModuleButtonClickListener(this);
            itemViewHolder.moduleButton.setMsgVisiblie(module.tz_flag == 1? View.VISIBLE : View.GONE);
        } else if (holder instanceof FooterViewHolder) {
            Module module = mDataList.get(position);
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            setFullSpan(holder);
            holder.itemView.getLayoutParams().height = mItemHeight;
            footerViewHolder.moduleButton.getLayoutParams().height = mItemHeight;
            footerViewHolder.moduleButton.setText(module.name, getModuleBgResId(position));
            footerViewHolder.moduleButton.setPosition(position);
            footerViewHolder.moduleButton.setOnModuleButtonClickListener(this);
            footerViewHolder.moduleButton.setMsgVisiblie(module.tz_flag == 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder viewHolder) {
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        if (null != layoutParams && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        AdView adView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.adView = (AdView) itemView.findViewById(R.id.ad_view);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ModuleButton moduleButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.moduleButton = (ModuleButton) itemView.findViewById(R.id.mb);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ModuleButton moduleButton;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.moduleButton = (ModuleButton) itemView.findViewById(R.id.mb);
        }

    }

    @Override
    public void onModuleButtonClick(ModuleButton button) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(ModuleAdapter.this, button.getPosition());
        }
    }

    public int getModuleBgResId(int position) {
        int resId = R.color.module_1;
        switch (position - 1 % 10 ) {
            case 0:
                resId = R.color.module_1;
                break;
            case 1:
                resId = R.color.module_2;
                break;
            case 2:
                resId = R.color.module_3;
                break;
            case 3:
                resId = R.color.module_4;
                break;
            case 4:
                resId = R.color.module_5;
                break;
            case 5:
                resId = R.color.module_6;
                break;
            case 6:
                resId = R.color.module_7;
                break;
            case 7:
                resId = R.color.module_8;
                break;
            case 8:
                resId = R.color.module_9;
                break;
            default:
                break;
        }
        return resId;
    }

}
