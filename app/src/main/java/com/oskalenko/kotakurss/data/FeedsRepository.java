package com.oskalenko.kotakurss.data;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.model.Feed;

import java.util.List;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Implementation to load feeds from the data sources into a cache.
 */
public class FeedsRepository implements FeedsDataSource {

    private static FeedsRepository INSTANCE = null;

    private final FeedsDataSource mFeedsRemoteDataSource;

    private final FeedsDataSource mFeedsLocalDataSource;

    private FeedsRepository(@NonNull FeedsDataSource feedsRemoteDataSource,
                            @NonNull FeedsDataSource feedsLocalDataSource) {
        mFeedsRemoteDataSource = checkNotNull(feedsRemoteDataSource);
        mFeedsLocalDataSource = checkNotNull(feedsLocalDataSource);
    }

    /**
     * Returns the single instance of this class.
     *
     * @param feedsRemoteDataSource the backend data source
     * @param feedsLocalDataSource  the device storage data source
     * @return the {@link FeedsRepository} instance
     */
    public static FeedsRepository getInstance(FeedsDataSource feedsRemoteDataSource,
                                              FeedsDataSource feedsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FeedsRepository(feedsRemoteDataSource, feedsLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(FeedsDataSource, FeedsDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p/>
     * Note: {@link GetFeedsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getFeeds(@NonNull final GetFeedsCallback callback) {
        checkNotNull(callback);

        // Load from back-end
        mFeedsRemoteDataSource.getFeeds(new GetFeedsCallback() {
            @Override
            public void onFeedsLoaded(List<Feed> feeds) {
                refreshLocalDataSource(feeds);
                callback.onFeedsLoaded(null);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Gets feeds from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source.
     */
    @Override
    public void getFeed(@NonNull final String feedId, @NonNull final GetFeedCallback callback) {
        checkNotNull(feedId);
        checkNotNull(callback);

        // Load from back-end
        mFeedsRemoteDataSource.getFeed(feedId, new GetFeedCallback() {
            @Override
            public void onFeedLoaded(Feed feed) {
                callback.onFeedLoaded(feed);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveFeed(@NonNull Feed feed) {
        checkNotNull(feed);
        mFeedsRemoteDataSource.saveFeed(feed);
        mFeedsLocalDataSource.saveFeed(feed);
    }

    private void refreshLocalDataSource(List<Feed> feeds) {
        if (feeds != null) {
            for (Feed feed : feeds) {
                mFeedsLocalDataSource.saveFeed(feed);
            }
        }
    }

    public interface LoadDataCallback {

        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}