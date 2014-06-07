
package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;

import org.antczak.whereIsMyPackage.adapter.TagsAdapter;

public class SlidingMenuFragment extends Fragment implements OnCheckedChangeListener,
        OnClickListener {

    public static final String FRAGMENT_TAG = "SlidingMenuFragment";
    
    private ListView mTagsList;
    private View mTagAdd;
    private OnEditModeStateChangeListener mOnEditModeStateChangeListener;
    private TagsAdapter mAdapter;
    
    public interface OnEditModeStateChangeListener {
        public void editModeStateChanged(boolean isInEditMode);
    }

    public SlidingMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slidingmenu, container,
                false);
        ((Switch) rootView.findViewById(R.id.editMode)).setOnCheckedChangeListener(this);
        mTagAdd = rootView.findViewById(R.id.tagAdd);
        mTagAdd.setOnClickListener(this);
        String[] from = new String[] {
                "_id", "NAME"
        };

        mAdapter = new TagsAdapter(
                getActivity(), R.layout.list_item_tags,
                MainApp.getDB().getTags(), from, null);
        mOnEditModeStateChangeListener = mAdapter;
        mTagsList = (ListView) rootView.findViewById(R.id.folders_list);
        mTagsList.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (mOnEditModeStateChangeListener != null) {
            mOnEditModeStateChangeListener.editModeStateChanged(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    public void setOnEditModeStateChangeListener(
            OnEditModeStateChangeListener onEditModeStateChangeListener) {
        this.mOnEditModeStateChangeListener = onEditModeStateChangeListener;
    }

    
    
    //TODO czy to musi byc
    @Override
    public void onSaveInstanceState(Bundle outState) {
        setUserVisibleHint(false);
        super.onSaveInstanceState(outState);
    }


}
