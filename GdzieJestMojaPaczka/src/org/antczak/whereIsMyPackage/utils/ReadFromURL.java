package org.antczak.whereIsMyPackage.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

public final class ReadFromURL {

	private static final String TAG = "ReadFromURL";

	public static String readBuffer(String stringUrl) {
		try {
			URL url = new URL(stringUrl);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String inputLine;
			String output = "";
			while ((inputLine = in.readLine()) != null)
				output += inputLine;
			in.close();
			return output;
		} catch (Exception e) {
			Log.d(TAG, "readBuffer. Error. Msg: " + e.getMessage());
		}
		return null;
	}

	public static String readString(String stringUrl) {
		return ReadFromURL.readBuffer(stringUrl);
	}

	public static int readInt(String stringUrl) {
		return Integer.parseInt(ReadFromURL.readBuffer(stringUrl));
	}

	public static JSONObject readJSON(String stringUrl) {
		try {
			return new JSONObject(ReadFromURL.readBuffer(stringUrl));
		} catch (Exception e) {
		}
		return null;
	}

}
