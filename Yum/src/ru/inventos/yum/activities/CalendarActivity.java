package ru.inventos.yum.activities;

import java.util.Calendar;
import java.util.Date;

import ru.inventos.yum.CaldroidCustomFragment;
import ru.inventos.yum.Consts;
import ru.inventos.yum.R;
import ru.inventos.yum.R.id;
import ru.inventos.yum.R.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.caldroid.CaldroidFragment;
import com.caldroid.CaldroidListener;

public class CalendarActivity extends FragmentActivity{
	private CaldroidCustomFragment mCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		Date date = (Date) this.getIntent().getSerializableExtra(Consts.CALENDAR_DATE);
		setResult(date);
		mCalendar = new CaldroidCustomFragment(this);
		Bundle args = new Bundle();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		args.putInt("month", calendar.get(Calendar.MONTH) + 1);
		args.putInt("year", calendar.get(Calendar.YEAR));
		args.putBoolean("enableSwipe", true);
		args.putBoolean("fitAllMonths", false);
		args.putInt("startDayOfWeek", 1); 
		mCalendar.setArguments(args);
		FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
		transact.replace(R.id.calendar_calendar, mCalendar);
		transact.commit();	
		mCalendar.setCaldroidListener(new CaldroidListener() {
			public void onSelectDate(Date date, View view) {
		    	setResult(date);
		    	finish();
		    }
		});
	}
	
	private void setResult(Date date) {
		Intent intent = new Intent();
		intent.putExtra(Consts.MY_ORDERS_DATE, date);
		setResult(RESULT_OK, intent);
	}  
}
