package org.antczak.whereIsMyPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

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

public class Main extends Activity {

	
/*
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


	
*/
}