package com.oskalenko.kotakurss.ui.view;

import android.content.Context;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.data.model.Feed;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 11:09
 */
public class FeedItemView extends BaseItemView<Feed> {

    public FeedItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.item_feed_view;
    }

    @Override
    public void setData(Feed feed) {
        if (feed != null) {

        }
    }
}
