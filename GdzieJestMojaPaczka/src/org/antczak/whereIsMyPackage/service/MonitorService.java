package org.antczak.whereIsMyPackage.service;

import org.antczak.whereIsMyPackage.History;
import org.antczak.whereIsMyPackage.R;
import org.antczak.whereIsMyPackage.utils.CheckInternet;
import org.antczak.whereIsMyPackage.utils.ReadFromURL;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class MonitorService extends Service {

	private static final String TAG = "MonitorService";
	NotificationManager nm;
	SharedPreferences sp;
	History history;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		sp = PreferenceManager.getDefaultSharedPreferences(MonitorService.this);
		history = new History(MonitorService.this);
		Log.d(TAG, "onCreate.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onCommandStart. Start id: " + startId);
		boolean net = sp.getBoolean("wifi", false);
		if (net)
			net = CheckInternet.haveWiFiConnection(this);
		else
			net = CheckInternet.haveAnyConnection(this);
		if (net) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Cursor c = history.getMonitored();
						c.moveToFirst();
						if (c.getCount() == 0)
							Log.v(TAG, "Nothing to monitor");
						for (int i = 0; i < c.getCount(); i++) {
							c.moveToPosition(i);
							String url = getString(R.string.api_url) 
									+ "?packageNumber=" + c.getString(1)
									+ "&courierCode=" + c.getString(3)
									+ "&getCount=true";
							Log.v(TAG, "URL: " + url);
							String result = ReadFromURL.readString(url);
							if (result != null) {
								Log.v(TAG, "Result: " + result);
								int qty = Integer.parseInt(result);
								Log.v(TAG, "New lines qty: " + qty
										+ ", old lines qty: " + c.getInt(5));
								if (qty > c.getInt(5)) {
									history.updateRowsCount(c.getString(1), qty);
									url = getString(R.string.api_url)
											+ "?packageNumber="
											+ c.getString(1) + "&courierCode="
											+ c.getString(3);
									Log.v(TAG, "URL: " + url);
									result = ReadFromURL.readString(url);
									JSONObject jsonResult = new JSONObject(
											result);
									Log.v(TAG, "Result: " + result);
									Notification notification = new Notification(
											R.drawable.ic_stat_notify,
											getString(R.string.notification),
											System.currentTimeMillis());
									/*Intent intent = new Intent(
											MonitorService.this, DetailsActivity.class);
									intent.putExtra("packageNumber",
											c.getString(1));
									intent.putExtra("packageDetails", result);
									intent.putExtra("courierCode",
											c.getString(3));
									intent.putExtra("courierName",
											c.getString(2));
									intent.putExtra("monitor", c.getString(4));
									PendingIntent contentIntent = PendingIntent
											.getActivity(MonitorService.this,
													0, intent, 0);
									notification.setLatestEventInfo(
											MonitorService.this, c.getString(2)
													+ ": " + c.getString(1),
											getString(R.string.notification),
											contentIntent);
									nm.notify(R.layout.details, notification);*/ 
									//TODO poprawic activity
								}
							}
						}
					} catch (JSONException e) {
						Log.d(TAG, "JSONException: " + e.getMessage());
					} catch (Exception e) {
						Log.d(TAG, "Exception: " + e.getMessage());
					}
				}
			}).start();
		} else {
			Log.v(TAG, "No network.");
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy.");
		history.closeConnection();
	}
}
