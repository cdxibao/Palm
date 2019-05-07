package org.kteam.palm.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.common.utils.DateUtils;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.Article;

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
public class ArticleAdapter extends BaseListAdapter<Article> {

    private OnRVItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Article article = getItem(position);
        itemViewHolder.tvName.setText(article.title);
        itemViewHolder.tvBrief.setText(article.brief);

        itemViewHolder.tvTime.setText(DateUtils.friendlyDate(new Date(article.create_date)));
        Resources resources = itemViewHolder.itemView.getContext().getResources();
        int itemBgColor = position % 2  == 0 ? resources.getColor(R.color.white) : resources.getColor(R.color.item3);
        itemViewHolder.itemView.setBackgroundColor(itemBgColor);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvBrief;
        TextView tvTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tvBrief = (TextView) itemView.findViewById(R.id.tv_brief);
            this.tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(ArticleAdapter.this, getAdapterPosition());
            }
        }
    }
}
