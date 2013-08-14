package org.antczak.whereIsMyPackage.adpaters;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class SimpleCursorAdapterHistory extends SimpleCursorAdapter {

    private static final String TAG = "CursorAdapter";
    private Cursor c;
    LayoutInflater layoutInflater;
    Context ctx;

    public SimpleCursorAdapterHistory(Context context, int layout, Cursor c,
	    String[] from, int[] to) {
	super(context, layout, c, from, to);
	this.ctx = context;
	this.c = c;
	// this.layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public boolean areAllItemsEnabled() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	this.layoutInflater = (LayoutInflater) ctx
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	c.moveToPosition(position);
	Log.d(TAG,
		"packageNo: " + c.getString(1) + ", courierName: "
			+ c.getString(2) + ", monitor: " + c.getString(4));
	Log.v(TAG, "cursorPosition: " + position);
	if (!c.getString(2).equals("-1") && !c.getString(2).equals("-2")) {
	    convertView = this.layoutInflater.inflate(
		    R.layout.simple_list_item_search_history, parent, false);
	    int resID = ctx.getResources().getIdentifier(
		    "courier_" + c.getString(4), "drawable",
		    "org.antczak.whereIsMyPackage");

	    ((ImageView) convertView.findViewById(R.id.imageViewList1))
		    .setImageDrawable(ctx.getResources().getDrawable(resID));
	    if (c.isNull(5)) {
		((TextView) convertView.findViewById(R.id.textViewList1))
			.setText(c.getString(1));
		((TextView) convertView.findViewById(R.id.textViewList2))
			.setText(c.getString(2));
	    } else {
		((TextView) convertView.findViewById(R.id.textViewList1))
			.setText(c.getString(1));
		((TextView) convertView.findViewById(R.id.textViewList2))
			.setText(c.getString(2));
		((TextView) convertView.findViewById(R.id.textViewList3))
			.setText(c.getString(5));

	    }

	} else {
	    Log.v(TAG,
		    "Separator: " + c.getString(1) + ", count(*): "
			    + c.getString(2));
	    convertView = this.layoutInflater.inflate(R.layout.list_separator,
		    parent, false);
	    TextView tv1 = (TextView) convertView
		    .findViewById(R.id.textViewSeparator1);
	    if (c.getString(1).equals("-1")
		    && Integer.parseInt(c.getString(2)) > 0) {
		tv1.setText(R.string.separator_monitor);
	    } else if (c.getString(1).equals("-2")
		    && Integer.parseInt(c.getString(2)) > 0) {
		tv1.setText(R.string.separator_history);
	    } else {
		Log.v(TAG, "Empty view: " + c.getString(1));
		return new TwoLineListItem(this.ctx);
	    }
	}
	return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
	// TODO Auto-generated method stub
	super.notifyDataSetChanged();
    }

    @Override
    public void changeCursor(Cursor c) {
	// TODO Auto-generated method stub
	super.changeCursor(c);
	this.c = c;
    }

    @Override
    public boolean hasStableIds() {
	// TODO Auto-generated method stub
	return true; // super.hasStableIds();
    }

    @Override
    public boolean isEnabled(int position) {
	this.c.moveToPosition(position);
	return !c.getString(1).equals("-1") && !c.getString(1).equals("-2");

    }
}
