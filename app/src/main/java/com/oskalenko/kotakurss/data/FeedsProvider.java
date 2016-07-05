package com.oskalenko.kotakurss.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.oskalenko.kotakurss.data.local.FeedsDbHelper;
import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract;

public class FeedsProvider extends ContentProvider {

    private static final int FEEDS = 100;
    private static final int FEED = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FeedsDbHelper mFeedsDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FeedsPersistenceContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FeedsPersistenceContract.FeedEntry.TABLE_NAME, FEEDS);
        matcher.addURI(authority, FeedsPersistenceContract.FeedEntry.TABLE_NAME + "/*", FEED);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mFeedsDbHelper = new FeedsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FEEDS:
                return FeedsPersistenceContract.CONTENT_FEEDS_TYPE;
            case FEED:
                return FeedsPersistenceContract.CONTENT_FEED_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FEEDS:
                retCursor = mFeedsDbHelper.getReadableDatabase().query(
                        FeedsPersistenceContract.FeedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FEED:
                String[] where = {uri.getLastPathSegment()};
                retCursor = mFeedsDbHelper.getReadableDatabase().query(
                        FeedsPersistenceContract.FeedEntry.TABLE_NAME,
                        projection,
                        FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mFeedsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FEEDS:
                Cursor exists = db.query(
                        FeedsPersistenceContract.FeedEntry.TABLE_NAME,
                        new String[]{FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID},
                        FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                        new String[]{values.getAsString(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID)},
                        null,
                        null,
                        null
                );
                if (exists.moveToLast()) {
                    long _id = db.update(
                            FeedsPersistenceContract.FeedEntry.TABLE_NAME, values,
                            FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                            new String[]{values.getAsString(FeedsPersistenceContract.FeedEntry.COLUMN_NAME_ENTRY_ID)}
                    );
                    if (_id > 0) {
                        returnUri = FeedsPersistenceContract.FeedEntry.buildFeedsUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } else {
                    long _id = db.insert(FeedsPersistenceContract.FeedEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = FeedsPersistenceContract.FeedEntry.buildFeedsUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                exists.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFeedsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FEEDS:
                rowsDeleted = db.delete(
                        FeedsPersistenceContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFeedsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FEEDS:
                rowsUpdated = db.update(FeedsPersistenceContract.FeedEntry.TABLE_NAME, values, selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
