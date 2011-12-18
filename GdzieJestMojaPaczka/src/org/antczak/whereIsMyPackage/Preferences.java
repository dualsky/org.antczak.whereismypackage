package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.service.MonitorService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences extends PreferenceActivity {

	private static final String TAG = "Preferences";

	PendingIntent monitorService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		monitorService = PendingIntent.getService(this, 0, new Intent(this,
				MonitorService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(
						onSharedPreferenceChangedListiner);
		addPreferencesFromResource(R.xml.preferences);
	}

	private OnSharedPreferenceChangeListener onSharedPreferenceChangedListiner = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			if (key.equals("frequency")) {
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(monitorService);
				am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock
						.elapsedRealtime(), 60 * 1000 * Integer.parseInt(prefs
						.getString("frequency", "0")), monitorService);
				Log.v(TAG,
						"AlarmManager changed, new period: "
								+ prefs.getString("frequency", "n/a"));
			}
		}
	};
}
