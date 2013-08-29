
package org.antczak.whereIsMyPackage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

import org.antczak.whereIsMyPackage.R;

public class CheckableListViewItem extends LinearLayout implements Checkable {

    private static final String TAG = "org.antczak.whereIsMyPackage.adpaters.SlidingMenuAdapter";

    private boolean isChecked;

    public CheckableListViewItem(Context context) {
        super(context);
    }

    public CheckableListViewItem(Context context, AttributeSet attrs,
            int defStyleRes) {
        super(context, attrs, defStyleRes);
    }

    public CheckableListViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        Log.d(TAG, "setChecked(" + isChecked + ") " + this.toString());
        this.isChecked = isChecked;
        View v = findViewById(R.id.selectionHandler);
        v.setBackgroundResource(isChecked ? R.drawable.list_activated_holo
                : android.R.color.transparent);
    }

    @Override
    public void toggle() {
        Log.d(TAG, "toggle() " + this.toString());
        isChecked = !isChecked;
        setChecked(isChecked);
    }

}
