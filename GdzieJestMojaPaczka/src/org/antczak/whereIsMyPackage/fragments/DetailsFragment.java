package org.antczak.whereIsMyPackage.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antczak.whereIsMyPackage.History;
import org.antczak.whereIsMyPackage.R;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DetailsFragment extends Fragment {

	private static final String TAG = "DetailsFragment";

	List<Map<String, String>> groupData;
	boolean currentSortAsc;
	boolean currentMonitoringStatus;

	History history;
	String packageNumber;
	String isMonitorable;
	Bundle bundle;
	boolean isTablet = true;
	SharedPreferences prefs;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setHasOptionsMenu(true);

		bundle = getArguments();

		isMonitorable = bundle.getString("isMonitorable");
		packageNumber = bundle.getString("packageNumber");

		prefs = PreferenceManager.getDefaultSharedPreferences(this
				.getActivity());

		isTablet = prefs.getBoolean("isTablet", false);
		Log.d(TAG, "isMonitorable:" + isMonitorable);
		Log.d(TAG, "packageNumber:" + packageNumber);
		Log.d(TAG, "isTablet:" + isTablet);

		groupData = new ArrayList<Map<String, String>>();
		Map<String, String> group;
		JSONObject row;
		try {
			JSONObject history = new JSONObject(
					bundle.getString("packageDetails"));
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

		currentSortAsc = Boolean.parseBoolean(prefs
				.getString("sorting", "true"));
		currentMonitoringStatus = bundle.getString("monitor").equals("1") ? true
				: false;
		this.getSupportActivity().invalidateOptionsMenu();
	}

	@Override
	public View onCreateView(org.holoeverywhere.LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_details, container, false);
		((TextView) v.findViewById(R.id.textViewDetails1)).setText(bundle
				.getCharSequence("packageNumber"));
		ImageView im = (ImageView) v.findViewById(R.id.imageViewDetails1);
		im.setImageDrawable(getResources().getDrawable(
				getResources().getIdentifier(
						"courier_" + bundle.getString("courierCode"),
						"drawable", "org.antczak.whereIsMyPackage")));

		((TextView) v.findViewById(R.id.textViewDetails1))
				.setText(packageNumber);
		((TextView) v.findViewById(R.id.textViewDetails2)).setText(bundle
				.getString("courierName"));

		lv = (ListView) v.findViewById(R.id.listViewDetails1);
		if (!currentSortAsc)
			Collections.reverse(groupData);
		fillList(groupData);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.d(TAG, "onCreateOptionsMenu");
		if (bundle != null) {
			if (isTablet)
				inflater.inflate(R.menu.activity_details_tablet, menu);
			else
				inflater.inflate(R.menu.activity_details, menu);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Log.d(TAG, "onPrepareOptionsMenu");
		if (bundle != null) {
			if (isTablet) {
				if (currentSortAsc) {
					menu.getItem(6).setVisible(true);
					menu.getItem(7).setVisible(false);
				} else {
					menu.getItem(6).setVisible(false);
					menu.getItem(7).setVisible(true);
				}
			} else {

				if (currentSortAsc) {
					menu.getItem(2).setVisible(true);
					menu.getItem(3).setVisible(false);
				} else {
					menu.getItem(2).setVisible(false);
					menu.getItem(3).setVisible(true);
				}
				if (isMonitorable.equals("1")) { // TODO dograc opcje menu

					if (currentMonitoringStatus) {
						menu.getItem(0).setVisible(false);
						menu.getItem(1).setVisible(true);
					} else {
						menu.getItem(0).setVisible(true);
						menu.getItem(1).setVisible(false);
					}

				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
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
		SimpleAdapter sca = new SimpleAdapter(this.getActivity(), data,
				R.layout.simple_list_item_package_history, new String[] {
						"date", "city", "desc" }, new int[] {
						R.id.textViewHistory1, R.id.textViewHistory2,
						R.id.textViewHistory3 });
		lv.setAdapter(sca);
	}

	public void setHistory(History history) {
		this.history = history;
	}

}
