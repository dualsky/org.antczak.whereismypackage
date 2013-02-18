package org.antczak.whereIsMyPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antczak.whereIsMyPackage.adpaters.SlidingMenuAdapter;
import org.antczak.whereIsMyPackage.fragments.DetailsFragment;
import org.antczak.whereIsMyPackage.fragments.MainFragment;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlidingMenu;
import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Switch;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.view.Window;
import com.mobeta.android.dslv.DragSortListView;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	android.content.SharedPreferences prefs;
	Editor prefsEditor;
	boolean isDualPane;
	boolean isDetailsSet = false;
	Bundle detailsBundle;
	Bundle savedState;
	DragSortListView listSlidingMenu;
	SlidingMenuAdapter slidingMenuAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate()");
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

		isDualPane = prefs.getBoolean("isTablet", false)
				&& (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

		addSlidingMenu();
		Log.v(TAG, "getBackStackEntryCount():"
				+ getSupportFragmentManager().getBackStackEntryCount());
		if (savedInstanceState == null) {
			MainFragment fragment = new MainFragment();
			// fragment.setArguments(getIntent().getExtras());
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.mainFragment, fragment).commit();

			Log.v(TAG, "fragmentTransaction:"
					+ getSupportFragmentManager().getBackStackEntryCount());
		} else {

			// FragmentTransaction fragmentTransaction =
			// getSupportFragmentManager().beginTransaction();
			// fragmentTransaction.a
			// fragmentTransaction.add(R.id.mainFragment,
			// getSupportFragmentManager().findFragmentByTag("main"))
			// .commit();
			//
			// getSupportFragmentManager();
			// getSupportFragmentManager().popBackStack("main",
			// FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}

	}

	/*
	 * Sliding menu
	 */

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume()");
		if (isDetailsSet()) {
			DetailsFragment detailsFragment = new DetailsFragment();
			MainFragment mainFragment = new MainFragment();
			detailsFragment.setArguments(getDetailsBundle());
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			if (isDualPane) {
				fragmentTransaction.replace(R.id.detailsFragment,
						detailsFragment);
				fragmentTransaction.replace(R.id.mainFragment, mainFragment);
			} else {
				fragmentTransaction.replace(R.id.mainFragment, detailsFragment)
						.addToBackStack("main");
			}
			fragmentTransaction.commit();
			Log.v(TAG, "onResume getBackStackEntryCount():"
					+ getSupportFragmentManager().getBackStackEntryCount());
		}
	}

	public void addSlidingMenu() {
		final AddonSlidingMenuA addonSlidingMenu = requireSlidingMenu();
		final SlidingMenu slidingMenu = addonSlidingMenu.getSlidingMenu();
		View v = getLayoutInflater().inflate(R.layout.list_slidingmenu);
		// v.setL
		Switch edit = (Switch) v.findViewById(R.id.dragEnabler);
		edit.setOnCheckedChangeListener(onEdit);
		listSlidingMenu = (DragSortListView) v
				.findViewById(R.id.listSlidingMenu);
		// ListView list = (ListView) v.findViewById(R.id.list);
		String[] items = { "this", "is", "a", "really", "silly", "list", "is",
				"a", "really", "silly", "list" };
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, items);
		slidingMenuAdapter = new SlidingMenuAdapter(this,
				R.layout.list_item_slidingmenu, android.R.id.text1, list);
		listSlidingMenu.setAdapter(slidingMenuAdapter);
		// list.setItemChecked(2, false);
		listSlidingMenu.setOnItemClickListener(slidingMenuAdapter);
		listSlidingMenu.setDropListener(onDrop);
		// list.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listSlidingMenu.setItemChecked(0, true);
		// mHasSlidingMenu = true;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addonSlidingMenu.setBehindContentView(v);
		addonSlidingMenu.setSlidingActionBarEnabled(true);
		slidingMenu.setBehindWidth(computeMenuWidth());
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setSlidingEnabled(true);
	}

	public AddonSlidingMenuA requireSlidingMenu() {
		return requireAddon(AddonSlidingMenu.class).activity(this);
	}

	private int computeMenuWidth() {
		int widthPixels = getResources().getDisplayMetrics().widthPixels;
		if (prefs.getBoolean("isTablet", false))
			return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics()));
		else
			return (int) (widthPixels * 0.65);
	}

	private OnCheckedChangeListener onEdit = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			listSlidingMenu.setDragEnabled(isChecked);
		}
	};

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			org.holoeverywhere.widget.Switch s;
			if (from != to) {
				String item = slidingMenuAdapter.getItem(from);
				slidingMenuAdapter.remove(item);
				slidingMenuAdapter.insert(item, to);
				listSlidingMenu.moveCheckState(from, to);
				Log.d("DSLV",
						"Selected item is "
								+ listSlidingMenu.getCheckedItemPosition());
			}
		}
	};

	@Override
	protected Holo onCreateConfig(Bundle savedInstanceState) {
		Log.v(TAG, "onCreateConfig()");
		Holo config = super.onCreateConfig(savedInstanceState);
		config.requireSlidingMenu = true;
		return config;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Menu
	// ////////////////////////////////////////////////////////////////////////////////
	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * item selection Log.v(TAG, "Menu: " + item.getItemId()); switch
	 * (item.getItemId()) { case R.id.menuDonate: Intent launchDonateIntent =
	 * new Intent().setClass(this, Donate.class);
	 * startActivityForResult(launchDonateIntent, 1); return true; case
	 * R.id.menuPreferences: Intent launchPreferencesIntent = new
	 * Intent().setClass(this, Preferences.class);
	 * startActivityForResult(launchPreferencesIntent, 1); return true; case
	 * R.id.menuClearHistory: history.clearHistory(); refreshHistory(); return
	 * true; case R.id.menuClearMonitored: history.clearMonitored();
	 * refreshHistory(); return true; case R.id.menuAbout: LayoutInflater
	 * factory = LayoutInflater.from(this); View textEntryView =
	 * factory.inflate(R.layout.about, null); AlertDialog.Builder builder = new
	 * AlertDialog.Builder(this); builder.setView(textEntryView)
	 * .setTitle(R.string.about_dialog_title) .setIcon(R.drawable.ic_launcher)
	 * .setCancelable(true) .setPositiveButton(R.string.about_close_button, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int id) { dialog.dismiss(); } }); AlertDialog alert =
	 * builder.create(); alert.show(); TextView tv = (TextView)
	 * alert.findViewById(R.id.textView5);
	 * tv.setText(getString(R.string.app_name) + " v" + 1); //TODO appVer z
	 * prefs return true; case R.id.menuRate: Intent intent = new
	 * Intent(Intent.ACTION_VIEW); intent.setData(Uri
	 * .parse("market://details?id=org.antczak.whereIsMyPackage"));
	 * startActivity(intent); return true; case android.R.id.home: // if
	 * (mStaticSlidingMenu && //
	 * getSupportFragmentManager().getBackStackEntryCount() == 0) {
	 * requireSlidingMenu().toggle(); // } else { // onBackPressed(); // }
	 * return true; default: return super.onOptionsItemSelected(item); } }
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater
	 * inflater = getSupportMenuInflater();
	 * inflater.inflate(R.menu.activity_main, menu); return true; }
	 */

	// //////////////////////////
	// //////////////////////////
	// ///////////////////////////
	// ///////////////////////////
	// ///////////////////////////
	// ///////////////////////////

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

	/*
	 * Saved State
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.v(TAG, "onSaveInstanceState()");
		outState.putBoolean("isDetailsSet", isDetailsSet());
		outState.putBundle("detailsBundle", getDetailsBundle());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		setDetailsSet(savedInstanceState.getBoolean("isDetailsSet"));
		setDetailsBundle(savedInstanceState.getBundle("detailsBundle"));
		Log.v(TAG, "onRestoreInstanceState(), isDetailsSet: " + isDetailsSet());
	}

	/*
	 * Getters/Setters
	 */
	public void setDetailsSet(boolean isDetailsSet) {
		this.isDetailsSet = isDetailsSet;
	}

	public boolean isDetailsSet() {
		return isDetailsSet;
	}

	public Bundle getDetailsBundle() {
		return detailsBundle;
	}

	public void setDetailsBundle(Bundle detailsBundle) {
		this.detailsBundle = detailsBundle;
	}

}
