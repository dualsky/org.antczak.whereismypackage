package org.antczak.whereIsMyPackage.adpaters;

import java.util.ArrayList;
import java.util.List;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterCourierOrder extends ArrayAdapter<String> {
	
	List<String> stringsArray;
	LayoutInflater layoutInflater;
	Context ctx;
	
	public ArrayAdapterCourierOrder(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		stringsArray = objects;
		this.ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.layoutInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.preferences_courier_order_layout_row,
					parent, false);
		}
		TextView tv = (TextView) convertView
				.findViewById(R.id.label);
		tv.setText(stringsArray.get(position).toString());
		return convertView;
	}

}
