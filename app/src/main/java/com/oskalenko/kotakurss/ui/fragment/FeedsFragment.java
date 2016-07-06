package com.oskalenko.kotakurss.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.interfaces.OnClickViewListener;
import com.oskalenko.kotakurss.manager.ImageManager;
import com.oskalenko.kotakurss.ui.activity.HomeActivity;
import com.oskalenko.kotakurss.ui.adapter.FeedsAdapter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

import java.util.List;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 22:35
 */

public class FeedsFragment extends BaseLceFragment<List<Feed>> implements FeedsContract.View, OnClickViewListener {

    private RecyclerView mFeedsRecyclerView;
    private Toolbar mToolbar;

    private FeedsAdapter mFeedsAdapter;

    public FeedsFragment() {
    }

    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageManager imageManager = new ImageManager();
        mFeedsAdapter =  new FeedsAdapter(imageManager);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getHomeActivity().setToolBar(mToolbar);
    }

    @Override
    protected void initRecyclerView() {
        mFeedsAdapter.setClickListener(this);
        mFeedsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFeedsRecyclerView.setAdapter(mFeedsAdapter);
    }

    @Override
    protected void bindViews(View view) {
        super.bindViews(view);
        mFeedsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_feeds_recycler_view);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void loadData(boolean pullToRefresh) {
        mPresenter.loadFeeds();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_feeds;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mFeedsRecyclerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_item:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showFeeds(List<Feed> feeds) {
        setData(feeds);
    }

    @Override
    public void setPresenter(FeedsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onClickListener(View view, int position) {
        Feed feed = mFeedsAdapter.getModel(position);
        switch (view.getId()) {
            case R.id.item_feed_view_read_more_text_view:
                getHomeActivity().startFeedDescriptionScreen(feed.getLink());
                break;
        }
    }

    private HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }
}
