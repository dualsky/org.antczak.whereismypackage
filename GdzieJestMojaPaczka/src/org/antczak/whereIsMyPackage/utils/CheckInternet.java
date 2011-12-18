package org.antczak.whereIsMyPackage.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
	/**
	 * Sprzwdzenie, czy siec jest dostêpna
	 * 
	 * @param ctx
	 * @return boolean
	 */
	/*
	 * public static boolean haveInternet(Context ctx) { NetworkInfo info =
	 * (NetworkInfo) ((ConnectivityManager) ctx
	 * .getSystemService(Context.CONNECTIVITY_SERVICE)) .getActiveNetworkInfo();
	 * if (info == null || !info.isConnected()) { return false; } if
	 * (info.isRoaming()) { return true; } return true; }
	 */

	public static boolean haveAnyConnection(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.isConnected())
				return true;
		}
		return false;
	}

	public static boolean haveWiFiConnection(Context ctx) {
		boolean haveConnectedWifi = false;

		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
		}
		return haveConnectedWifi;
	}

}
