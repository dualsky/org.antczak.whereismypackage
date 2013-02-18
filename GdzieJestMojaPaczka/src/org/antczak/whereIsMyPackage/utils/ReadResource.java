package org.antczak.whereIsMyPackage.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ReadResource {
	
	private static final String TAG = "ReadResource";

	public static String getString(Context context, int resourceId) {
		try {
			Resources res = context.getResources();
			InputStream inputStream = res.openRawResource(resourceId);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			return new String(b);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
}
