package com.oskalenko.kotakurss.ui.adapter;

import android.view.ViewGroup;

import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.manager.ImageManager;
import com.oskalenko.kotakurss.ui.view.FeedItemView;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 10:57
 */
public class FeedsAdapter extends ImageBaseAdapter<Feed, FeedItemView> {

    public FeedsAdapter(ImageManager imageManager) {
        super(imageManager);
    }

    @Override
    protected String getImageUrl(int position) {
        return getItem(position).getImageUrl();
    }

    @Override
    public BaseViewHolder<FeedItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(new FeedItemView(parent.getContext()));
    }
}