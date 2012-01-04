package org.antczak.whereIsMyPackage;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;

public class Donate extends Activity implements OnClickListener {

	// The PayPal server to be used - can also be ENV_NONE and ENV_LIVE
	private static final int server = PayPal.ENV_SANDBOX;
	// The ID of your application that you received from PayPal
	private static final String appID = "APP-80W284485P519543T";
	// This is passed in for the startActivityForResult() android function, the value used is up to you
	private static final int request = 1;
	
	public static final String build = "10.12.09.8053";
	
	protected static final int INITIALIZE_SUCCESS = 0;
	protected static final int INITIALIZE_FAILURE = 1;
	
	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
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
	CheckoutButton launchSimplePayment;
	LinearLayout content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate);
		status = (TextView) findViewById(R.id.donatetextView1);
		content = (LinearLayout) findViewById(R.id.donateContent);
		Thread libraryInitializationThread = new Thread() {
			public void run() {
				initLibrary();
				if (PayPal.getInstance().isLibraryInitialized()) {
					hRefresh.sendEmptyMessage(INITIALIZE_SUCCESS);
				}
				else {
					hRefresh.sendEmptyMessage(INITIALIZE_FAILURE);
				}
			}
		};
		libraryInitializationThread.start();
	}
	
	private void initLibrary() {
		PayPal pp = PayPal.getInstance();
		if(pp == null) {
			pp = PayPal.initWithAppID(this, appID, server);
        	pp.setLanguage("pl_PL"); 
        	pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER); 
		}
	}
	
	private void showSuccess() {
		PayPal pp = PayPal.getInstance();
		// Get the CheckoutButton. There are five different sizes. The text on the button can either be of type TEXT_PAY or TEXT_DONATE.
		launchSimplePayment = pp.getCheckoutButton(this, PayPal.BUTTON_152x33, CheckoutButton.TEXT_DONATE);
		// You'll need to have an OnClickListener for the CheckoutButton. For this application, MPL_Example implements OnClickListener and we
		// have the onClick() method below.
		launchSimplePayment.setOnClickListener(this);
		// The CheckoutButton is an android LinearLayout so we can add it to our display like any other View.
		content.addView(launchSimplePayment);
	}
	
	private void showFailure() {
		status.setText("FAILURE");
	}

	private PayPalPayment exampleSimplePayment() {
		PayPalPayment payment = new PayPalPayment();
    	payment.setCurrencyType("USD");
    	payment.setRecipient("example-merchant-1@paypal.com");
    	payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);
    	
    	// PayPalInvoiceData can contain tax and shipping amounts. It also contains an ArrayList of PayPalInvoiceItem which can
    	// be filled out. These are not required for any transaction.
    	PayPalInvoiceData invoice = new PayPalInvoiceData();
    	// PayPalInvoiceItem has several parameters available to it. None of these parameters is required.
    	PayPalInvoiceItem item1 = new PayPalInvoiceItem();
    	// Sets the name of the item.
    	item1.setName("Dotacja");
    	item1.setTotalPrice(new BigDecimal("6.00"));
    	// Sets the unit price.
//    	item1.setUnitPrice(new BigDecimal("2.00"));
    	// Sets the quantity.
    	item1.setQuantity(1);
    	// Add the PayPalInvoiceItem to the PayPalInvoiceData. Alternatively, you can create an ArrayList<PayPalInvoiceItem>
    	// and pass it to the PayPalInvoiceData function setInvoiceItems().
    	invoice.getInvoiceItems().add(item1);
    	
    	
    	// Sets the PayPalPayment invoice data.
    	payment.setInvoiceData(invoice);

    	// Sets the merchant name. This is the name of your Application or Company.
    	payment.setMerchantName("Gdzie jest moja paczka!?");
    	
    	return payment;
	}
	
	@Override
	public void onClick(View v) {
		PayPalPayment payment = exampleSimplePayment();	
		// Use checkout to create our Intent.
		Intent checkoutIntent = PayPal.getInstance().checkout(payment, this);
		// Use the android's startActivityForResult() and pass in our Intent. This will start the library.
    	startActivityForResult(checkoutIntent, request);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
