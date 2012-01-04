package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.service.MonitorService;
import org.antczak.whereIsMyPackage.utils.CheckPackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class Preferences extends PreferenceActivity {

	private static final String TAG = "Preferences";

	PendingIntent monitorService;
	PreferenceScreen preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences_layout);
		
		monitorService = PendingIntent.getService(this, 0, new Intent(this,
				MonitorService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(
						onSharedPreferenceChangedListiner);

		preferences = this.getPreferenceScreen();
		
		Preference tmpPreference;
		if (CheckPackage.isInstalled(this, "com.google.zxing.client.android"))
			tmpPreference = findPreference("barcodeScanner");
		else
			tmpPreference = findPreference("autoCheck");
		PreferenceCategory tmpCategory = (PreferenceCategory) findPreference("scanning");
		tmpCategory.removePreference(tmpPreference);
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
			} else if (key.equals("savedCourier")) {
				if (!prefs.getBoolean("savedCourier", true)) {
					SharedPreferences.Editor prefsEditor = prefs.edit();
					prefsEditor.putBoolean("autoCheck", false);
					prefsEditor.commit();
					CheckBoxPreference autoCheck = (CheckBoxPreference) preferences
							.findPreference("autoCheck");
					autoCheck.setChecked(false);
				}
			}
		}
	};
}
