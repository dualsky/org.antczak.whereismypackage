
package org.antczak.whereIsMyPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
        // Do we have static sliding menu?
        if (findViewById(R.id.sliding_fragment_container) != null) {
            Log.i(TAG, "sliding_fragment_container != null");
            mThreePane = true;

        } else {
            Log.i(TAG, "sliding_fragment_container == null");
            mThreePane = false;
            mSlidingMenu = new SlidingMenu(this);
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            mSlidingMenu.setBehindScrollScale(1f);
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
                showHomeNone();
                mainFragment.setActivateOnItemClick(true);

            } else if (mTwoPane) {
                Log.i(TAG, "mTwoPane");
                showHomeAsSlidingMenu();
                mainFragment.setActivateOnItemClick(true);

            } else {
                Log.i(TAG, "mOnePane");
                showHomeAsSlidingMenu();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment_container, mainFragment,
                            MainFragment.FRAGMENT_TAG).commit();

        } else {

            Log.i(TAG, "savedInstanceState != null");

            if (mThreePane) {
                Log.i(TAG, "mThreePane");
                showHomeNone();
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainFragment.FRAGMENT_TAG);
                mainFragment.setActivateOnItemClick(true);

            } else if (mTwoPane) {
                Log.i(TAG, "mTwoPane");
                showHomeAsSlidingMenu();

                MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainFragment.FRAGMENT_TAG);
                mainFragment.setActivateOnItemClick(true);

            } else {
                Log.i(TAG, "mOnePane");

                DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(DetailsFragment.FRAGMENT_TAG);
                if (detailsFragment == null) {

                    showHomeAsSlidingMenu();

                    MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                            .findFragmentByTag(MainFragment.FRAGMENT_TAG);
                    mainFragment.setActivateOnItemClick(false);

                } else {
                    showHomeButton();

                    // getSupportFragmentManager()
                    // .beginTransaction()
                    // .replace(R.id.main_fragment_container, detailsFragment,
                    // DetailsFragment.FRAGMENT_TAG).commit();
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
            showHomeButton();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right,
                            R.anim.slide_out_left, R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .addToBackStack(null).replace(R.id.main_fragment_container,
                            detailsFragment, DetailsFragment.FRAGMENT_TAG).commit();

        }
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

    private void showHomeButton() {
        if (mSlidingMenu != null)
            mSlidingMenu.setSlidingEnabled(false);
        setHomeAsUpIndicator(R.drawable.ic_ab_back);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void showHomeAsSlidingMenu() {
        if (mSlidingMenu != null)
            mSlidingMenu.setSlidingEnabled(true);
        setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    private void showHomeNone() {
        if (mSlidingMenu != null)
            mSlidingMenu.setSlidingEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
    }

    private void handleBackAndUp(boolean isUp) {
        if (mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.toggle();
        }
        else {

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                showHomeAsSlidingMenu();
                getSupportFragmentManager().popBackStack();
            } else {
                if (isUp) {
                    mSlidingMenu.toggle();
                } else {
                    super.onBackPressed();
                }
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
}
