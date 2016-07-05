package com.oskalenko.kotakurss.data;

import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.model.Feed;

import java.util.List;

/**
 * Main entry point for accessing tasks data.
 */
public interface FeedsDataSource {

    interface GetFeedsCallback {

        void onFeedsLoaded(List<Feed> feeds);

        void onDataNotAvailable();
    }

    interface GetFeedCallback {

        void onFeedLoaded(Feed feed);

        void onDataNotAvailable();
    }

    void getFeeds(@NonNull GetFeedsCallback callback);

    void getFeed(@NonNull String feedId, @NonNull GetFeedCallback callback);

    void saveFeed(@NonNull Feed feed);
}