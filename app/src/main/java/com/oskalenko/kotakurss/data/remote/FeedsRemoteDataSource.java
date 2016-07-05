package com.oskalenko.kotakurss.data.remote;

import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.FeedsDataSource;
import com.oskalenko.kotakurss.data.model.Feed;

/**
 * Implementation of the remote data source.
 */
public class FeedsRemoteDataSource implements FeedsDataSource {

    private static FeedsRemoteDataSource INSTANCE;

    private FeedsRemoteDataSource() {
    }

    public static FeedsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeedsRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getFeeds(FeedsDataSource.GetFeedsCallback callback) {
        callback.onFeedsLoaded(null);
    }

    @Override
    public void getFeed(@NonNull String feedId, @NonNull GetFeedCallback callback) {
        callback.onFeedLoaded(null);
    }

    @Override
    public void saveFeed(@NonNull Feed feed) {

    }
}