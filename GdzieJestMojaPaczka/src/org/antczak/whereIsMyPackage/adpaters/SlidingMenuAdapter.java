package org.antczak.whereIsMyPackage.adpaters;



import java.util.List;

import org.antczak.whereIsMyPackage.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdapter extends ArrayAdapter<String> implements OnItemClickListener {
    private LayoutInflater mInflater;
    private List<String> folders;
	private static final String TAG = "org.antczak.whereIsMyPackage.adpaters.SlidingMenuAdapter";
	 
	public SlidingMenuAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);

	}

	public SlidingMenuAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		 mInflater = LayoutInflater.from(context);
		 folders = objects;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.v(TAG, "onItemClick() " + arg1.toString());
		((Checkable)arg1).setChecked(true);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  ViewHolder holder;

          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.list_item_slidingmenu, parent);
              holder = new ViewHolder();
              holder.mText1 = (TextView) convertView.findViewById(android.R.id.text1);
              holder.mText2 = (TextView) convertView.findViewById(android.R.id.text2);
              holder.mView = (View) convertView.findViewById(R.id.selectionHandler);
              holder.mImageView = (ImageView) convertView.findViewById(R.id.imageViewPro);
              convertView.setTag(holder);
          } else {
              holder = (ViewHolder) convertView.getTag();
          }

          holder.mText1.setText(folders.get(position));
          holder.mText2.setText("(34)");
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
