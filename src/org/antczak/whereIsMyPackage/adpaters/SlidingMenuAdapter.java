package org.antczak.whereIsMyPackage.adpaters;



import java.util.List;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdapter extends SimpleCursorAdapter implements OnItemClickListener {
  
	private LayoutInflater mInflater;
    private Cursor folders;
	private static final String TAG = "org.antczak.whereIsMyPackage.adpaters.SlidingMenuAdapter";

	public SlidingMenuAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mInflater = LayoutInflater.from(context);
	folders = c;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.v(TAG, "onItemClick() " + arg1.toString());
		((Checkable)arg1).setChecked(true);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  ViewHolder holder;
			folders.moveToPosition(position);
			Log.v(TAG, "getView() " + position);
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.list_slidingmenu_item, parent, false);
              holder = new ViewHolder();
              holder.mText1 = (TextView) convertView.findViewById(android.R.id.text1);
              holder.mText2 = (TextView) convertView.findViewById(android.R.id.text2);
              holder.mView = (View) convertView.findViewById(R.id.selectionHandler);
              holder.mImageView = (ImageView) convertView.findViewById(R.id.imageViewPro);
              convertView.setTag(holder);
          } else {
              holder = (ViewHolder) convertView.getTag();
          }

          holder.mText1.setText(folders.getString(1));
          holder.mText2.setText("34");
          if (position % 3 ==0) 
        	  holder.mImageView.setImageResource(R.drawable.pro);
          return convertView;
      }


    static class ViewHolder {
        TextView mText1;
        TextView mText2;
        View mView;
        ImageView mImageView;
        
    }

}
