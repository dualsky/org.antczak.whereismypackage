package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements
		MainFragment.Callbacks {

	private final String TAG = "MainActivity";

	private SlidingMenu mSlidingMenu;

	private boolean mTwoPane;
	private boolean mThreePane;
	private boolean mAreDetailsVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// configure the SlidingMenu
		View customNav = LayoutInflater.from(this).inflate(R.layout.activity_main_actionbar, null);
		getActionBar().setCustomView(customNav);
		// Do we have static sliding menu?
		if (findViewById(R.id.sliding_fragment_container) != null) {
			mThreePane = true;

		} else {
			mThreePane = false;
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
			mTwoPane = mThreePane ? false : true;
		} else {
			mTwoPane = false;
		}

		Log.i(TAG, "How many panes? One? " + (!mTwoPane && !mThreePane)
				+ " Two? " + mTwoPane + " Three? " + mThreePane);

		if (savedInstanceState == null) {

			Log.i(TAG, "savedInstanceState == null");

			MainFragment mainFragment = new MainFragment();

			if (mThreePane) {
				Log.i(TAG, "mThreePane");
				mainFragment.setActivateOnItemClick(true);

			} else if (mTwoPane) {
				Log.i(TAG, "mTwoPane");
				setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
				getActionBar().setDisplayHomeAsUpEnabled(true);
				getActionBar().setHomeButtonEnabled(true);
				mainFragment.setActivateOnItemClick(true);

			} else {
				Log.i(TAG, "mOnePane");
				setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
				getActionBar().setDisplayHomeAsUpEnabled(true);
				getActionBar().setHomeButtonEnabled(true);
				mainFragment.setActivateOnItemClick(false);

			}

			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.main_fragment_container, mainFragment,
							MainFragment.FRAGMENT_TAG).commit();

		} else {

			Log.i(TAG, "savedInstanceState != null");

			if (mThreePane) {
				Log.i(TAG, "mThreePane");
				MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
						.findFragmentByTag(MainFragment.FRAGMENT_TAG);
				mainFragment.setActivateOnItemClick(true);

			} else if (mTwoPane) {
				Log.i(TAG, "mTwoPane");
				// setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
				MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
						.findFragmentByTag(MainFragment.FRAGMENT_TAG);
				setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
				getActionBar().setDisplayHomeAsUpEnabled(true);
				getActionBar().setHomeButtonEnabled(true);
				mainFragment.setActivateOnItemClick(true);

			} else {
				Log.i(TAG, "mOnePane");
				// setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
				getActionBar().setHomeButtonEnabled(true);
				DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
						.findFragmentByTag(DetailsFragment.FRAGMENT_TAG);
				if (detailsFragment == null) {

					setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
					getActionBar().setDisplayHomeAsUpEnabled(true);
					getActionBar().setHomeButtonEnabled(true);

					MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
							.findFragmentByTag(MainFragment.FRAGMENT_TAG);
					mainFragment.setActivateOnItemClick(false);

				} else {

					setHomeAsUpIndicator(android.R.attr.homeAsUpIndicator);
					getActionBar().setDisplayHomeAsUpEnabled(true);
					getActionBar().setHomeButtonEnabled(true);

					getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.main_fragment_container, detailsFragment,
									DetailsFragment.FRAGMENT_TAG).commit();
				}
			}

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
		Log.i(TAG, "onItemSelected, id:" + id);
		Log.i(TAG, "How many panes? One? " + (!mTwoPane && !mThreePane)
				+ " Two? " + mTwoPane + " Three? " + mThreePane);
		DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
				.findFragmentByTag(DetailsFragment.FRAGMENT_TAG);
		if (detailsFragment == null) {
			Bundle arguments = new Bundle();
			arguments.putString(DetailsFragment.PACKAGE_NUMBER, id);
			detailsFragment = new DetailsFragment();
			detailsFragment.setArguments(arguments);
		}
		if (mTwoPane || mThreePane) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.details_fragment_container, detailsFragment,
							DetailsFragment.FRAGMENT_TAG).commit();

		} else {
			setHomeAsUpIndicator(android.R.attr.homeAsUpIndicator);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
			getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.main_fragment_container, detailsFragment,
							DetailsFragment.FRAGMENT_TAG).commit();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void setHomeAsUpIndicator(int resId) {
		final View home = findViewById(android.R.id.home);
		if (home == null)
			return;

		final ViewGroup parent = (ViewGroup) home.getParent();
		final int childcount = parent.getChildCount();
		if (childcount != 2)
			return;

		final View first = parent.getChildAt(0);
		final View second = parent.getChildAt(1);
		final View up = first.getId() == android.R.id.home ? second : first;
		if (up instanceof ImageView)
			((ImageView) up).setImageResource(resId);
		((ImageView) up).setImageResource(resId);

	}

}
