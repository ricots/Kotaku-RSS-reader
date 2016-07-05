package com.oskalenko.kotakurss.data;

import android.content.ContentValues;

import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract;
import com.oskalenko.kotakurss.data.model.Feed;

public class FeedValues {

    public static ContentValues from(Feed feed) {
        ContentValues values = new ContentValues();
        values.put(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID, feed.getId());
        values.put(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_TITLE, feed.getTitle());
        values.put(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_DESCRIPTION, feed.getDescription());
        values.put(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_DATE, feed.getDate());
        values.put(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_IMAGE_URL, feed.getImageUrl());
        return values;
    }
}
