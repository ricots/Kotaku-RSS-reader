package com.oskalenko.kotakurss.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.oskalenko.kotakurss.data.FeedsProvider;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 20:51
 */

public class PrefManager {

    private static final String SORT_ORDER_KEY = "sort_order_key";

    private SharedPreferences mSharedPreferences;

    public PrefManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSortOrder(String sortOrder) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SORT_ORDER_KEY, sortOrder);
        editor.commit();
    }

    public String getSortOrder() {
        return mSharedPreferences.getString(SORT_ORDER_KEY, FeedsProvider.SORT_ASCENDING);
    }
}
