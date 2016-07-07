package com.oskalenko.kotakurss.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.oskalenko.kotakurss.interfaces.ItemClickView;
import com.oskalenko.kotakurss.interfaces.OnClickViewListener;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 21:08
 */

public class BaseViewHolder<T1 extends View> extends RecyclerView.ViewHolder
        implements ItemClickView {
    private T1 mItemView;

    public BaseViewHolder(T1 itemView) {
        super(itemView);
        this.mItemView = itemView;
    }

    public T1 getView() {
        return mItemView;
    }

    public void setView(T1 view) {
        mItemView = view;
    }

    @Override
    public void setPosition(int position) {
    }

    @Override
    public void setClickListener(OnClickViewListener clickListener) {
    }
}
