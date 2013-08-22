package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements
		MainFragment.Callbacks {

	private final String TAG = "MainActivity";

	private SlidingMenu mSlidingMenu;

	private boolean mTwoPane;
	private boolean mThreePane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		// configure the SlidingMenu

		// Do we have static sliding menu?
		if (findViewById(R.id.sliding_fragment_container) != null) {
			mThreePane = true;
			getActionBar().setDisplayHomeAsUpEnabled(false);

		} else {
			mThreePane = false;
			getActionBar().setDisplayHomeAsUpEnabled(true);
			mSlidingMenu = new SlidingMenu(this);
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			mSlidingMenu.setBehindScrollScale(0.5f);
			mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			mSlidingMenu.setFadeEnabled(false);
			mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
			mSlidingMenu.setMenu(R.layout.fragment_slidingmenu_placeholder);
			mSlidingMenu.setShadowWidth(20);
			mSlidingMenu.setSlidingEnabled(true);

		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.sliding_fragment_container,
						new SlidingMenuFragment()).commit();

		// Do we have static details?
		if (findViewById(R.id.details_fragment_container) != null) {
			mTwoPane = true;
		} else {
			mTwoPane = false;
		}

		if (savedInstanceState == null) {
			MainFragment mainFragment = new MainFragment();

			if (mTwoPane) {
				mainFragment.setActivateOnItemClick(true);

			} else {
				mainFragment.setActivateOnItemClick(false);

			}
			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_fragment_container, mainFragment).commit();
		}

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
			arguments.putString(DetailsFragment.PACKAGE_NUMBER, id);
			DetailsFragment fragment = new DetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.details_fragment_container, fragment)
					.commit();

		} else {

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mSlidingMenu.toggle();
		}
		return super.onOptionsItemSelected(item);
	}
}
