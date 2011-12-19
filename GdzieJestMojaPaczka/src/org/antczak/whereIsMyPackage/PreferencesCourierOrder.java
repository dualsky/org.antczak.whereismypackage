package org.antczak.whereIsMyPackage;

import java.util.ArrayList;
import java.util.Arrays;

import org.antczak.whereIsMyPackage.adpaters.ArrayAdapterCourierOrder;
import org.antczak.whereIsMyPackage.views.TouchListView;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class PreferencesCourierOrder extends ListActivity {

	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEditor;
	private Resources res;
	private Context ctx;
	
	private ArrayAdapterCourierOrder adapter = null;
	private static final String TAG = "PreferencesCourierOrder";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.preferences_courier_order_layout);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
		this.res = getResources();
		this.ctx = this;
		TouchListView tlv = (TouchListView) getListView();
		// adapter = new IconicAdapter(new
		// ArrayList<String>(Arrays.asList(items)));
		
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		
		setAdpater(false);
	}

	private TouchListView.DropListener onDrop = new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {

			String item = adapter.getItem(from);
			Log.v(TAG, "from: " + from + ", to: " + to + ", item: " + item);
			adapter.remove(item);
			adapter.insert(item, to);
			saveChanges();
		}
	};

	private TouchListView.RemoveListener onRemove = new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if (adapter.getCount() == 1) 
				Toast.makeText(ctx, getString(R.string.preferences_sorting_last_one), Toast.LENGTH_SHORT).show();
			else {
			adapter.remove(adapter.getItem(which));
			saveChanges(); }
		}
	};

	private void saveChanges() {
		int count = adapter.getCount();
		JSONArray items = new JSONArray();
		for (int i = 0; i < count; i++) {
			items.put(adapter.getItem(i));
		}
		prefsEditor.putString("curiersOrder", items.toString());
		prefsEditor.putInt("selectedCourier", 0);
		prefsEditor.commit();
	}

	private String[] getCuriersOrder(boolean revert) {
		try {
			String order = prefs.getString("curiersOrder", null);
			if (revert || order == null) {
				return res.getStringArray(R.array.curiers);
			} else {
				JSONArray items = new JSONArray(order);
				int count = items.length();
				String[] result = new String[count];
				for (int i = 0; i < count; i++)
					result[i] = items.getString(i);
				return result;
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	private void setAdpater(boolean revert) {
		String[] couriers = getCuriersOrder(revert);
		ArrayList<String> couriersArray = new ArrayList<String>(
				Arrays.asList(couriers));
		adapter = new ArrayAdapterCourierOrder(this, R.id.label, couriersArray);
		setListAdapter(adapter);
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Menu
	// ////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_preferences_courier_order, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v(TAG, "Menu: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.menuRevert:
			setAdpater(true);
			saveChanges();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
