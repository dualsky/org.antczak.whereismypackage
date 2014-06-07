
package org.antczak.whereIsMyPackage.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.antczak.whereIsMyPackage.R;
import org.antczak.whereIsMyPackage.SlidingMenuFragment.OnEditModeStateChangeListener;

public class TagsAdapter extends SimpleCursorAdapter implements OnEditModeStateChangeListener {

    private static final String TAG = "TagsAdapter";

    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mLayoutInflater;
    private boolean mIsInEditMode;

    public TagsAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
        mCursor = c;
        mIsInEditMode = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        int realPosition = position < 0 ? 0 : position;
        mCursor.moveToPosition(realPosition);

        if (convertView == null) {
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(
                    R.layout.list_item_tags, parent, false);
            holder.tv1 = (TextView) convertView.findViewById(android.R.id.text1);
            holder.tv2 = (TextView) convertView.findViewById(android.R.id.text2);
            holder.drag = (ImageView) convertView.findViewById(R.id.imageViewDrag);
            holder.remove = (ImageView) convertView.findViewById(R.id.imageViewRemove);
            holder.pro = (ImageView) convertView.findViewById(R.id.imageViewPro);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv1.setText(mCursor.getString(1));
        if (mIsInEditMode) {
            holder.drag.setVisibility(View.VISIBLE);
            holder.remove.setVisibility(View.VISIBLE);
            holder.pro.setVisibility(View.GONE);
            holder.tv2.setVisibility(View.GONE);
        } else {
            holder.drag.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
            holder.pro.setVisibility(View.VISIBLE);
            holder.tv2.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class Holder {
        TextView tv1;
        TextView tv2;
        ImageView drag;
        ImageView remove;
        ImageView pro;
    }

    @Override
    public void editModeStateChanged(boolean editModeState) {
        Log.i(TAG, "editModeStateChanged:" + editModeState);
        mIsInEditMode = editModeState;
        notifyDataSetChanged();

    }
}
