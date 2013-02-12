package org.antczak.whereIsMyPackage;

import org.antczak.whereIsMyPackage.fragments.DetailsFragment;
import org.holoeverywhere.app.Activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.MenuItem;

public class DetailsActivity extends Activity {

	private static final String TAG = "Details";
	History history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(R.string.notification);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_details);

	}

	@Override
	protected void onPause() {
		super.onPause();
		this.history.closeConnection();
		this.history = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (history == null)
			history = new History(this);
		DetailsFragment fragment = new DetailsFragment();
		fragment.setArguments(getIntent().getExtras());
		fragment.setHistory(history);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.detailsFragmentPhone, fragment);
		fragmentTransaction.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
