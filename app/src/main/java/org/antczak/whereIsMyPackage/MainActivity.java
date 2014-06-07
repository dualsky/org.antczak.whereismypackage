
package org.antczak.whereIsMyPackage;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity implements
        MainFragment.Callbacks {

    private final String TAG = "MainActivity";

    private boolean mTwoPane;
    private boolean mThreePane;
    private boolean mAreDetailsVisible;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mThreePane = false;
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            mDrawerToggle = new ActionBarDrawerToggle(
                    this, /* host Activity */
                    mDrawerLayout, /* DrawerLayout object */
                    R.drawable.ic_drawer, /*
                                           * nav drawer image to replace 'Up'
                                           * caret
                                           */
                    R.string.app_name, /*
                                        * "open drawer" description for
                                        * accessibility
                                        */
                    R.string.title_package_detail /*
                                                   * "close drawer" description
                                                   * for accessibility
                                                   */
                    ) {
                        public void onDrawerClosed(View view) {
                            getActionBar().setTitle("sdf");
                            invalidateOptionsMenu();
                            // creates call to
                            // onPrepareOptionsMenu()
                        }

                        public void onDrawerOpened(View drawerView) {
                            getActionBar().setTitle("dddd");
                            invalidateOptionsMenu();
                            // creates call to
                            // onPrepareOptionsMenu()
                        }
                    };
            mDrawerLayout.setDrawerListener(mDrawerToggle);

        } else {
            mThreePane = true;
        }

        SlidingMenuFragment slidingMenuFragment = (SlidingMenuFragment) getSupportFragmentManager()
                .findFragmentByTag(SlidingMenuFragment.FRAGMENT_TAG);
        if (slidingMenuFragment == null) {
            slidingMenuFragment = new SlidingMenuFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.sliding_fragment_container, slidingMenuFragment,
                            SlidingMenuFragment.FRAGMENT_TAG).commit();
        }

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
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment_container, mainFragment,
                            MainFragment.FRAGMENT_TAG).commit();
            if (mThreePane) {
                Log.i(TAG, "mThreePane");
                disableHomeAsUp();
                mainFragment.setActivateOnItemClick(true);

            } else if (mTwoPane) {
                Log.i(TAG, "mTwoPane");
                enableDrawer();
                mainFragment.setActivateOnItemClick(true);

            } else {
                Log.i(TAG, "mOnePane");
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
            }

        } else {

            Log.i(TAG, "savedInstanceState != null");

            if (mThreePane || mTwoPane) {
                if (mTwoPane) {
                    Log.i(TAG, "mTwoPane");
                    enableDrawer();
                } else {
                    Log.i(TAG, "mThreePane");
                    disableHomeAsUp();
                }
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainFragment.FRAGMENT_TAG);
                mainFragment.setActivateOnItemClick(true);

            } else {
                Log.i(TAG, "mOnePane");

                DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(DetailsFragment.FRAGMENT_TAG);
                if (detailsFragment == null) {
                    enableDrawer();
                    MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                            .findFragmentByTag(MainFragment.FRAGMENT_TAG);
                    mainFragment.setActivateOnItemClick(false);

                } else {
                    enableHomeAsUp();
                    // getSupportFragmentManager()
                    // .beginTransaction()
                    // .replace(R.id.main_fragment_container, detailsFragment,
                    // DetailsFragment.FRAGMENT_TAG).commit();
                }
            }

        }

        if (!mThreePane) {
            mDrawerToggle.syncState();
        }

        Log.d(TAG, "smallestScreenWidthDp "
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
            if (mTwoPane)
                enableDrawer();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment_container, detailsFragment,
                            DetailsFragment.FRAGMENT_TAG).commit();

        } else {

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right,
                            R.anim.slide_out_left, R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .addToBackStack(null).replace(R.id.main_fragment_container,
                            detailsFragment, DetailsFragment.FRAGMENT_TAG).commit();
            enableHomeAsUp();

        }
    }

    private void enableDrawer() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void enableHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void disableHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
    }

    private void handleBackAndUp(boolean isUp) {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            enableDrawer();
            getSupportFragmentManager().popBackStack();
        } else {
            if (isUp) {
                if (mDrawerLayout.isDrawerVisible(Gravity.START)) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        handleBackAndUp(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackAndUp(true);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
}
