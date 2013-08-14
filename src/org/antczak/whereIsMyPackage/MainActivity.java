package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.adpaters.SlidingMenuAdapter;
import org.antczak.whereIsMyPackage.fragments.DetailsFragment;
import org.antczak.whereIsMyPackage.fragments.MainFragment;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlidingMenu;
import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.Switch;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.actionbarsherlock.view.Window;
import com.mobeta.android.dslv.DragSortListView;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity implements OnItemSelectedListener {

    private static final String TAG = "org.antczak.whereIsMyPackage.MainActivity";

    android.content.SharedPreferences prefs;
    Editor prefsEditor;
    boolean isDualPane;
    boolean isDetailsSet = false;
    Bundle detailsBundle;
    Bundle savedState;
    DragSortListView listSlidingMenu;
    SlidingMenuAdapter slidingMenuAdapter;
    LinearLayout addTagButton;
    Cursor folders;

    int sortField;
    int sortOrder;

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
	addSortList();
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

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // List Navigation
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private void addSortList() {

	// ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(
	// ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(
	// getSupportActionBar().getThemedContext(), ,
	// R.layout.sherlock_spinner_item);

	ArrayAdapter<CharSequence> sortFieldAdapter = ArrayAdapter
		.createFromResource(getSupportActionBar().getThemedContext(),
			R.array.sortFields, R.layout.list_navigation_item);
	ArrayAdapter<CharSequence> sortOrderAdapter = ArrayAdapter
		.createFromResource(getSupportActionBar().getThemedContext(),
			R.array.sortOrders, R.layout.list_navigation_item);
	// list2.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

	// getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	// getSupportActionBar().setListNavigationCallbacks(list, this);

	View list = LayoutInflater.from(this).inflate(R.layout.list_navigation,
		null);
	IcsSpinner sortFieldSpinner = (IcsSpinner) list
		.findViewById(R.id.sortField);
	IcsSpinner sortOrderSpinner = (IcsSpinner) list
		.findViewById(R.id.sortOrder);
	sortFieldSpinner.setAdapter(sortFieldAdapter);
	sortOrderSpinner.setAdapter(sortOrderAdapter);

	sortFieldSpinner.setOnItemSelectedListener(this);
	sortOrderSpinner.setOnItemSelectedListener(this);
	// Attach to the action bar
	getSupportActionBar().setCustomView(list);
	getSupportActionBar().setDisplayShowCustomEnabled(true);

	// getSupportActionBar().setSe
    }

    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view,
	    int position, long id) {
	switch (parent.getId()) {
	case R.id.sortField:
	    sortField = position;
	    break;
	case R.id.sortOrder:
	    sortOrder = position;
	    break;
	}
	MainFragment fragment = (MainFragment) getSupportFragmentManager()
		.findFragmentById(R.id.mainFragment);
	fragment.refreshHistory();
    }

    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
	// TODO Auto-generated method stub

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // End List Navigation
    // /////////////////////////////////////////////////////////////////////////////////////////////

    // @Override
    /*
     * public boolean onNavigationItemSelected(int itemPosition, long itemId) {
     * Log.v(TAG, "onNavigationItemSelected() " + itemPosition); if
     * (itemPosition == list.getSelected()) list.setAsc(!list.isAsc()); else {
     * list.setSelected(itemPosition); list.setAsc(true); } return true; }
     */
    private void addSlidingMenu() {
	AddonSlidingMenuA addonSlidingMenu = requireSlidingMenu();
	SlidingMenu slidingMenu = addonSlidingMenu.getSlidingMenu();

	View v = findViewById(R.id.mainTags);
	if (v == null) {
	    v = getLayoutInflater().inflate(R.layout.list_slidingmenu);
	}
	Switch editSwitch = (Switch) v.findViewById(R.id.dragEnabler);
	editSwitch.setOnCheckedChangeListener(onEdit);
	addTagButton = (LinearLayout) v.findViewById(R.id.tagAdd);
	addTagButton.setOnClickListener(onAdd);
	listSlidingMenu = (DragSortListView) v
		.findViewById(R.id.listSlidingMenu);
	folders = MainApplication.getHistory().getTags();
	folders.moveToFirst();
	slidingMenuAdapter = new SlidingMenuAdapter(this,
		R.layout.list_slidingmenu_item, folders, new String[] { "NAME",
			"_id" }, new int[] { android.R.id.text1,
			android.R.id.text2 }, CursorAdapter.NO_SELECTION);
	listSlidingMenu.setAdapter(slidingMenuAdapter);
	// list.setItemChecked(2, false);
	listSlidingMenu.setOnItemClickListener(slidingMenuAdapter);
	listSlidingMenu.setDropListener(onDrop);
	// list.setChoiceMode(ListView.CHOICE_MODE_NONE);
	listSlidingMenu.setItemChecked(0, true);
	if (isDualPane) {
	    // FrameLayout ff = (FrameLayout) findViewById(R.id.mainTags);
	    // ff.addView(v);
	    addonSlidingMenu.setBehindContentView(getLayoutInflater().inflate(
		    R.layout.fragment_details)); // dummy view
	    slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	    slidingMenu.setSlidingEnabled(!isDualPane);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(!isDualPane);
	} else {
	    addonSlidingMenu.setBehindContentView(v);
	    addonSlidingMenu.setSlidingActionBarEnabled(true);
	    slidingMenu.setBehindWidth(computeMenuWidth());
	    slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	    slidingMenu.setSlidingEnabled(!isDualPane);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(!isDualPane);
	}
    }

    public AddonSlidingMenuA requireSlidingMenu() {
	return requireAddon(AddonSlidingMenu.class).activity(this);
    }

    private int computeMenuWidth() {
	int widthPixels = getResources().getDisplayMetrics().widthPixels;
	if (prefs.getBoolean("isTablet", false))
	    return (int) (TypedValue.applyDimension(
		    TypedValue.COMPLEX_UNIT_DIP, 300, getResources()
			    .getDisplayMetrics()));
	else
	    return (int) (widthPixels * 0.65);
    }

    private OnCheckedChangeListener onEdit = new OnCheckedChangeListener() {

	@Override
	public void onCheckedChanged(CompoundButton buttonView,
		boolean isChecked) {
	    listSlidingMenu.setDragEnabled(isChecked);
	    addTagButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
	}
    };

    private OnClickListener onAdd = new OnClickListener() {

	@Override
	public void onClick(View v) {

	}
    };
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
	@Override
	public void drop(int from, int to) {
	    if (from != to) {
		String item = (String) slidingMenuAdapter.getItem(from)
			.toString();
		// slidingMenuAdapter.
		// slidingMenuAdapter.insert(item, to);
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
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	// setContentView(R.layout.main);
    }

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

    public int getSortField() {
	return sortField;
    }

    public void setSortField(int sortField) {
	this.sortField = sortField;
    }

    public int getSortOrder() {
	return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
	this.sortOrder = sortOrder;
    }

}