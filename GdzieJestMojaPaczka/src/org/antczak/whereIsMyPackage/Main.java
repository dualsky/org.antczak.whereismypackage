package org.antczak.whereIsMyPackage;

import java.util.ArrayList;
import java.util.Arrays;

import org.antczak.whereIsMyPackage.adpaters.ArrayAdapterCourierOrder;
import org.antczak.whereIsMyPackage.adpaters.ArrayAdapterMenu;
import org.antczak.whereIsMyPackage.adpaters.SimpleCursorAdapterHistory;
import org.antczak.whereIsMyPackage.service.MonitorService;
import org.antczak.whereIsMyPackage.utils.CheckInternet;
import org.antczak.whereIsMyPackage.utils.CheckPackage;
import org.antczak.whereIsMyPackage.utils.ReadFromURL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Main extends Activity {

	private static final String TAG = "Main";

	ImageButton scanBar;
	ImageButton checkPackage;
	EditText packageNumber;
	Spinner courierNameDropdown;
	Toast info;
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	History history;
	String[] courierNames;
	String[] courierCodes;
	String[] customCuriersOrder;
	int[] courierIsMonitorable;
	ListView historyList;
	Dialog optionsMenu;
	PendingIntent monitorService;
	String appVersion;
	Context ctx;
	GoogleAnalyticsTracker tracker;
	SimpleCursorAdapterHistory sca;
	Cursor searchHistory;
	AsyncTask<String, Integer, JSONObject> checkPackageThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(getString(R.string.GA), this);
		tracker.trackEvent("Debug", "Version", appVersion, 0);
		tracker.trackEvent("Debug", "Device", android.os.Build.MODEL, 0);
		setTitle(getString(R.string.app_name));
		setContentView(R.layout.main);
		ctx = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
		if (!prefs.getString("appVersion", "0").equals(appVersion)) {
			prefsEditor.putString("appVersion", appVersion);
			prefsEditor.putString("data", null);
			prefsEditor.commit();
			whatsNew();
		}

		if (CheckPackage.isInstalled(this, "com.google.zxing.client.android")) {
			scanBar = (ImageButton) findViewById(R.id.button1);
			scanBar.setOnClickListener(scanBarListiner);
			scanBar.setVisibility(0);
		}
		checkPackage = (ImageButton) findViewById(R.id.button2);
		checkPackage.setOnClickListener(checkPackageListiner);

		packageNumber = (EditText) findViewById(R.id.editText1);
		packageNumber.setOnKeyListener(packageNumberListiner);

		courierNameDropdown = (Spinner) findViewById(R.id.spinner1);
		readCustomCuriersOrder();
		
		historyList = (ListView) findViewById(R.id.listView1);
		historyList.setOnItemClickListener(historyListListiner);

		monitorService = PendingIntent.getService(this, 0, new Intent(this,
				MonitorService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");
		// checkPackageThread.suspend();
		if (checkPackageThread != null)
			checkPackageThread.cancel(true);
		this.history.closeConnection();
		this.history = null;
		if (tracker != null && CheckInternet.haveAnyConnection(this)) {
			tracker.dispatch();
			tracker.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (history == null)
			history = new History(this);
		readCustomCuriersOrder();
		refreshHistory();
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(getString(R.string.GA), this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (history != null)
			this.history.closeConnection();
		if (tracker != null && CheckInternet.haveAnyConnection(this)) {
			tracker.dispatch();
			tracker.stop();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Listiners
	// ////////////////////////////////////////////////////////////////////////////////
	public Spinner.OnItemSelectedListener selectCourierListiner = new Spinner.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			prefsEditor.putInt("selectedCourier", getRealCourierId(courierNameDropdown.getSelectedItem().toString()));
			prefsEditor.commit();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			prefsEditor.putInt("selectedCourier", 0);
			prefsEditor.commit();
		}
	};

	public ImageButton.OnClickListener scanBarListiner = new ImageButton.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		}
	};

	public Button.OnClickListener checkPackageListiner = new Button.OnClickListener() {
		public void onClick(View v) {
			if (packageNumber.getText().toString().equals(""))
				showToast(getString(R.string.enter_package_number));
			else {
				checkPackage(
						packageNumber.getText().toString()
								.replaceAll("[^a-zA-Z0-9]", "").toUpperCase(),
						(String) courierNameDropdown.getSelectedItem(),
						courierCodes[getRealCourierId(courierNameDropdown.getSelectedItem().toString())],
						"0");
				packageNumber.setText("");
			}

		}
	};

	public OnItemClickListener historyListListiner = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			searchHistory.moveToPosition(position);
			final String packageNumber = searchHistory.getString(1);
			final String courierName = searchHistory.getString(2);
			final String courierCode = searchHistory.getString(3);
			final boolean isMonitorable = isCourierMonitorable(courierCode);
			final String monitor = searchHistory.getString(4);
			final boolean desc = searchHistory.isNull(5);

			AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
			builder.setTitle(courierName + ": " + packageNumber);
			ListView modeList = new ListView(Main.this);
			// modeList.setCacheColorHint(android.R.color.black);
			// modeList.setBackgroundColor(android.R.color.darker_gray);
			// modeList.setFooterDividersEnabled(true);

			Object[][] objectArray = new Object[][] {
					new Object[] { getString(R.string.dialog_check_package),
							R.drawable.ic_menu_search_full },
					new Object[] {
							monitor.equals("1") ? getString(R.string.dialog_stop_monitoring)
									: getString(R.string.dialog_start_monitoring),
							monitor.equals("1") ? R.drawable.ic_menu_view_delete
									: R.drawable.ic_menu_view, isMonitorable },
					new Object[] {
							desc ? getString(R.string.dialog_add_desc)
									: getString(R.string.dialog_delete_desc),
							desc ? R.drawable.ic_menu_compose
									: R.drawable.ic_menu_compose_delete },
					new Object[] {
							getString(R.string.dialog_delete_from_history),
							R.drawable.ic_menu_delete } };

			ArrayAdapterMenu modeAdapter = new ArrayAdapterMenu(Main.this,
					R.layout.options_menu, R.id.textViewOptionsMenu1,
					objectArray);

			modeList.setAdapter(modeAdapter);
			modeList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					switch (position) {
					case 0:
						checkPackage(packageNumber, courierName, courierCode,
								monitor);
						break;
					case 1:
						if (monitor.equals("0")) {
							history.startMonitoring(packageNumber);
						} else
							history.stopMonitoring(packageNumber);
						int monitoredCount = history.getMonitoredCount();
						tracker.trackEvent("History", "Monitored Count", ""
								+ monitoredCount, 0);
						if (monitoredCount > 0) {
							Log.d(TAG, "Monitoring: " + monitoredCount);
							((AlarmManager) Main.this
									.getSystemService(Context.ALARM_SERVICE))
									.setRepeating(
											AlarmManager.ELAPSED_REALTIME,
											SystemClock.elapsedRealtime(),
											60 * 1000 * Integer.parseInt(prefs
													.getString("frequency", "0")),
											monitorService);
						} else {
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
							am.cancel(monitorService);
							Log.d(TAG, "AlarmManager stop. Nothing to monitor.");
						}
						refreshHistory();
						break;
					case 2:
						if (desc) {
							LayoutInflater factory = LayoutInflater
									.from(Main.this);
							final View textEntryView = factory.inflate(
									R.layout.add_desc, null);
							AlertDialog.Builder builder = new AlertDialog.Builder(
									Main.this)
									.setIcon(R.drawable.ic_menu_compose)
									.setTitle(R.string.dialog_add_desc_title)
									.setView(textEntryView)
									.setPositiveButton(
											R.string.dialog_add_desc_add,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													history.addDesc(
															packageNumber,
															((EditText) textEntryView
																	.findViewById(R.id.desc))
																	.getText()
																	.toString());
													refreshHistory();
												}
											})
									.setNegativeButton(
											R.string.dialog_add_desc_cancel,
											null);
							AlertDialog dialog = builder.create();
							dialog.show();
						} else {
							history.deleteDesc(packageNumber);
							refreshHistory();
						}
						break;
					case 3:
						history.deleteFromHistory(packageNumber);
						refreshHistory();
						break;
					}
					optionsMenu.dismiss();
				}
			});
			builder.setView(modeList);
			// builder.setInverseBackgroundForced(true);
			optionsMenu = builder.create();
			optionsMenu.show();
		}
	};

	public OnKeyListener packageNumberListiner = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == 0) {
				checkPackage.performClick();
				return true;
			}
			return false;
		}
	};

	// ////////////////////////////////////////////////////////////////////////////////
	// Scan
	// ////////////////////////////////////////////////////////////////////////////////
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				packageNumber.setText(contents);
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Engine
	// ////////////////////////////////////////////////////////////////////////////////
	private void refreshHistory() {
		searchHistory = history.getHistory(45);
		if (sca == null) {
			sca = new SimpleCursorAdapterHistory(this,
					R.layout.simple_list_item_search_history, searchHistory,
					new String[] { "packageNumber", "courierName" }, new int[] {
							R.id.textViewList1, R.id.textViewList2 });
		} else {

			sca.changeCursor(searchHistory);
			sca.notifyDataSetChanged();
		}
		if (historyList.getAdapter() == null)
			historyList.setAdapter(sca);
	}

	private void startDetailsIntent(String packageNumber,
			String packageDetails, String courierName, String courierCode,
			String monitor, boolean isMonitorable) {
		Intent i = new Intent(Main.this, Details.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		i.putExtra("packageNumber", packageNumber);
		i.putExtra("packageDetails", packageDetails);
		i.putExtra("courierCode", courierCode);
		i.putExtra("courierName", courierName);
		i.putExtra("monitor", monitor);
		i.putExtra("isMonitorable", isCourierMonitorable(courierCode) == true ? "1" : "0");
		startActivity(i);
	}

	private void checkPackage(String packageNumber, String courierName,
			String courierCode, String monitor) {
		if (CheckInternet.haveAnyConnection(this)) {
			tracker.trackPageView(courierName);
			tracker.trackEvent("History", "Count",
					"" + history.getHistoryCount(), 0);
			checkPackageThread = new GetData().execute(packageNumber,
					courierName, courierCode, monitor);
		} else {
			history.addToHistory(packageNumber, courierName, courierCode, "0");
			history.oneMoreCheck(packageNumber);
			refreshHistory();
			setTitle(getString(R.string.app_name));
			setProgressBarIndeterminateVisibility(false);
			showToast(getString(R.string.no_network));
		}
	}

	private class GetData extends AsyncTask<String, Integer, JSONObject> {

		String[] packageData;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setTitle(getTitle() + " - " + getString(R.string.receiving));
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected JSONObject doInBackground(String... packageData) {
			// History history = new History(Main.this);
			this.packageData = packageData;
			String url = getString(R.string.api_url) + "?packageNumber="
					+ packageData[0] + "&courierCode=" + packageData[2];
			Log.v(TAG, "URL: " + url);
			String result = ReadFromURL.readString(url);
			JSONObject packageDetails = null;
			if (result != null) {
				try {
					JSONObject jsonResult = new JSONObject(result);
					packageDetails = jsonResult;
				} catch (JSONException e) {

				}
			}
			Log.v(TAG, "Result: " + result);
			history.addToHistory(packageData[0], packageData[1],
					packageData[2], packageDetails == null ? "0" : ""
							+ packageDetails.length());
			// history.closeConnection();
			history.oneMoreCheck(packageData[0]);
			return packageDetails;
		}

		@Override
		protected void onPostExecute(JSONObject packageDetails) {
			Log.v(TAG, "onPostExecute");
			if (!isCancelled()) {
				setProgressBarIndeterminateVisibility(false);
				if (packageDetails != null && packageDetails.length() > 0) {
					startDetailsIntent(packageData[0],
							packageDetails.toString(), packageData[1],
							packageData[2], packageData[3], isCourierMonitorable(packageData[2]));
				} else {
					if (packageDetails == null)
						showToast(getString(R.string.receiving_error));
					else {
						showToast(getString(R.string.receiving_no_data));
					}
					refreshHistory();
				}
				setTitle(getString(R.string.app_name));
			}
			super.onPostExecute(packageDetails);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Menu
	// ////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v(TAG, "Menu: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.menuPreferences:
			Intent launchPreferencesIntent = new Intent().setClass(this,
					Preferences.class);
			startActivityForResult(launchPreferencesIntent, 1);
			return true;
		case R.id.menuClearHistory:
			history.clearHistory();
			refreshHistory();
			return true;
		case R.id.menuClearMonitored:
			history.clearMonitored();
			refreshHistory();
			return true;
		case R.id.menuClose:
			finish();
			return true;
		case R.id.menuAbout:
			LayoutInflater factory = LayoutInflater.from(this);
			View textEntryView = factory.inflate(R.layout.about, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(textEntryView)
					.setTitle(R.string.about_dialog_title)
					.setIcon(R.drawable.ic_launcher)
					.setCancelable(true)
					.setPositiveButton(R.string.about_close_button,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			TextView tv = (TextView) alert.findViewById(R.id.textView5);
			tv.setText(getString(R.string.app_name) + " v" + appVersion);
			return true;
		case R.id.menuRate:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse("market://details?id=org.antczak.whereIsMyPackage"));
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Others
	// ////////////////////////////////////////////////////////////////////////////////
	private void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void whatsNew() {
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.whats_new, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(textEntryView)
				.setTitle(R.string.whats_new_title)
				// .setIcon(R.drawable.icon)
				.setCancelable(true)
				.setPositiveButton(R.string.about_close_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean isCourierMonitorable(String courierCode) {
		for (int i = 0; i < courierCodes.length; i++) {
			if (courierCodes[i].equals(courierCode)) {
				Log.v(TAG, "isCourierMonitorable: courierCode: " + courierCode
						+ ", courierIsMonitorable" + courierIsMonitorable[i]);
				return courierIsMonitorable[i] == 1 ? true : false;
			}
		}
		return false;
	}
	
	private void readCustomCuriersOrder() {
		Resources res = getResources();
		courierCodes = res.getStringArray(R.array.curiersCodes);
		courierIsMonitorable = res.getIntArray(R.array.curiersIsMonitorable);
		courierNames = res.getStringArray(R.array.curiers);
		
		ArrayAdapter<String> adapter;
		ArrayList<String> couriersArray;
		String order = prefs.getString("curiersOrder", null);
		if (order == null) {
			couriersArray = new ArrayList<String>(
					Arrays.asList(courierNames));
			customCuriersOrder = courierNames;
		} else {
			try {
				JSONArray items = new JSONArray(order);
				int count = items.length();
				customCuriersOrder = new String[count];
				for (int i = 0; i < count; i++)
					customCuriersOrder[i] = items.getString(i);
				couriersArray = new ArrayList<String>(
						Arrays.asList(customCuriersOrder));
			} catch (JSONException e) {
				couriersArray = new ArrayList<String>(
						Arrays.asList(courierNames));
				customCuriersOrder = courierNames;
			}
		}
		adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_couriers_list, couriersArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		courierNameDropdown.setAdapter(adapter);
		if (prefs.getBoolean("savedCourier", false))
			courierNameDropdown.setSelection(getCustomCourierId(prefs.getInt("selectedCourier", 0)));
		courierNameDropdown.setOnItemSelectedListener(selectCourierListiner);
		
	}
	
	private int getRealCourierId(String courierName) {
		for(int i = 0; i < courierNames.length; i++) {
			if (courierNames[i].equals(courierName)) return i;
		}
		return 0;
	}
	private int getCustomCourierId(int courierId) {
		for(int i = 0; i < customCuriersOrder.length; i++) {
			if (courierNames[courierId].equals(customCuriersOrder[i])) return i;
		}
		return 0;
	}
}