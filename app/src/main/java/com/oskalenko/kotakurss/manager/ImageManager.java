package com.oskalenko.kotakurss.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.widget.ImageView;

import com.oskalenko.kotakurss.R;
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

public class ImageManager {

    private static final String TAG = ImageManager.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static final int DOWNLOAD_THREADS_COUNT = 5;

    private Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private FileCache mFileCache;
    private MemoryCache mMemoryCache = new MemoryCache();
    private ExecutorService mExecutorService;
    private Handler mHandler = new Handler();

    public ImageManager(Context context) {
        mFileCache = new FileCache(context);
        mExecutorService = Executors.newFixedThreadPool(DOWNLOAD_THREADS_COUNT);
    }

    public void loadImage(String url, ImageView imageView, @DrawableRes int noPhotoDrawableRes, Callback callback) {
        //Store image and mUrl in Map
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
            //queue Photo to download from mUrl
            queuePhoto(url, imageView, callback, noPhotoDrawableRes);
        }
    }

    //Task for the queue
    private class PhotoToLoad {

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

        public PhotoToLoad(String url, ImageView imageView, Callback callback, @DrawableRes int noPhotoDrawableRes) {
            mUrl = url;
            mImageView = imageView;
            mCallback = callback;
            mNoPhotoDrawableRes = noPhotoDrawableRes;
        }
    }

    private class PhotosLoader implements Runnable {

        private PhotoToLoad mPhotoToLoad;

        public PhotosLoader(PhotoToLoad photoToLoad) {
            mPhotoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(mPhotoToLoad))
                    return;
                // download image from web mUrl
                Bitmap bitmap = getBitmap(mPhotoToLoad.getUrl());

                // set image data in Memory Cache
                mMemoryCache.put(mPhotoToLoad.getUrl(), bitmap);

                if (imageViewReused(mPhotoToLoad))
                    return;

                // Get mBitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bitmap, mPhotoToLoad);

                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this mHandler is attached.
                // BitmapDisplayer run method will call
                mHandler.post(bd);

            } catch (Throwable throwable) {
                mPhotoToLoad.getCallback().onError();
                Log.e(TAG, throwable.getMessage());
            }
        }
    }

    private Bitmap getBitmap(String url) {
        File f = mFileCache.getFile(url);

        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

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
            OutputStream outputStream = new FileOutputStream(f);

            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            Utils.copyStream(inputStream, outputStream);

            outputStream.close();
            conn.disconnect();

            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(f);

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

    private Bitmap decodeFile(File f) {

        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream = new FileInputStream(f);
            BitmapFactory.decodeStream(stream, null, o);
            stream.close();

            //Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            final int REQUIRED_SIZE = 85;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    //Used to display mBitmap in the UI thread
    private class BitmapDisplayer implements Runnable {

        private Bitmap mBitmap;
        private PhotoToLoad mPhotoToLoad;

        public BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad) {
            mBitmap = bitmap;
            mPhotoToLoad = photoToLoad;
        }

        public void run() {
            if (imageViewReused(mPhotoToLoad))
                return;

            // Show mBitmap on UI
            if (mBitmap != null) {
                mPhotoToLoad.mImageView.setImageBitmap(mBitmap);
                mPhotoToLoad.getCallback().onSuccess();
            } else {
                mPhotoToLoad.getImageView().setImageResource(mPhotoToLoad.getNoPhotoDrawableRes());
                mPhotoToLoad.getCallback().onError();
            }
        }
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = mImageViews.get(photoToLoad.mImageView);
        //Check mUrl is already exist in mImageViews MAP
        if (tag == null || !tag.equals(photoToLoad.mUrl))
            return true;
        return false;
    }

    private void queuePhoto(String url, ImageView imageView, Callback callback, @DrawableRes int noPhotoDrawableRes) {
        Log.i(TAG, "queuePhoto " + url);
        // Store image and mUrl in PhotoToLoad object
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView, callback, noPhotoDrawableRes);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        mExecutorService.submit(new PhotosLoader(photoToLoad));
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
