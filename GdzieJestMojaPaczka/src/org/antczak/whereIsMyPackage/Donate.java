package org.antczak.whereIsMyPackage;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

public class Donate extends PreferenceActivity implements OnClickListener {

	private static final String TAG = "Donate";
	// The PayPal server to be used - can also be ENV_NONE and ENV_LIVE
	private static final int server = PayPal.ENV_LIVE;
	// The ID of your application that you received from PayPal
	private static final String appID = "***REMOVED***";
	// This is passed in for the startActivityForResult() android function, the
	// value used is up to you
	private static final int request = 1;

	private int amount = 5;

	private String currency = "PLN";

	public static final String build = "10.12.09.8053";

	protected static final int INITIALIZE_SUCCESS = 0;
	protected static final int INITIALIZE_FAILURE = 1;

	Handler hRefresh = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INITIALIZE_SUCCESS:
				showSuccess();
				break;
			case INITIALIZE_FAILURE:
				showFailure();
				break;
			}
		}
	};

	TextView status;
	TextView message;
	CheckoutButton launchSimplePayment;
	LinearLayout content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_donate);
		setContentView(R.layout.donate);

		status = (TextView) findViewById(R.id.donateStatus);
		message = (TextView) findViewById(R.id.donateMessage);
		content = (LinearLayout) findViewById(R.id.donateContent);

		
		SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(this);
		sp.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangedListiner);
		onSharedPreferenceChangedListiner.onSharedPreferenceChanged(sp, null);
		libraryInitializationThread.start();
	}

	private OnSharedPreferenceChangeListener onSharedPreferenceChangedListiner = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			amount = Integer.parseInt(prefs.getString("amount", "5"));
			((Preference) findPreference("amount")).setSummary(amount + "");
			currency = prefs.getString("currency", "PLN");
			((Preference) findPreference("currency")).setSummary(currency);
		}
	};

	Thread libraryInitializationThread = new Thread() {
		public void run() {
			initLibrary();
			if (PayPal.getInstance().isLibraryInitialized()) {
				hRefresh.sendEmptyMessage(INITIALIZE_SUCCESS);
			} else {
				hRefresh.sendEmptyMessage(INITIALIZE_FAILURE);
			}
		}
	};

	private void initLibrary() {
		PayPal pp = PayPal.getInstance();
		if (pp == null) {
			pp = PayPal.initWithAppID(this, appID, server);
			pp.setLanguage("pl_PL");
			pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
		}
	}

	private void showSuccess() {
		PayPal pp = PayPal.getInstance();
		launchSimplePayment = pp.getCheckoutButton(this, PayPal.BUTTON_278x43,
				CheckoutButton.TEXT_DONATE);
		launchSimplePayment.setOnClickListener(this);
		content.addView(launchSimplePayment);
		content.setVisibility(View.VISIBLE);
		status.setText(R.string.donate_pp_ok);
	}

	private void showFailure() {
		status.setText(R.string.donate_pp_notok);
	}

	private PayPalPayment exampleSimplePayment() {
		PayPalPayment payment = new PayPalPayment();
		Log.v(TAG, "curr:" + currency + ", amount: " + amount + ", receipt: " + getString(R.string.donate_recipient));
		payment.setCurrencyType(currency);
		payment.setRecipient(getString(R.string.donate_recipient));
		payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);
		payment.setSubtotal(new BigDecimal(amount));
		//payment.setMerchantName("Gdzie jest moja paczka!?");
		return payment;
	}

	@Override
	public void onClick(View v) {
		PayPalPayment payment = exampleSimplePayment();
		Intent checkoutIntent = PayPal.getInstance().checkout(payment, this);
		startActivityForResult(checkoutIntent, request);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String resultStatus = "";
		String resultMessage = "";
		switch (resultCode) {
		case Activity.RESULT_OK:
			resultStatus = getString(R.string.donate_result_ok_status);
			resultMessage = getString(R.string.donate_result_ok_message);
			break;
		case Activity.RESULT_CANCELED:
			resultStatus = getString(R.string.donate_result_cancel_status);
			resultMessage = getString(R.string.donate_result_cancel_message);
			break;
		case PayPalActivity.RESULT_FAILURE:
			resultStatus = getString(R.string.donate_result_error_status);
			resultMessage = getString(R.string.donate_result_error_message)
					+ data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
		}
		status.setText(resultStatus);
		message.setText(resultMessage);
		launchSimplePayment.updateButton();
	}
}
