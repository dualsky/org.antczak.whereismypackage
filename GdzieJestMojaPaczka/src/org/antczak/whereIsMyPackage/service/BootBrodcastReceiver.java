package org.antczak.whereIsMyPackage.service;

import org.antczak.whereIsMyPackage.History;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootBrodcastReceiver extends BroadcastReceiver {

	private static final String TAG = "BootBrodcastReceiver";

	private History history;

	@Override
	public void onReceive(final Context context, final Intent bootintent) {
		Log.d(TAG, "Boot received.");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (prefs.getBoolean("autoStart", false)) {
			Log.d(TAG, "Boot monitor enabled.");
			history = new History();
			int monitoredCount = history.getMonitoredCount();
			history.closeConnection();
			if (monitoredCount > 0) {
				Log.d(TAG, "Monitoring: " + monitoredCount);
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
						.setRepeating(AlarmManager.ELAPSED_REALTIME,
								SystemClock.elapsedRealtime(),
								60 * 1000 * Integer.parseInt(prefs.getString(
										"frequency", "0")),
								// SystemClock.elapsedRealtime(), 30 * 1000 * 1,
								PendingIntent.getService(context, 0,
										new Intent(context,
												MonitorService.class),
										PendingIntent.FLAG_UPDATE_CURRENT));
			} else {
				Log.d(TAG, "Nothing to monitor.");
			}

		} else {
			Log.d(TAG, "Boot monitor disabled.");
		}

	}
}
