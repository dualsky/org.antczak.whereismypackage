
package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import org.antczak.whereIsMyPackage.dummy.DummyContent;

public class SlidingMenuFragment extends Fragment {

    private DragSortListView mTagsList;

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

        ArrayAdapter adapter = new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(), R.layout.list_item_tags,
                android.R.id.text1, DummyContent.ITEMS);

        mTagsList = (DragSortListView) rootView.findViewById(R.id.folders_list);
        mTagsList.setAdapter(adapter);
        mTagsList.setDragEnabled(true);

        return rootView;
    }
}
