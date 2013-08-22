package org.antczak.whereIsMyPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.antczak.whereIsMyPackage.dummy.DummyContent;

public class DetailsFragment extends Fragment {

	public static final String PACKAGE_NUMBER = "package_number";
	public static final String COURIER_CODE = "courier_code";

	private String mPackageNumber;
	private String mCourierCode;

	public DetailsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPackageNumber = null;
		mCourierCode = null;

		if (getArguments().containsKey(PACKAGE_NUMBER)) {
			mPackageNumber = getArguments().getString(PACKAGE_NUMBER);
			mCourierCode = getArguments().getString(COURIER_CODE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_details, container,
				false);

		if (mPackageNumber != null) {
			((TextView) rootView.findViewById(R.id.package_detail))
					.setText(mPackageNumber + " " + mCourierCode);
		}

		return rootView;
	}
}
