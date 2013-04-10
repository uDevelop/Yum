package ru.inventos.yum;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LunchInfo extends Activity {
	private final static int MAX_CHARS_IN_LINE = 34;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_info);
		Intent intent = this.getIntent();
		TextView title = (TextView) findViewById(R.id.lunch_info_title);
		String str = intent.getStringExtra(Consts.LUNCH_INFO_TITLE);
		str = Utils.getFormatText(str, MAX_CHARS_IN_LINE, false);
		title.setText(str);
		TextView description = (TextView) findViewById(R.id.lunch_info_description); 
		str = intent.getStringExtra(Consts.LUNCH_INFO_DESCRIPTION);
		description.setText(str);
		float cost = intent.getFloatExtra(Consts.LUNCH_INFO_PRICE, -1f);
		str = String.format(Locale.US, "%.2f ", cost);
		TextView price = (TextView) findViewById(R.id.lunch_info_price);
		price.setText(str);
	}
	
	public void onBtnClick(View v) {
		finish();
	}
}
