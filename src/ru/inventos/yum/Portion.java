package ru.inventos.yum;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Portion extends Activity {
	private NumberPicker mPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portion);
		mPicker = (NumberPicker) findViewById (R.id.portion_numberPicker);
        mPicker.setMaxValue(20);
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

}
