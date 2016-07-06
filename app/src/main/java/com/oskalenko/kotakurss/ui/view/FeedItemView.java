package com.oskalenko.kotakurss.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.manager.ImageManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 11:09
 */
public class FeedItemView extends BaseItemView<Feed> implements View.OnClickListener {

    private static final String DATE_FORMAT = "EEE, d MMM yyyy";

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mDateTextView;
    private View mReadMoreView;

    private SimpleDateFormat mSimpleDateFormat;

    public FeedItemView(Context context) {
        super(context);
        mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    protected void bindViews(View view) {
        super.bindViews(view);

        mTitleTextView = (TextView) view.findViewById(R.id.item_feed_view_title_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.item_feed_view_description_text_view);
        mDateTextView = (TextView) view.findViewById(R.id.item_feed_view_date_text_view);
        mReadMoreView = view.findViewById(R.id.item_feed_view_read_more_text_view);
        mReadMoreView.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.item_feed_view;
    }

    @Override
    public void setData(Feed feed) {
        if (feed != null) {
            mTitleTextView.setText(feed.getTitle());
            if (!TextUtils.isEmpty(feed.getDescription())) {
                mDescriptionTextView.setText(Html.fromHtml(feed.getDescription()));
            }
            mDateTextView.setText(mSimpleDateFormat.format(new Date(feed.getDate())));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_feed_view_read_more_text_view:
                mClickListener.onClickListener(view, mPosition);
                break;
        }
    }
}
