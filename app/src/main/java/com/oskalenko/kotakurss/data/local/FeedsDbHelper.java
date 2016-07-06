package com.oskalenko.kotakurss.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract.FeedEntry;

public class FeedsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "feeds.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_IMAGE_URL + TEXT_TYPE +
            " )";

    public FeedsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
