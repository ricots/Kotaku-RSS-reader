package com.oskalenko.kotakurss.data.remote;

import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.common.Constants;
import com.oskalenko.kotakurss.data.FeedsDataSource;
import com.oskalenko.kotakurss.data.Injection;
import com.oskalenko.kotakurss.data.model.Feed;

import static com.oskalenko.kotakurss.common.Constants.FEEDS_URL;

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
        FeedsParser feedsParser = Injection.provideFeedsParser();
        new FeedsDownloadTask(callback, feedsParser).execute(FEEDS_URL);
    }

    @Override
    public void getFeed(@NonNull String feedId, @NonNull GetFeedCallback callback) {
    }

    @Override
    public void saveFeed(@NonNull Feed feed) {
    }
}