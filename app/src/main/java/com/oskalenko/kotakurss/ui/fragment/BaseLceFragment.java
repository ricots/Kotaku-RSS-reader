package com.oskalenko.kotakurss.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oskalenko.kotakurss.R;
import com.oskalenko.kotakurss.ui.adapter.BaseAdapter;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 21:12
 */
public abstract class BaseLceFragment<M extends List> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout mContentView;
    protected View mErrorView;
    protected TextView mEmptyView;
    protected M mData;

    protected abstract void initRecyclerView();
    protected abstract void loadData(boolean pullToRefresh);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mContentView == null) {
            throw new NullPointerException(
                    "Content view is null! Have you specified a content view in your layout xml file?"
                            + " You have to give your content View the id R.id.mContentView");
        }

        if (mErrorView == null) {
            throw new NullPointerException(
                    "Error view is null! Have you specified a content view in your layout xml file?"
                            + " You have to give your error View the id R.id.mErrorView");
        }

        if (mEmptyView == null) {
            throw new NullPointerException(
                    "Empty view is null! Have you specified a content view in your layout xml file?"
                            + " You have to give your error View the id R.id.mEmptyView");
        }

        mErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorViewClicked();
            }
        });

        initRecyclerView();
        initRefreshLayout();
        loadData(false);
    }

    @Override
    protected void bindViews(View view) {
        mContentView = (SwipeRefreshLayout) view.findViewById(R.id.contentView);
        mErrorView = view.findViewById(R.id.errorView);
        mEmptyView = (TextView) view.findViewById(R.id.emptyView);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void showError(Throwable throwable) {
        super.showError(throwable);

        hideRefreshing();
        showErrorView();
    }

    protected void setData(M data) {
        mData = data;

        hideRefreshing();
        mErrorView.setVisibility(GONE);
        mEmptyView.setVisibility(data != null && data.size() > 0 ? GONE : VISIBLE);

        getAdapter().setData(data);
    }

    protected void initRefreshLayout() {
        if (mContentView != null) {
            mContentView.setOnRefreshListener(this);
            mContentView.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorWhite);
        }
    }

    protected RecyclerView getRecyclerView() {
        return null;
    }

    protected BaseAdapter getAdapter() {
        if (getRecyclerView() == null) {
            return null;
        }
        return (BaseAdapter) getRecyclerView().getAdapter();
    }

    /**
     * Called if the error view has been clicked. To disable clicking on the mErrorView use
     * <code>mErrorView.setClickable(false)</code>
     */
    protected void onErrorViewClicked() {
        mErrorView.setVisibility(GONE);
        loadData(false);
    }

    /**
     * Try to retry content loading
     **/
    protected void showErrorView() {
        mErrorView.setVisibility(VISIBLE);
    }

    private void hideRefreshing() {
        mContentView.setRefreshing(false);
    }
}
