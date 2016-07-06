package com.oskalenko.kotakurss.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.data.FeedsProvider;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.interfaces.OnClickViewListener;
import com.oskalenko.kotakurss.manager.ImageManager;
import com.oskalenko.kotakurss.manager.PrefManager;
import com.oskalenko.kotakurss.ui.activity.HomeActivity;
import com.oskalenko.kotakurss.ui.adapter.FeedsAdapter;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

import java.util.List;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;
import static com.oskalenko.kotakurss.data.FeedsProvider.SORT_ASCENDING;
import static com.oskalenko.kotakurss.data.FeedsProvider.SORT_DESCENDING;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 22:35
 */

public class FeedsFragment extends BaseLceFragment<List<Feed>>
        implements FeedsContract.View, OnClickViewListener, RadioGroup.OnCheckedChangeListener {

    private RecyclerView mFeedsRecyclerView;
    private Toolbar mToolbar;
    private RadioGroup mSortRadioGroup;

    private FeedsAdapter mFeedsAdapter;
    private ImageManager mImageManager;
    private PrefManager mPrefManager;

    public FeedsFragment() {
    }

    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectFields();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSortRadioGroup();
        getHomeActivity().setToolBar(mToolbar);
    }

    @Override
    protected void bindViews(View view) {
        super.bindViews(view);
        mFeedsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_feeds_recycler_view);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.fragment_feeds_sort_radio_group);
        mSortRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initRecyclerView() {
        mFeedsAdapter.setClickListener(this);
        mFeedsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFeedsRecyclerView.setAdapter(mFeedsAdapter);
    }

    @Override
    protected void loadData(boolean pullToRefresh) {
        mPresenter.loadFeeds(pullToRefresh);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.fragment_feeds_sort_ascending_radio_button:
                mPrefManager.setSortOrder(SORT_ASCENDING);
                break;
            case R.id.fragment_feeds_sort_descending_radio_button:
                mPrefManager.setSortOrder(SORT_DESCENDING);
                break;
        }

        loadData(false);
    }

    private void injectFields() {
        mImageManager = Injection.provideImageManager(getActivity());
        mPrefManager = Injection.providePrefManager(getActivity());
        mFeedsAdapter = new FeedsAdapter(mImageManager);
    }

    private void initSortRadioGroup() {
        String sortOrder = mPrefManager.getSortOrder();
        if (sortOrder.equals(SORT_ASCENDING)) {
            mSortRadioGroup.check(R.id.fragment_feeds_sort_ascending_radio_button);
        } else {
            mSortRadioGroup.check(R.id.fragment_feeds_sort_descending_radio_button);
        }
    }

    private HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }
}
