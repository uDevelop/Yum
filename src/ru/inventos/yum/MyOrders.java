package ru.inventos.yum;

import org.holoeverywhere.widget.Spinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyOrders extends Activity {
	private Spinner sortSpinner;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);
		createSortSpinner();
		OrderItem[] ordr = new OrderItem[2];
		ordr[1] = new OrderItem();
		ordr[1].cost = 1024.72f;
		ordr[1].status = Consts.ORDER_STATUS_ACTIVE;
		ordr[1].time="2013-04-29T09:11:15Z";
		ordr[0] = new OrderItem();
		ordr[0].cost = 550;
		ordr[0].status = Consts.ORDER_STATUS_CANCEL;
		ordr[0].time="2015-03-14T14:11:15Z";
		MyOrdersListAdapter adapter = new MyOrdersListAdapter(this, ordr);
		ListView list = (ListView) findViewById(R.id.my_orders_list);
		list.setAdapter(adapter);
		adapter.setSort(MyOrdersListAdapter.SORT_MODE_DESC_BY_DATE);
		
		Intent intent = new Intent(this, CalendarActivity.class);
		startActivity(intent);
	}
	
	private void createSortSpinner() {
		sortSpinner = (Spinner) findViewById(R.id.my_orders_sort);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.my_orders_sort_type, R.layout.my_orders_sort);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(adapter);
	}
		
	
	
}
