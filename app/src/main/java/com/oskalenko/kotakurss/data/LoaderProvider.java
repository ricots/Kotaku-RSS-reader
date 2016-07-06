package com.oskalenko.kotakurss.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;
import static com.oskalenko.kotakurss.data.local.FeedsPersistenceContract.FeedEntry.COLUMN_NAME_DATE;

public class LoaderProvider {

    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        mContext = checkNotNull(context, "context cannot be null");
    }

    public Loader<Cursor> createFeedsLoader(String sortOrder) {
        String selection = null;
        String[] selectionArgs = null;
        String sortOrderArgs = COLUMN_NAME_DATE + " " + sortOrder;

        return new CursorLoader(
                mContext,
                FeedsPersistenceContract.FeedEntry.buildFeedsUri(),
                FeedsPersistenceContract.FeedEntry.FEEDS_COLUMNS, selection, selectionArgs, sortOrderArgs
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
