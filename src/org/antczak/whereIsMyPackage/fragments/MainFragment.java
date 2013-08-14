package org.antczak.whereIsMyPackage.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.antczak.whereIsMyPackage.History;
import org.antczak.whereIsMyPackage.MainActivity;
import org.antczak.whereIsMyPackage.MainApplication;
import org.antczak.whereIsMyPackage.R;
import org.antczak.whereIsMyPackage.adpaters.ArrayAdapterMenu;
import org.antczak.whereIsMyPackage.adpaters.SimpleCursorAdapterHistory;
import org.antczak.whereIsMyPackage.service.MonitorService;
import org.antczak.whereIsMyPackage.utils.CheckInternet;
import org.antczak.whereIsMyPackage.utils.CheckPackage;
import org.antczak.whereIsMyPackage.utils.ReadFromURL;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    ImageButton scanBar;
    ImageButton checkPackage;
    EditText packageNumber;
    Spinner courierNameDropdown;
    Toast info;
    android.content.SharedPreferences prefs;
    Editor prefsEditor;
    History history;
    String[] courierNames;
    String[] courierCodes;
    String[] customCuriersOrder;
    int[] courierIsMonitorable;
    ListView historyList;
    Dialog optionsMenu;
    PendingIntent monitorService;
    boolean isDualPane = true;

    SimpleCursorAdapterHistory sca;
    Cursor searchHistory;
    GoogleAnalyticsTracker tracker;

    AsyncTask<String, Integer, JSONObject> checkPackageThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	prefs = MainApplication.getPrefs();
	prefsEditor = prefs.edit();
	isDualPane = prefs.getBoolean("isTablet", false)
		&& (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

	// TODO zbadac
	monitorService = PendingIntent.getService(this.getActivity(), 0,
		new Intent(this.getActivity(), MonitorService.class),
		PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public View onCreateView(org.holoeverywhere.LayoutInflater inflater,
	    ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.fragment_main, container, false);
	// View tv = v.findViewById(R.id.textView1);
	// ((TextView)tv).setText("The TextView saves and restores this text.");

	// Retrieve the text editor and tell it to save and restore its state.
	// Note that you will often set this in the layout XML, but since
	// we are sharing our layout with the other fragment we will customize
	// it here.
	// ((TextView)v.findViewById(R.id.saved)).setSaveEnabled(true);

	if (CheckPackage.isInstalled(this.getActivity(),
		"com.google.zxing.client.android")) {
	    scanBar = (ImageButton) v.findViewById(R.id.button1);
	    scanBar.setOnClickListener(scanBarListiner);
	    scanBar.setVisibility(0);
	}
	checkPackage = (ImageButton) v.findViewById(R.id.button2);
	checkPackage.setOnClickListener(checkPackageListiner);

	packageNumber = (EditText) v.findViewById(R.id.editText1);
	packageNumber.setOnKeyListener(packageNumberListiner);

	courierNameDropdown = (Spinner) v.findViewById(R.id.spinner1);
	readCustomCuriersOrder();

	historyList = (ListView) v.findViewById(R.id.listView1);
	historyList.setOnItemClickListener(historyListListiner);

	return v;
    }

    @Override
    public void onPause() {
	super.onPause();
	Log.v(TAG, "onPause()");
	// checkPackageThread.suspend();
	if (checkPackageThread != null)
	    checkPackageThread.cancel(true);

    }

    @Override
    public void onResume() {
	super.onResume();
	Log.v(TAG, "onResume()");

	onCustomResume();
    }

    private void onCustomResume() {
	Log.v(TAG, "onCustomResume()");
	if (history == null)
	    history = MainApplication.getHistory();
	if (tracker == null)
	    tracker = ((MainApplication) getActivity().getApplication())
		    .getTracker();
	readCustomCuriersOrder();
	refreshHistory();

    }

    // ////////////////////////////////////////////////////////////////////////////////
    // Scan
    // ////////////////////////////////////////////////////////////////////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	onCustomResume();
	if (requestCode == 0) {
	    this.getActivity();
	    if (resultCode == Activity.RESULT_OK) {
		String contents = intent.getStringExtra("SCAN_RESULT");
		// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
		packageNumber.setText(contents);
		if (prefs.getBoolean("autoCheck", false))
		    checkPackage.performClick();
	    }
	}
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // Engine
    // ////////////////////////////////////////////////////////////////////////////////
    public void refreshHistory() {
	searchHistory = history.getHistory(
		((MainActivity) getActivity()).getSortField(),
		((MainActivity) getActivity()).getSortOrder());
	if (sca == null) {
	    sca = new SimpleCursorAdapterHistory(this.getActivity(),
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

    private void showDetails(String packageNumber, String packageDetails,
	    String courierName, String courierCode, String monitor,
	    String isMonitorable) {

	InputMethodManager inputMethodManager = (InputMethodManager) getSupportActivity()
		.getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(getSupportActivity()
		.getCurrentFocus().getWindowToken(), 0);
	// this.packageNumber.clearFocus();

	Bundle b = new Bundle();
	b.putCharSequence("packageNumber", packageNumber);
	b.putCharSequence("packageDetails", packageDetails);
	b.putCharSequence("courierCode", courierCode);
	b.putCharSequence("courierName", courierName);
	b.putCharSequence("monitor", monitor);
	b.putCharSequence("isMonitorable", isMonitorable);

	DetailsFragment detailsFragment = new DetailsFragment();
	detailsFragment.setArguments(b);
	((MainActivity) getActivity()).setDetailsBundle(b);
	((MainActivity) getActivity()).setDetailsSet(true);
	FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		.beginTransaction();
	if (!isDualPane) {
	    fragmentTransaction.replace(R.id.mainFragment, detailsFragment);
	} else {
	    // TODO optymalizacja

	    fragmentTransaction.replace(R.id.detailsFragment, detailsFragment,
		    detailsFragment.getTag());
	    // fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
	    // android.R.anim.fade_out);
	    // fragmentTransaction.show(newFragment);
	}
	fragmentTransaction.commit();

    }

    private void checkPackage(String packageNumber, String courierName,
	    String courierCode, String monitor) {
	if (CheckInternet.haveAnyConnection(this.getActivity())) {
	    tracker.trackPageView(courierName);
	    checkPackageThread = new GetData().execute(packageNumber,
		    courierName, courierCode, monitor);
	} else {
	    history.addToHistory(packageNumber, courierName, courierCode, "0");

	}
    }

    private class GetData extends AsyncTask<String, Integer, JSONObject> {

	String[] packageData;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    getSupportActivity().setSupportProgressBarIndeterminateVisibility(
		    true);
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

	    return packageDetails;
	}

	@Override
	protected void onPostExecute(JSONObject packageDetails) {
	    Log.v(TAG, "onPostExecute");
	    if (!isCancelled()) {
		getSupportActivity()
			.setSupportProgressBarIndeterminateVisibility(false);
		if (packageDetails != null && packageDetails.length() > 0) {
		    showDetails(packageData[0], packageDetails.toString(),
			    packageData[1], packageData[2], packageData[3],
			    isCourierMonitorable(packageData[2]) == true ? "1"
				    : "0");
		} else {
		    if (packageDetails == null)
			showToast(getString(R.string.receiving_error));
		    else {
			showToast(getString(R.string.receiving_no_data));
		    }
		    if (isDualPane)
			refreshHistory();
		}

	    }
	    super.onPostExecute(packageDetails);
	}
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // Listiners
    // ////////////////////////////////////////////////////////////////////////////////
    public Spinner.OnItemSelectedListener selectCourierListiner = new Spinner.OnItemSelectedListener() {

	@Override
	public void onItemSelected(
		org.holoeverywhere.widget.AdapterView<?> arg0, View arg1,
		int arg2, long arg3) {
	    prefsEditor.putInt("selectedCourier",
		    getRealCourierId(courierNameDropdown.getSelectedItem()
			    .toString()));
	    prefsEditor.commit();

	}

	@Override
	public void onNothingSelected(
		org.holoeverywhere.widget.AdapterView<?> arg0) {
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
				.replaceAll("[^a-zA-Z0-9]", "")
				.toUpperCase(Locale.US),
			(String) courierNameDropdown.getSelectedItem(),
			courierCodes[getRealCourierId(courierNameDropdown
				.getSelectedItem().toString())], "0");
		packageNumber.setText("");
	    }
	}
    };

    public OnItemClickListener historyListListiner = new OnItemClickListener() {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
		long id) {
	    // view.setSelected(true);

	    searchHistory.moveToPosition(position);
	    final String packageNumber = searchHistory.getString(1);
	    final String courierName = searchHistory.getString(2);
	    final String courierCode = searchHistory.getString(3);
	    final boolean isMonitorable = isCourierMonitorable(courierCode);
	    final String monitor = searchHistory.getString(4);
	    final boolean desc = searchHistory.isNull(5);
	    final String descText = desc ? "" : searchHistory.getString(5);

	    AlertDialog.Builder builder = new AlertDialog.Builder(
		    MainFragment.this.getActivity());
	    builder.setTitle(courierName + ": " + packageNumber);
	    ListView modeList = new ListView(MainFragment.this.getActivity());
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
				    : getString(R.string.dialog_modify_desc_long),
			    desc ? R.drawable.ic_menu_compose
				    : R.drawable.ic_menu_compose_delete },
		    new Object[] {
			    getString(R.string.dialog_delete_from_history),
			    R.drawable.ic_menu_delete } };

	    ArrayAdapterMenu modeAdapter = new ArrayAdapterMenu(
		    MainFragment.this.getActivity(), R.layout.options_menu,
		    R.id.textViewOptionsMenu1, objectArray);

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

			if (monitoredCount > 0) {
			    Log.d(TAG, "Monitoring: " + monitoredCount);
			    ((AlarmManager) MainFragment.this.getActivity()
				    .getSystemService(Context.ALARM_SERVICE))
				    .setRepeating(
					    AlarmManager.ELAPSED_REALTIME,
					    SystemClock.elapsedRealtime(),
					    60 * 1000 * Integer.parseInt(prefs
						    .getString("frequency", "0")),
					    monitorService);
			} else {
			    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			    am.cancel(monitorService);
			    Log.d(TAG, "AlarmManager stop. Nothing to monitor.");
			}
			refreshHistory();
			break;
		    case 2:
			LayoutInflater factory = LayoutInflater
				.from(MainFragment.this.getActivity());
			View textEntryView = factory.inflate(R.layout.add_desc,
				null);
			final EditText descView = (EditText) textEntryView
				.findViewById(R.id.desc);
			DialogInterface.OnClickListener addListiner = new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,
				    int whichButton) {
				history.addDesc(packageNumber, descView
					.getText().toString());
				refreshHistory();

			    }
			};
			DialogInterface.OnClickListener delListiner = new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,
				    int whichButton) {
				history.deleteDesc(packageNumber);
				refreshHistory();
			    }
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(
				MainFragment.this.getActivity())
				.setIcon(R.drawable.ic_menu_compose)
				.setTitle(R.string.dialog_add_desc_title)
				.setView(textEntryView)

				.setNegativeButton(
					R.string.dialog_add_desc_cancel, null);
			if (!desc) {
			    descView.setText(descText);
			    builder.setNeutralButton(
				    R.string.dialog_delete_desc, delListiner);
			    builder.setPositiveButton(
				    R.string.dialog_modify_desc, addListiner);

			} else {
			    builder.setPositiveButton(
				    R.string.dialog_add_desc_add, addListiner);
			}
			AlertDialog dialog = builder.create();
			dialog.show();

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

    private void readCustomCuriersOrder() {
	Resources res = getResources();
	courierCodes = res.getStringArray(R.array.curiersCodes);
	courierIsMonitorable = res.getIntArray(R.array.curiersIsMonitorable);
	courierNames = res.getStringArray(R.array.curiers);

	ArrayAdapter<String> adapter;
	ArrayList<String> couriersArray;
	String order = prefs.getString("curiersOrder", null);
	if (order == null) {
	    couriersArray = new ArrayList<String>(Arrays.asList(courierNames));
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
	adapter = new ArrayAdapter<String>(this.getActivity(),
		R.layout.simple_list_item_1, couriersArray);
	adapter.setDropDownViewResource(R.layout.simple_list_item_1);
	courierNameDropdown.setAdapter(adapter);
	courierNameDropdown.setOnItemSelectedListener(selectCourierListiner);
	if (prefs.getBoolean("savedCourier", false))
	    courierNameDropdown.setSelection(
		    getCustomCourierId(prefs.getInt("selectedCourier", 0)),
		    true);

    }

    private int getRealCourierId(String courierName) {
	for (int i = 0; i < courierNames.length; i++) {
	    if (courierNames[i].equals(courierName))
		return i;
	}
	return 0;
    }

    private int getCustomCourierId(int courierId) {
	for (int i = 0; i < customCuriersOrder.length; i++) {
	    if (courierNames[courierId].equals(customCuriersOrder[i]))
		return i;
	}
	return 0;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // Others
    // ////////////////////////////////////////////////////////////////////////////////
    private void showToast(CharSequence msg) {
	Toast.makeText(MainFragment.this.getActivity(), msg, Toast.LENGTH_SHORT)
		.show();
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

}
