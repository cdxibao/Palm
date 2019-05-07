package org.kteam.palm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package org.kteam.palm.adapter
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 15:45
 */
public class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> mDataList;

    public BaseListAdapter() {
        mDataList = new ArrayList<T>();
    }

    public void clearData() {
        mDataList.clear();
    }

    public void setData(List<T> list) {
        mDataList.clear();
        mDataList.addAll(list);
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void appendDataList(List<T> list) {
        mDataList.addAll(list);
    }

    public void appendData(T t) {
        mDataList.add(t);
    }

    public void appendListData(List<T> list) {
        mDataList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public T getItem(int position) {
        return mDataList.get(position);
    }

}
