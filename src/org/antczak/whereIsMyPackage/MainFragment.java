
package org.antczak.whereIsMyPackage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.antczak.whereIsMyPackage.dummy.DummyContent;

public class MainFragment extends Fragment implements OnItemClickListener {

    private final String TAG = "MainFragment";

    public static final String FRAGMENT_TAG = "MainFragment";

    private ListView mPackagesList;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String ACTIV_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private boolean mActivateOnItemClick = false;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
        Log.d(TAG, "I'm main fragment.");
        Log.d(TAG, "mActivateOnItemClick:" + mActivateOnItemClick);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container,
                false);

        mPackagesList = (ListView) rootView.findViewById(R.id.package_list);
        ArrayAdapter adapter = new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(), R.layout.list_item_packages,
                android.R.id.text1, DummyContent.ITEMS);
        
        mPackagesList.setAdapter(adapter);
        // TODO: replace with a real list adapter.
        // mPackagesList.setAdapter();
        mPackagesList
                .setChoiceMode(isActivateOnItemClick() ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);

        mPackagesList.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(ACTIV_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(ACTIV_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(ACTIV_POSITION, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mPackagesList.setItemChecked(mActivatedPosition, false);
        } else {
            mPackagesList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public boolean isActivateOnItemClick() {
        Log.d(TAG, "isActivateOnItemClick:" + mActivateOnItemClick);
        return mActivateOnItemClick;
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        Log.d(TAG, "setActivateOnItemClick:" + activateOnItemClick);
        this.mActivateOnItemClick = activateOnItemClick;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(TAG, "onItemClick: " + arg2);
        mCallbacks.onItemSelected(DummyContent.ITEMS.get(arg2).id);

    }

}
