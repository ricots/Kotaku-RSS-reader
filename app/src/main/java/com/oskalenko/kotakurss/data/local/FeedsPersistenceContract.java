package com.oskalenko.kotakurss.data.local;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.oskalenko.kotakurss.BuildConfig;

/**
 * The contract used for the db to save the feeds locally.
 */
public final class FeedsPersistenceContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final String CONTENT_FEEDS_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + FeedEntry.TABLE_NAME;
    public static final String CONTENT_FEED_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + FeedEntry.TABLE_NAME;
    public static final String VND_ANDROID_CURSOR_ITEM_VND = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".";
    private static final String CONTENT_SCHEME = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);
    private static final String VND_ANDROID_CURSOR_DIR_VND = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".";
    private static final String SEPARATOR = "/";

    public FeedsPersistenceContract() {
    }

    public static Uri getBaseFeedUri(String feedId) {
        return Uri.parse(CONTENT_SCHEME + CONTENT_FEED_TYPE + SEPARATOR + feedId);
    }

    /* Class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "feed";
        public static final String COLUMN_NAME_ENTRY_ID = "feedId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final Uri CONTENT_FEED_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static String[] FEEDS_COLUMNS = new String[]{
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_ENTRY_ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_DESCRIPTION,
                FeedEntry.COLUMN_NAME_LINK,
                FeedEntry.COLUMN_NAME_DATE,
                FeedEntry.COLUMN_NAME_IMAGE_URL
        };

        public static Uri buildFeedsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FEED_URI, id);
        }

        public static Uri buildFeedsUriWith(String id) {
            Uri uri = CONTENT_FEED_URI.buildUpon().appendPath(id).build();
            return uri;
        }

        public static Uri buildFeedsUri() {
            return CONTENT_FEED_URI.buildUpon().build();
        }

    }
}
