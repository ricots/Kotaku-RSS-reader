package com.oskalenko.kotakurss.common;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {

    private static final String TAG = MemoryCache.class.getSimpleName();

    //Last argument true for LRU ordering
    private Map<String, Bitmap> mCache = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    //current allocated mSize
    private long mSize = 0;

    //max memory mCache folder used to download images in bytes
    private long mLimit = 1000000L;

    public MemoryCache() {
        //use 25% of available heap mSize
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setLimit(long limit) {
        mLimit = limit;
        Log.i(TAG, "MemoryCache will use up to " + mLimit / 1024. / 1024. + "MB");
    }

    public Bitmap get(String id) {
        try {
            if (!mCache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return mCache.get(id);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (mCache.containsKey(id))
                mSize -= getSizeInBytes(mCache.get(id));
            mCache.put(id, bitmap);
            mSize += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void checkSize() {
        Log.i(TAG, "mCache mSize=" + mSize + " mLimit=" + mLimit + " length=" + mCache.size());
        if (mSize > mLimit) {
            Log.i(TAG, "mSize > mLimit " + mCache.size());
            Iterator<Map.Entry<String, Bitmap>> iterator = mCache.entrySet().iterator();//least recently accessed item will be the first one iterated

            while (iterator.hasNext()) {
                Map.Entry<String, Bitmap> entry = iterator.next();
                mSize -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if (mSize <= mLimit)
                    break;
            }

            Log.i(TAG, "Clean mCache. New mSize " + mCache.size());
        }
    }

    public void clear() {
        try {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            mCache.clear();
            mSize = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
