package ru.inventos.yum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.holoeverywhere.widget.CalendarView;


public class CSpinnerAdapter extends BaseAdapter {
	private TextView mDateTV;
	private CalendarView mCalendar;
	private View mDropdownView;
	
	public CSpinnerAdapter(Context context) {
				LayoutInflater inflater = LayoutInflater.from(context);
				mDateTV = (TextView) inflater.inflate(R.layout.my_orders_spinner_item, null).findViewById(android.R.id.text1);
				mDateTV.setText("hello");
				mDropdownView = inflater.inflate(R.layout.my_orders_spinner_dropdown, null);
				//mCalendar = (CalendarView) mDropdownView.findViewById(R.id.my_orders_spinner_calendar);				
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		return mDateTV;
    }
	
	@Override
	public int getCount() {
		return 1; 
	}

	 
	@Override
	public Object getItem(int position) {
		return mDateTV;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return  mDropdownView;
    }

}
