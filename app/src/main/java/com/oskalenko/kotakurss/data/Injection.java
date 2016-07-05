package com.oskalenko.kotakurss.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.oskalenko.kotakurss.data.local.FeedsLocalDataSource;
import com.oskalenko.kotakurss.data.remote.FeedsRemoteDataSource;

import static com.oskalenko.kotakurss.common.Utils.checkNotNull;

/**
 * Enables injection of production implementations for
 * {@link FeedsDataSource} at compile time.
 */
public class Injection {

    public static FeedsRepository provideTasksRepository(Context context){
        return FeedsRepository.getInstance(provideRemoteDataSource(), provideLocalDataSource(context));
    }

    public static FeedsDataSource provideRemoteDataSource() {
        return FeedsRemoteDataSource.getInstance();
    }

    public static FeedsLocalDataSource provideLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        return FeedsLocalDataSource.getInstance(context.getContentResolver());
    }
}
