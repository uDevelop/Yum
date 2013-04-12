package ru.inventos.yum;

import org.holoeverywhere.widget.NumberPicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Portion extends Activity {
	private NumberPicker mPicker;
	private int mLunchId;
	private Cart mCart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portion);
		Intent intent = this.getIntent();
		mLunchId = intent.getIntExtra(Consts.PORTION_ELEMENT_ID, -1);
		int count = intent.getIntExtra(Consts.PORTION_MAX_COUNT, 0); 
		mCart = new Cart();
		mPicker = (NumberPicker) findViewById (R.id.portion_numberPicker);
        mPicker.setMaxValue(count);
        mPicker.setMinValue(0);
        mPicker.setValue(mCart.getItem(mLunchId).count);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	public void onBtnClick(View view) {
		mCart.setCount(mLunchId, mPicker.getValue()); 
	    finish();		
	}

}
