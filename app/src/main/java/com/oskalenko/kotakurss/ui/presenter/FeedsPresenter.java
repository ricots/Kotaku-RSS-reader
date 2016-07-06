package com.oskalenko.kotakurss.ui.presenter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.oskalenko.kotakurss.data.FeedsDataSource;
import com.oskalenko.kotakurss.data.FeedsRepository;
import com.oskalenko.kotakurss.data.LoaderProvider;
import com.oskalenko.kotakurss.data.model.Feed;
import com.oskalenko.kotakurss.manager.PrefManager;
import com.oskalenko.kotakurss.ui.presenter.view.FeedsContract;

import java.util.List;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 10:25
 */
public class FeedsPresenter implements FeedsContract.Presenter, FeedsRepository.LoadDataCallback,
        FeedsDataSource.GetFeedsCallback, LoaderManager.LoaderCallbacks<Cursor> {

    public final static int TASKS_LOADER = 1;

    private final FeedsContract.View mView;

    private final FeedsRepository mFeedsRepository;
    private final LoaderManager mLoaderManager;
    private final LoaderProvider mLoaderProvider;
    private final PrefManager mPrefManager;

    public FeedsPresenter(@NonNull LoaderProvider loaderProvider,
                          @NonNull LoaderManager loaderManager,
                          @NonNull FeedsRepository feedsRepository,
                          @NonNull FeedsContract.View view,
                          @NonNull PrefManager prefManager) {

        mLoaderProvider = checkNotNull(loaderProvider, "loaderProvider provider cannot be null");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager provider cannot be null");
        mFeedsRepository = checkNotNull(feedsRepository, "FeedsRepository provider cannot be null");
        mView = checkNotNull(view, "FeedsView cannot be null!");
        mPrefManager = checkNotNull(prefManager, "PrefManager provider cannot be null");

        mView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void start() {
        loadFeeds();
    }

    /**
     * We will always have fresh data from remote, the Loaders handle the local data
     */
    @Override
    public void loadFeeds() {
        mView.setLoading(true);
        mFeedsRepository.getFeeds(this);
    }

    @Override
    public void onDataLoaded(Cursor cursor) {
        mView.setLoading(false);
        // Show the list of tasks
        mView.showFeeds(Feed.from(cursor));
    }

    @Override
    public void onFeedsLoaded(List<Feed> feeds) {
        if (mLoaderManager.getLoader(TASKS_LOADER) == null) {
            mLoaderManager.initLoader(TASKS_LOADER, null, this);
        } else {
            mLoaderManager.restartLoader(TASKS_LOADER, null, this);
        }
    }

    @Override
    public void onDataEmpty() {
        mView.setLoading(false);
    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onDataReset() {
        mView.showFeeds(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createFeedsLoader(mPrefManager.getSortOrder());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onDataReset();
    }

    @Override
    public void openFeedDetails(@NonNull Feed feed) {

    }
}
