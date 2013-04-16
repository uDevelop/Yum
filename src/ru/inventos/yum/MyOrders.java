package ru.inventos.yum;

import org.holoeverywhere.widget.Spinner;

import android.os.Bundle;
import android.app.Activity;

import android.widget.ArrayAdapter;

public class MyOrders extends Activity {
	private Spinner sortSpinner;
	private Spinner periodStartSpinner;
	private Spinner periodEndSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);
		createSortSpinner();
		createPeriodSpinners();
	}
	
	private void createSortSpinner() {
		sortSpinner = (Spinner) findViewById(R.id.my_orders_sort);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.my_orders_sort_type, R.layout.my_orders_sort);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(adapter);
	}
		
	
	private void createPeriodSpinners() {
		periodStartSpinner = (Spinner) (Spinner) findViewById(R.id.my_orders_period_start);
		CSpinnerAdapter adapter = new CSpinnerAdapter(this);
		periodStartSpinner.setAdapter(adapter);	
	}
}
