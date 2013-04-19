package ru.inventos.yum;

import java.util.Date;

import org.holoeverywhere.widget.CalendarView;
import org.holoeverywhere.widget.CalendarView.OnDateChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CalendarActivity extends Activity implements OnDateChangeListener {
	private CalendarView mCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		mCalendar = (CalendarView) findViewById(R.id.calendar_calendar);
		mCalendar.setOnDateChangeListener(this);
	}
	
	@Override
	public void onSelectedDayChange (CalendarView view, int year, int month, int dayOfMonth) {
		Date date = new Date(mCalendar.getDate());
		Intent intent = new Intent();
		intent.putExtra(Consts.MY_ORDERS_DATE, date);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	

	

}
