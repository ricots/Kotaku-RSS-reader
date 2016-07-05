package com.oskalenko.kotakurss.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

public class LoaderProvider {

    @NonNull
    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        mContext = checkNotNull(context, "context cannot be null");
    }

    public Loader<Cursor> createFeedsLoader() {
        String selection = null;
        String[] selectionArgs = null;

        return new CursorLoader(
                mContext,
                FeedsPersistenceContract.FeedEntry.buildFeedsUri(),
                FeedsPersistenceContract.FeedEntry.FEEDS_COLUMNS, selection, selectionArgs, null
        );
    }

    public Loader<Cursor> createFeedLoader(String taskId) {
        return new CursorLoader(mContext, FeedsPersistenceContract.FeedEntry.buildFeedsUriWith(taskId),
                                null,
                                null,
                                new String[]{String.valueOf(taskId)}, null
        );
    }
}
