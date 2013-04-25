package ru.inventos.yum;

import org.holoeverywhere.widget.Spinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Checkout extends Activity {
	private Cart mCart;
	private Spinner mTimeSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout);
		mCart = new Cart();
		makeActionBar();
		makeTimeSpinner();
	}
	
	private void makeActionBar() {		
		TextView tv = (TextView) findViewById(R.id.checkout_actionbar_order_count);
		tv.setText(Integer.toString(mCart.getCount()));
	}
	
	private void makeTimeSpinner() {
		mTimeSpinner = (Spinner) findViewById(R.id.checkout_time);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.checkout_times, R.layout.checkout_spinner_item);
		adapter.setDropDownViewResource(R.layout.dropdown_item);
		mTimeSpinner.setAdapter(adapter);
	}
	
	public void onOrderBtnClick(View v) {
		Intent intent = new Intent(this, Order2.class);
		startActivityForResult(intent, Consts.ORDER2_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Consts.ORDER2_REQUEST && data != null) {
				int answer = data.getIntExtra(Consts.ORDER2_ANSWER, -1);
				if (answer == Consts.ORDER2_CHECKOUT_REQUEST) {
					Intent  intent = new Intent(this, Report.class);
					startActivity(intent);
				}				
				finish();
		}
	}
	
	

}
