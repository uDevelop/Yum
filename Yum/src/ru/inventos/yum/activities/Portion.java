package ru.inventos.yum.activities;

import org.holoeverywhere.widget.NumberPicker;

import ru.inventos.yum.Cart;
import ru.inventos.yum.Consts;
import ru.inventos.yum.R;
import ru.inventos.yum.R.id;
import ru.inventos.yum.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

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
		if (count < 0) {
			count = 0;
		}
		mCart = new Cart();
		mPicker = (NumberPicker) findViewById (R.id.portion_numberPicker);
        mPicker.setMaxValue(count);
        mPicker.setMinValue(0);
        mPicker.setValue(mCart.getItem(mLunchId).count);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
        Button btn = (Button) findViewById(R.id.portion_btn);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        btn.requestFocus(); //сбиваем фокус у Picker
	}
	
	public void onBtnClick(View view) {
		mCart.setCount(mLunchId, mPicker.getValue()); 
	    finish();		
	}

}
