package com.oskalenko.kotakurss.data.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.oskalenko.kotakurss.data.FeedsDataSource;
import com.oskalenko.kotakurss.data.model.Feed;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 21:24
 */
public class FeedsDownloadTask extends AsyncTask<String, Void, List<Feed>> {

    private static final String TAG = FeedsDownloadTask.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private FeedsDataSource.GetFeedsCallback mCallback;
    private FeedsParser mFeedsParser;

    public FeedsDownloadTask(@NonNull FeedsDataSource.GetFeedsCallback callback, @NonNull FeedsParser feedsParser) {
        checkNotNull(callback);
        checkNotNull(feedsParser);
        mCallback = callback;
        mFeedsParser = feedsParser;
    }

    @Override
    protected List<Feed> doInBackground(String... urls) {
        try {
            return loadRssFromNetwork(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Feed> feeds) {
        if (feeds != null) {
            mCallback.onFeedsLoaded(feeds);
        } else {
            mCallback.onDataNotAvailable();
        }
    }

    private List<Feed> loadRssFromNetwork(@NonNull String urlString) throws XmlPullParserException, IOException {
        checkNotNull(urlString);

        InputStream stream = null;
        List<Feed> feeds = null;

        try {
            stream = downloadUrl(urlString);
            feeds = mFeedsParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return feeds;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}