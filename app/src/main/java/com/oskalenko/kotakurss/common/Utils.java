package com.oskalenko.kotakurss.common;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 05.07.2016
 * Time: 9:28
 */
public class Utils {

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) {

        final int bufferSize = 1024;

        try {
            byte[] bytes = new byte[bufferSize];

            for(;;) {
                int count = inputStream.read(bytes, 0, bufferSize);

                if(count == -1)
                    break;

                outputStream.write(bytes, 0, count);
            }
        } catch(Exception ex){
            Log.e("CopyStream", ex.getMessage());
        }
    }
}
