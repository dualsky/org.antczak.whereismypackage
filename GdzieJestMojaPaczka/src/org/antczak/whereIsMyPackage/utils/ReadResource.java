package org.antczak.whereIsMyPackage.utils;

import java.io.IOException;
import java.io.InputStream;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.util.Log;

public class ReadResource {

	private static final String TAG = "ReadResource";

	public static String getString(Context context, int resourceId) {
		try {
			InputStream inputStream = context.getResources().openRawResource(
					resourceId);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			return new String(b);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	public static String[] getStringArray(Context context, int resourceId) {
		return context.getResources().getStringArray(R.array.tags);
	}
}
