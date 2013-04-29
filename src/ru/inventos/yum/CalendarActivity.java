package ru.inventos.yum;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.holoeverywhere.widget.CalendarView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.caldroid.CaldroidFragment;


public class CalendarActivity extends FragmentActivity/* implements OnDateChangeListener */{
	private CalendarView mCalendar;
	private CaldroidFragment dialogCaldroidFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		
		final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

		// Setup caldroid fragment
		// **** If you want normal CaldroidFragment, use below line ****
		CaldroidFragment caldroidFragment = new CaldroidFragment();

		// This is to show customized fragment
		// **** If you want customized version, uncomment below line ****
		// caldroidFragment = new CaldroidSampleCustomFragment();

		// Setup arguments

		// If Activity is created after rotation
		if (savedInstanceState != null) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState,
					"CALDROID_SAVED_STATE");
		}
		// If activity is created from fresh
		else {
			Bundle args = new Bundle();
			Calendar cal = Calendar.getInstance();
			args.putInt("month", cal.get(Calendar.MONTH) + 1);
			args.putInt("year", cal.get(Calendar.YEAR));
			args.putBoolean("enableSwipe", true);
			args.putBoolean("fitAllMonths", false);

			// Uncomment this to customize startDayOfWeek
			// args.putInt("startDayOfWeek", 6); // Saturday
			caldroidFragment.setArguments(args);
		}

		// Attach to the activity
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();		
		
		tst(savedInstanceState);
		//mCalendar = (CalendarView) findViewById(R.id.calendar_calendar);
		//mCalendar.setOnDateChangeListener(this);
	}
	
	/*@Override
	public void onSelectedDayChange (CalendarView view, int year, int month, int dayOfMonth) {
		Date date = new Date(mCalendar.getDate());
		Intent intent = new Intent();
		intent.putExtra(Consts.MY_ORDERS_DATE, date);
		setResult(RESULT_OK, intent);
		finish();
	}*/
	
	private void tst(Bundle state) {

		// Setup caldroid to use as dialog
		dialogCaldroidFragment = new CaldroidFragment();
		//dialogCaldroidFragment.setCaldroidListener(listener);

		// If activity is recovered from rotation
		final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
		if (state != null) {
			dialogCaldroidFragment.restoreDialogStatesFromKey(getSupportFragmentManager(),
					state, "DIALOG_CALDROID_SAVED_STATE",
					dialogTag);
			Bundle args = dialogCaldroidFragment.getArguments();
			args.putString("dialogTitle", "Select a date");
		} else {
			// Setup arguments
			Bundle bundle = new Bundle();
			// Setup dialogTitle
			bundle.putString("dialogTitle", "Select a date");
			dialogCaldroidFragment.setArguments(bundle);
		}

		dialogCaldroidFragment.show(getSupportFragmentManager(),
				dialogTag);
	
	}
	
	

	

}
