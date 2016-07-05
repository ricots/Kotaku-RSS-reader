package com.oskalenko.kotakurss.data.remote;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.oskalenko.kotakurss.data.model.Feed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 18:21
 */
public class FeedsParser {

    private static final String TAG = FeedsParser.class.getSimpleName();
    private static final String NS = null;
    private static final String IMAGE_URL_PATTERN = "(?m)(?s)<img\\s+(.*)src\\s*=\\s*\"([^\"]+)\"(.*)";
    private static final String PUB_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String GUID = "guid";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESCRIPTION = "description";
    private static final String PUB_DATE = "pubDate";

    private SimpleDateFormat mSimpleDateFormat;

    public FeedsParser() {
        mSimpleDateFormat = new SimpleDateFormat(PUB_DATE_FORMAT);
    }

    public List<Feed> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Feed> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Feed> entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, NS, "rss");

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            }
        }

        return entries;
    }

    private Feed readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, "item");

        String title = null;
        String description = null;
        String link = null;
        long pubDate = 0L;
        String imageUrl = null;
        long guid = 0L;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(DESCRIPTION)) {
                description = readDescription(parser);
                imageUrl = getImageUrl(description);
            } else if (name.equals(LINK)) {
                link = readLink(parser);
            } else if (name.equals(PUB_DATE)) {
                pubDate = readPubDate(parser);
            } else if (name.equals(GUID)) {
                guid = readGuid(parser);
            } else {
                skip(parser);
            }
        }

        return new Feed(guid, title, description, link, pubDate, imageUrl);
    }

    private String getImageUrl(String description) {
        Pattern pattern = Pattern.compile(IMAGE_URL_PATTERN);
        Matcher matcher = pattern.matcher(description);
        String src = null;
        if (matcher.matches()) {
            src = matcher.group(2);
        }
        return src;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, TITLE);
        return title;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, LINK);
        return link;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, DESCRIPTION);
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, DESCRIPTION);
        return description;
    }

    private long readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, PUB_DATE);

        final String pubDate = readText(parser);
        if (!TextUtils.isEmpty(pubDate)) {
            try {
                return mSimpleDateFormat.parse(pubDate).getTime();
            } catch (ParseException e) {
                Log.e(TAG + ":readPubDate", e.getMessage());
            }
        }

        parser.require(XmlPullParser.END_TAG, NS, PUB_DATE);

        return 0L;
    }

    private long readGuid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, GUID);
        String guid = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, GUID);
        return Long.parseLong(guid);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
