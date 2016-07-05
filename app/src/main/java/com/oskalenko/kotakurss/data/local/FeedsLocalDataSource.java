package com.oskalenko.kotakurss.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.FeedValues;
import com.oskalenko.kotakurss.data.FeedsDataSource;
import com.oskalenko.kotakurss.data.model.Feed;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Implementation of a data source as a db.
 */
public class FeedsLocalDataSource implements FeedsDataSource {

    private static FeedsLocalDataSource INSTANCE;

    private ContentResolver mContentResolver;

    // Prevent direct instantiation.
    private FeedsLocalDataSource(@NonNull ContentResolver contentResolver) {
        checkNotNull(contentResolver);
        mContentResolver = contentResolver;
    }

    public static FeedsLocalDataSource getInstance(@NonNull ContentResolver contentResolver) {
        if (INSTANCE == null) {
            INSTANCE = new FeedsLocalDataSource(contentResolver);
        }
        return INSTANCE;
    }

    @Override
    public void getFeeds(@NonNull GetFeedsCallback callback) {
        // Data is loader via Cursor Loader
    }

    @Override
    public void getFeed(@NonNull String taskId, @NonNull GetFeedCallback callback) {
        // Data is loaded via Cursor Loader
    }

    @Override
    public void saveFeed(@NonNull Feed feed) {
        checkNotNull(feed);

        ContentValues values = FeedValues.from(feed);
        mContentResolver.insert(FeedsPersistenceContract.FeedEntry.buildFeedsUri(), values);
    }
}