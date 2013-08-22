package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.antczak.whereIsMyPackage.dummy.DummyContent;

public class SlidingMenuFragment extends Fragment {

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

		((TextView) rootView.findViewById(R.id.package_detail))
				.setText("I'm sliding menu");

		return rootView;
	}
}
