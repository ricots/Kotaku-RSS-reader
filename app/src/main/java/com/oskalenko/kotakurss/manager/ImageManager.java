package com.oskalenko.kotakurss.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.oskalenko.kotakurss.common.FileCache;
import com.oskalenko.kotakurss.common.MemoryCache;
import com.oskalenko.kotakurss.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

public class ImageManager {

    private static final String TAG = ImageManager.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static final int DOWNLOAD_THREADS_COUNT = 5;

    private Map<ImageView, String> mImageViews;
    private FileCache mFileCache;
    private MemoryCache mMemoryCache;
    private ExecutorService mExecutorService;
    private Handler mHandler;

    public ImageManager(Context context) {
        mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        mFileCache = new FileCache(context);
        mMemoryCache = new MemoryCache();
        mExecutorService = Executors.newFixedThreadPool(DOWNLOAD_THREADS_COUNT);
        mHandler = new Handler();
    }

    public void loadImage(@NonNull String url, @NonNull ImageView imageView,
                          @DrawableRes int noPhotoDrawableRes, @NonNull Callback callback) {
        checkNotNull(url);
        checkNotNull(imageView);
        checkNotNull(callback);

        //Store image and url in Map
        mImageViews.put(imageView, url);

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = mMemoryCache.get(url);

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            Log.i(TAG, url + " from cache");
            imageView.setImageBitmap(bitmap);
            callback.onSuccess();
        } else {
            //queue image to download from url
            queueImage(url, imageView, callback, noPhotoDrawableRes);
        }
    }

    //Task for the queue
    private class ImageInfo {

        private String mUrl;
        private ImageView mImageView;
        private Callback mCallback;
        private int mNoPhotoDrawableRes;

        public String getUrl() {
            return mUrl;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public Callback getCallback() {
            return mCallback;
        }

        public int getNoPhotoDrawableRes() {
            return mNoPhotoDrawableRes;
        }

        public ImageInfo(String url, ImageView imageView, Callback callback, @DrawableRes int noPhotoDrawableRes) {
            mUrl = url;
            mImageView = imageView;
            mCallback = callback;
            mNoPhotoDrawableRes = noPhotoDrawableRes;
        }
    }

    private class ImageLoader implements Runnable {

        private ImageInfo mImageInfo;

        public ImageLoader(ImageInfo imageInfo) {
            mImageInfo = imageInfo;
        }

        @Override
        public void run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(mImageInfo))
                    return;
                // download image from web mUrl
                Bitmap bitmap = getBitmap(mImageInfo.getUrl());
                // set image data in Memory Cache
                mMemoryCache.put(mImageInfo.getUrl(), bitmap);

                if (imageViewReused(mImageInfo))
                    return;

                // Get bitmap to display
                DisplayOnUI displayOnUI = new DisplayOnUI(bitmap, mImageInfo);

                // Causes the Runnable displayOnUI (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this mHandler is attached.
                // BitmapDisplayer run method will call
                mHandler.post(displayOnUI);

            } catch (Throwable throwable) {
                mImageInfo.getCallback().onError();
                Log.e(TAG, throwable.getMessage());
            }
        }
    }

    private Bitmap getBitmap(String url) {
        File file = mFileCache.getFile(url);

        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        Bitmap bitmapFromFile = decodeFile(file);
        if (bitmapFromFile != null)
            return bitmapFromFile;

        // Download image file from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setInstanceFollowRedirects(true);
            InputStream inputStream = conn.getInputStream();

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream outputStream = new FileOutputStream(file);

            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            Utils.copyStream(inputStream, outputStream);

            outputStream.close();
            conn.disconnect();

            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(file);

            return bitmap;

        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());

            if (throwable instanceof OutOfMemoryError) {
                Log.i(TAG, "OutOfMemoryError");
                mMemoryCache.clear();
            }

            return null;
        }
    }

    private Bitmap decodeFile(File file) {

        try {
            //Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            FileInputStream stream = new FileInputStream(file);
            BitmapFactory.decodeStream(stream, null, options);
            stream.close();

            //Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            final int requiredSize = 85;

            int width = options.outWidth;
            int height = options.outHeight;
            int scale = 1;

            while (true) {
                if (width / 2 < requiredSize || height / 2 < requiredSize)
                    break;
                width /= 2;
                height /= 2;
                scale *= 2;
            }

            //decode with current scale values
            BitmapFactory.Options optionsResult = new BitmapFactory.Options();
            optionsResult.inSampleSize = scale;
            FileInputStream inputStreamResult = new FileInputStream(file);
            Bitmap resultBitmap = BitmapFactory.decodeStream(inputStreamResult, null, optionsResult);
            inputStreamResult.close();

            return resultBitmap;

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    //Used to display bitmap in the UI thread
    private class DisplayOnUI implements Runnable {

        private Bitmap mBitmap;
        private ImageInfo mImageInfo;

        public DisplayOnUI(Bitmap bitmap, ImageInfo imageInfo) {
            mBitmap = bitmap;
            mImageInfo = imageInfo;
        }

        public void run() {
            if (imageViewReused(mImageInfo))
                return;

            // Show mBitmap on UI
            if (mBitmap != null) {
                mImageInfo.mImageView.setImageBitmap(mBitmap);
                mImageInfo.getCallback().onSuccess();
            } else {
                mImageInfo.getImageView().setImageResource(mImageInfo.getNoPhotoDrawableRes());
                mImageInfo.getCallback().onError();
            }
        }
    }

    private boolean imageViewReused(ImageInfo imageInfo) {
        String url = mImageViews.get(imageInfo.mImageView);
        //Check mUrl is already exist in mImageViews MAP
        if (url == null || !url.equals(imageInfo.mUrl))
            return true;
        return false;
    }

    private void queueImage(String url, ImageView imageView, Callback callback, @DrawableRes int noPhotoDrawableRes) {
        // Store image and mUrl in ImageInfo object
        ImageInfo imageInfo = new ImageInfo(url, imageView, callback, noPhotoDrawableRes);

        // pass ImageInfo object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        mExecutorService.submit(new ImageLoader(imageInfo));
    }

    public interface Callback {
        void onSuccess();

        void onError();

        public static class EmptyCallback implements Callback {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        }
    }
}
