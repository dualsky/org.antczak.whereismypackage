package org.antczak.whereIsMyPackage.adpaters;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ArrayAdapterMenu extends ArrayAdapter<Object> {

	private static final String TAG = "ArrayAdapterMenu";

	Object[][] objectArray;
	LayoutInflater layoutInflater;
	Context ctx;

	public ArrayAdapterMenu(Context context, int resource,
			int textViewResourceId, Object[][] objects) {
		super(context, resource, textViewResourceId, objects);
		objectArray = objects;
		ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.layoutInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.options_menu,
					parent, false);
		}
		TextView tv = (TextView) convertView
				.findViewById(R.id.textViewOptionsMenu1);
		tv.setText((String) objectArray[position][0]);
		if (!isEnabled(position)) {
			Log.e(TAG, "notEnabled: " + position);
			tv.setTextColor(-7829368);
		} else
			tv.setTextColor(-1);
		Log.d(TAG, "pos: " + position + ", color: " + tv.getCurrentTextColor());
		((ImageView) convertView.findViewById(R.id.imageViewOptionsMenu1))
				.setImageResource((Integer) objectArray[position][1]);

		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		Log.v(TAG, position + " " + objectArray[position].length);
		if (objectArray[position].length == 3) {
			Log.v(TAG, "retu " + (Boolean) objectArray[position][2]);
			return (Boolean) objectArray[position][2];
		} else
			return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
