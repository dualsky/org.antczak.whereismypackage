package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.utils.CheckInternet;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MainApplication extends org.holoeverywhere.app.Application {

	private static final String TAG = "org.antczak.whereIsMyPackage.Application";

	private History history;
	private GoogleAnalyticsTracker tracker;
	private android.content.SharedPreferences prefs;
	private Editor prefsEditor;
	
	private String appVersion;
	private boolean isTablet;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		
		history = new History(this);
		prefs = getDefaultSharedPreferences();
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(getString(R.string.GA), this);
		tracker.trackEvent("Debug", "Version", appVersion, 0);
		tracker.trackEvent("Debug", "Device", android.os.Build.MODEL, 0);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					"org.antczak.whereIsMyPackage",
					PackageManager.GET_META_DATA);
			if (pInfo != null)
				appVersion = pInfo.versionName;
			Log.d(TAG, "Version reading. OK. Value: " + appVersion);
		} catch (NameNotFoundException e) {
			appVersion = "-1";
			Log.d(TAG, "Version reading. Error. Msg:" + e.getMessage());
		}
		
		isTablet = getResources().getBoolean(R.bool.isTablet);
		Log.d(TAG, "isTablet:" + isTablet);
		
		prefsEditor = prefs.edit();
		prefsEditor.putBoolean("isTablet", isTablet);

		if (!prefs.getString("appVersion", "0").equals(appVersion)) {
			prefsEditor.putString("appVersion", appVersion);
			prefsEditor.putBoolean("showWhatsNew", true);
			prefsEditor.putString("data", null);
			
		}
		prefsEditor.commit();
		
		
		
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate");

	}

	public void trackerDispach() {
		Log.d(TAG, "trackerDispach");
		if (tracker != null && CheckInternet.haveAnyConnection(this)) {
			tracker.dispatch();
		}
	}
	
	public History getHistory() {
		Log.d(TAG, "getHistory");
		return history;
	}
	
}
