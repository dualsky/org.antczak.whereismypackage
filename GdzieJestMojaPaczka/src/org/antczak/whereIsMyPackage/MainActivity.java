package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.fragments.MainFragment;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlidingMenu;
import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ListView;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.view.Window;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	android.content.SharedPreferences prefs;
	Editor prefsEditor;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		getSupportActionBar().setIcon(R.drawable.ic_launcher2);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
		if (prefs.getBoolean("showWhatsNew", false)) {
			whatsNew();
			prefsEditor.putBoolean("showWhatsNew", false);
			prefsEditor.commit();
		}

		final AddonSlidingMenuA addonSM = requireSlidingMenu();
		final SlidingMenu sm = addonSM.getSlidingMenu();
		View v = getLayoutInflater().inflate(R.layout.menu);

		ListView list = (ListView) v.findViewById(R.id.list);
		String[] items = { "this", "is", "a", "really", "silly", "list" };
		list.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, items));

		// mHasSlidingMenu = true;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addonSM.setBehindContentView(v);
		addonSM.setSlidingActionBarEnabled(true);
		sm.setBehindWidth(350);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setSlidingEnabled(true);
		////////////////
		/////////////
		////////////////////
		////////////////////
		//////////////////
		


	}

	public AddonSlidingMenuA requireSlidingMenu() {
		return requireAddon(AddonSlidingMenu.class).activity(this);
	}

	@Override
	protected Holo onCreateConfig(Bundle savedInstanceState) {
		Holo config = super.onCreateConfig(savedInstanceState);
		config.requireSlidingMenu = true;
		return config;
	}









	
	
	// ////////////////////////////////////////////////////////////////////////////////
	// Menu
	// ////////////////////////////////////////////////////////////////////////////////
/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v(TAG, "Menu: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.menuDonate:
			Intent launchDonateIntent = new Intent().setClass(this,
					Donate.class);
			startActivityForResult(launchDonateIntent, 1);
			return true;
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
			tv.setText(getString(R.string.app_name) + " v" + 1); //TODO appVer z prefs
			return true;
		case R.id.menuRate:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse("market://details?id=org.antczak.whereIsMyPackage"));
			startActivity(intent);
			return true;
		case android.R.id.home:
			// if (mStaticSlidingMenu &&
			// getSupportFragmentManager().getBackStackEntryCount() == 0) {
			requireSlidingMenu().toggle();
			// } else {
			// onBackPressed();
			// }
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

*/
	
	
	
	////////////////////////////
	////////////////////////////
	/////////////////////////////
	/////////////////////////////
	/////////////////////////////
	/////////////////////////////
	
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MainFragment fragment = new MainFragment();
		fragment.setArguments(getIntent().getExtras());
		//fragment.setHistory(history);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.mainFragmentPhone, fragment);
		fragmentTransaction.commit();
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
}
