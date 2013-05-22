package ru.inventos.yum.activities;

import java.util.Locale;

import ru.inventos.yum.Cart;
import ru.inventos.yum.R;
import ru.inventos.yum.R.id;
import ru.inventos.yum.R.layout;
import ru.inventos.yum.adapters.OrderListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Order extends Activity {
	private Cart mCart; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		mCart = new Cart();
		OrderListAdapter adapter = new OrderListAdapter(this, mCart);
		ListView list = (ListView) findViewById(R.id.order_list);
		list.setAdapter(adapter);
		TextView totalPrice = (TextView) findViewById(R.id.order_price);
		String str = String.format(Locale.US, "%.2f", mCart.getTotalPrice()) + " " ;
		totalPrice.setText(str);
	}
	
	
	public void onClearBtnClick(View v) {
		mCart.clear();
		finish();
	}
	
	public void onCheckoutBtnClick(View v)  {
		Intent intent = new Intent(this, Checkout.class);
		startActivity(intent);
		finish();
	}
}
