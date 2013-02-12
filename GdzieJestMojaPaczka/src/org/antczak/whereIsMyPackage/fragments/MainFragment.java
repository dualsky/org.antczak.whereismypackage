package org.antczak.whereIsMyPackage.fragments;

import org.antczak.whereIsMyPackage.R;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

	@Override
	public View onCreateView(org.holoeverywhere.LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container,
				false);
		// View tv = v.findViewById(R.id.textView1);
		// ((TextView)tv).setText("The TextView saves and restores this text.");

		// Retrieve the text editor and tell it to save and restore its state.
		// Note that you will often set this in the layout XML, but since
		// we are sharing our layout with the other fragment we will customize
		// it here.
		// ((TextView)v.findViewById(R.id.saved)).setSaveEnabled(true);
		return v;
	}

}
