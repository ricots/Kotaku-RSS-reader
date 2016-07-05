package com.oskalenko.kotakurss.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oskalenko.kotakurss.interfaces.ItemClickView;
import com.oskalenko.kotakurss.interfaces.OnClickViewListener;
import com.oskalenko.kotakurss.interfaces.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 21:07
 */

public abstract class BaseAdapter<T1, T2 extends View & ViewModel & ItemClickView, T3 extends BaseViewHolder<T2>> extends
        RecyclerView.Adapter<T3> {

    protected List<T1> mContent = new ArrayList<>();
    protected OnClickViewListener mClickListener;

    public void setClickListener(OnClickViewListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public abstract T3 onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(T3 holder, int position) {
        T2 view = holder.getView();
        view.setClickListener(mClickListener);
        view.setPosition(position);
        view.setData(mContent.get(position));
    }

    public T1 getModel(int position) {
        return mContent.get(position);
    }

    public void addData(T1 data) {
        mContent.add(data);
        notifyItemInserted(mContent.size() - 1);
    }

    public void addData(T1 data, int position) {
        mContent.add(position, data);
        notifyItemInserted(mContent.size() - 1);
    }

    public List<T1> getData() {
        return mContent;
    }

    public void setData(List<T1> data) {
        if (data != null) {
            mContent = data;
            notifyDataSetChanged();
        }
    }

    public void updateData(int position, T1 data) {
        if (data != null) {
            mContent.remove(position);
            mContent.add(position, data);
            notifyDataSetChanged();
        }
    }

    public void insertDataToStart(T1 data) {
        mContent.add(0, data);
        notifyItemInserted(0);
    }

    public void removeData(int position) {
        mContent.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void clearData() {
        mContent.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mContent == null) ? 0 : mContent.size();
    }

    public T1 getItem(int position) {
        return mContent.get(position);
    }
}
