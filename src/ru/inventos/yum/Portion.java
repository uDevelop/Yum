package ru.inventos.yum;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Portion extends Activity {
	private NumberPicker mPicker;
	private int mLunchId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		mLunchId = intent.getIntExtra(Consts.PORTION_ELEMENT_ID, -1);
		int count = intent.getIntExtra(Consts.PORTION_MAX_COUNT, 0); 
		setContentView(R.layout.activity_portion);
		mPicker = (NumberPicker) findViewById (R.id.portion_numberPicker);
        mPicker.setMaxValue(count);
        mPicker.setMinValue(0);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);         
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.portion, menu);
		return true;
	}
	
	public void onBtnClick(View view) {
		Cart cart = new Cart();
		cart.setCount(mLunchId, mPicker.getValue());
	    finish();		
	}

}
