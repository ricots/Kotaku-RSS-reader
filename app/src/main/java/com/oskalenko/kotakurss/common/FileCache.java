package com.oskalenko.kotakurss.common;

import android.content.Context;

import java.io.File;

import static android.os.Environment.*;
import static android.os.Environment.getExternalStorageState;

public class FileCache {

    private File mCacheDir;

    public FileCache(Context context) {

        //Find the dir at SDCARD to save cached images

        if (getExternalStorageState().equals(MEDIA_MOUNTED)) {
            //if SDCARD is mounted (SDCARD is present on device and mounted)
            mCacheDir = new File(getExternalStorageDirectory(), "LazyList");
        } else {
            // if checking on simulator the create cache dir in your application context
            mCacheDir = context.getCacheDir();
        }

        if (!mCacheDir.exists()) {
            // create cache dir in your application context
            mCacheDir.mkdirs();
        }
    }

    public File getFile(String url) {
        //Identify images by hashcode or encode by URLEncoder.encode.
        String filename = String.valueOf(url.hashCode());
        File file = new File(mCacheDir, filename);
        return file;
    }

    public void clear() {
        // list all files inside cache directory
        File[] files = mCacheDir.listFiles();
        if (files == null)
            return;
        //delete all cache directory files
        for (File file : files)
            file.delete();
    }
}
