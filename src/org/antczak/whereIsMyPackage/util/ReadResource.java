
package org.antczak.whereIsMyPackage.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ReadResource {
    private static final String TAG = "ReadResource";

    public static String getDrawable(Context context, int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(
                    resourceId);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            return new String(b);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static String getString(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    public static String[] getStringArray(Context context, int resourceId) {
        return context.getResources().getStringArray(resourceId);
    }
}
