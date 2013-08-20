package org.antczak.whereIsMyPackage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * An activity representing a list of Packages. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link DetailsActivity} representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical
 * panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MainFragment} and the item details (if present) is a
 * {@link DetailsFragment}.
 * <p>
 * This activity also implements the required {@link MainFragment.Callbacks}
 * interface to listen for item selections.
 */
public class MainActivity extends SlidingFragmentActivity implements
		MainFragment.Callbacks {

	private final String TAG = "MainActivity";

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private boolean mThreePane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Do we have static sliding menu?
		if (findViewById(R.id.sliding_menu) != null) {
			mThreePane = true;
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		} else {
			setBehindContentView(R.layout.fragment_sliding_menu_placeholder);
			getSlidingMenu().setSlidingEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.sliding_menu_placeholder,
							new SlidingMenuFragment()).commit();

		}

		// Do we have static details?
		if (findViewById(R.id.package_detail_container) != null) {
			mTwoPane = true;
			((MainFragment) getSupportFragmentManager().findFragmentById(
					R.id.main_fragment_placeholder)).setActivateOnItemClick(true);
		} 
		
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.ic_launcher);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

		Log.d(TAG, "smallestScreenWidthDp"
				+ getResources().getConfiguration().smallestScreenWidthDp);

	}

	/**
	 * Callback method from {@link MainFragment.Callbacks} indicating that the
	 * item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(DetailsFragment.ARG_ITEM_ID, id);
			DetailsFragment fragment = new DetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.package_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, DetailsActivity.class);
			detailIntent.putExtra(DetailsFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
