package org.antczak.whereIsMyPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Details extends Activity {

	private static final String TAG = "Details";

	List<Map<String, String>> groupData;
	boolean currentSortAsc;
	boolean currentMonitoringStatus;
	SharedPreferences prefs;
	History history;
	String packageNumber;
	String isMonitorable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(R.layout.details);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.details);

		ImageView im = (ImageView) findViewById(R.id.imageViewDetails1);
		im.setImageDrawable(getResources().getDrawable(
				getResources().getIdentifier(
						"courier_"
								+ getIntent().getExtras().getString(
										"courierCode"), "drawable",
						"org.antczak.whereIsMyPackage")));
		isMonitorable = getIntent().getExtras().getString("isMonitorable");
		packageNumber = getIntent().getExtras().getString("packageNumber");

		((TextView) findViewById(R.id.textViewDetails1)).setText(packageNumber);
		((TextView) findViewById(R.id.textViewDetails2)).setText(getIntent()
				.getExtras().getString("courierName"));

		groupData = new ArrayList<Map<String, String>>();
		Map<String, String> group;
		JSONObject row;
		try {
			JSONObject history = new JSONObject(getIntent().getExtras()
					.getString("packageDetails"));
			for (int i = 0; i < history.length(); i++) {
				group = new HashMap<String, String>();
				row = history.getJSONObject(i + "");
				group.put("date",
						row.getString("date") + " " + row.getString("hour"));
				group.put("city", row.getString("city"));
				group.put("desc", row.getString("desc"));
				groupData.add(group);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		currentSortAsc = Boolean.parseBoolean(prefs
				.getString("sorting", "true"));
		currentMonitoringStatus = getIntent().getExtras().getString("monitor")
				.equals("1") ? true : false;
		if (!currentSortAsc)
			Collections.reverse(groupData);
		fillList(groupData);
		history = new History(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.history.closeConnection();
		this.history = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (history == null)
			history = new History(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_details, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currentSortAsc) {
			menu.getItem(2).setVisible(true);
			menu.getItem(3).setVisible(false);
		} else {
			menu.getItem(2).setVisible(false);
			menu.getItem(3).setVisible(true);
		}
		if (isMonitorable.equals("1")) {
			if (currentMonitoringStatus) {
				menu.getItem(0).setVisible(false);
				menu.getItem(1).setVisible(true);
			} else {
				menu.getItem(0).setVisible(true);
				menu.getItem(1).setVisible(false);
			}
		} else {
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menuOrderDesc:
			Collections.reverse(groupData);
			fillList(groupData);
			currentSortAsc = false;
			return true;
		case R.id.menuOrderAsc:
			Collections.reverse(groupData);
			fillList(groupData);
			currentSortAsc = true;
			return true;
		case R.id.menuStartMonitoring:
			history.startMonitoring(packageNumber);
			currentMonitoringStatus = true;
			return true;
		case R.id.menuStopMonitoring:
			history.stopMonitoring(packageNumber);
			currentMonitoringStatus = false;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void fillList(List<Map<String, String>> data) {
		SimpleAdapter sca = new SimpleAdapter(this, data,
				R.layout.simple_list_item_package_history, new String[] {
						"date", "city", "desc" }, new int[] {
						R.id.textViewHistory1, R.id.textViewHistory2,
						R.id.textViewHistory3 });
		ListView lv = (ListView) findViewById(R.id.listViewDetails1);
		lv.setAdapter(sca);
	}

}
