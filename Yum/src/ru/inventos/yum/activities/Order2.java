package ru.inventos.yum.activities;

import java.util.Locale;

import ru.inventos.yum.Cart;
import ru.inventos.yum.Consts;
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

public class Order2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order2);
		Cart cart = new Cart();
		OrderListAdapter adapter = new OrderListAdapter(this, cart);
		ListView list = (ListView) findViewById(R.id.order2_list);
		list.setAdapter(adapter);
		TextView totalPrice = (TextView) findViewById(R.id.order2_price);
		String str = String.format(Locale.US, "%.2f", cart.getTotalPrice()) + " " ;
		totalPrice.setText(str);
	}
	
	public void onEditBtnClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(Consts.ORDER2_ANSWER, Consts.ORDER2_EDIT_REQUEST);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void onCheckoutBtnClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(Consts.ORDER2_ANSWER, Consts.ORDER2_CHECKOUT_REQUEST);
		setResult(RESULT_OK, intent);
		finish();
	}
}
