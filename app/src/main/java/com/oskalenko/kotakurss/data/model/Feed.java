package com.oskalenko.kotakurss.data.model;

import android.database.Cursor;

import com.oskalenko.kotakurss.data.local.FeedsPersistenceContract;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    private long id;
    private String title;
    private String description;
    private String link;
    private long date;
    private String imageUrl;

    public Feed(long id, String title, String description, String link, long date, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Convert Cursor to Feed list
     */
    public static List<Feed> from(Cursor cursor) {
        List<Feed> feeds = new ArrayList<>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            feeds.add(fromCursor(cursor));
            cursor.moveToNext();
        }

        return feeds;
    }

    private static Feed fromCursor(Cursor cursor) {
        final long feedId = cursor.getLong(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry._ID));
        final String title = cursor.getString(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry.COLUMN_NAME_TITLE));
        final String description = cursor.getString(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry.COLUMN_NAME_DESCRIPTION));
        final String link = cursor.getString(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry.COLUMN_NAME_LINK));
        final long date = cursor.getLong(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry.COLUMN_NAME_DATE));
        final String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                FeedsPersistenceContract.FeedEntry.COLUMN_NAME_IMAGE_URL));
        return new Feed(feedId, title, description, link, date, imageUrl);
    }

    @Override
    public String toString() {
        return "FeedMessage{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", date='" + date + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
