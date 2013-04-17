package ru.inventos.yum;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyOrdersListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private OrderItem[] mOrders;
	private ArrayList<OrderItem> mCurrentOrders;
	private View[] mViews;
	private String mMinDate;
	private String mMaxDate;
	private String[] mDaysOfWeek;
			
	public MyOrdersListAdapter(Context context, OrderItem[] orders) {
		mCurrentOrders = new ArrayList<OrderItem>();
		mOrders = orders;
		mInflater = LayoutInflater.from(context);
		Resources res = context.getResources();
		mDaysOfWeek = res.getStringArray(R.array.days_of_week_full);
		initCurrentOrders();
		fillViews();
	}
	
	private void initCurrentOrders() {
		mCurrentOrders.clear();
		mMinDate = "7953-12-04";
		mMaxDate = "2013-04-17";
		for(OrderItem item : mOrders) {
			mCurrentOrders.add(item);
			String curDate = item.time.substring(0, 9);
			if (curDate.compareTo(mMinDate) < 0) {
				mMinDate = curDate;
			}
			if (curDate.compareTo(mMaxDate) > 0) {
				mMaxDate = curDate;
			}			
		}
	}
	
	private void fillViews() {
		int count = mCurrentOrders.size(); 
		mViews = new View[count];
		View v = null;
		GregorianCalendar calendar = new GregorianCalendar();
		OrderItem item;
		ImageView status;
		TextView tv;
		String sday;
		String smonth;
		String syear;
		String shour;
		String str;
		int day;
		int month;
		int year;
		int hour;
		int dayOfWeek;
		
		for (int i = 0; i < count; i++) {
			item = mCurrentOrders.get(i);
			v = mInflater.inflate(R.layout.my_orders_list_item, null);
			status = (ImageView) v.findViewById(R.id.my_order_item_status);
			switch (item.status) {
			case Consts.ORDER_STATUS_ACTIVE:
				status.setImageResource(R.drawable.ic_launcher);
				break;
			case Consts.ORDER_STATUS_COMPLETE:
				status.setImageResource(R.drawable.ic_launcher);
				break;
			case Consts.ORDER_STATUS_CANCEL:
				status.setImageResource(R.drawable.ic_launcher);
				break;
			}
			tv = (TextView) v.findViewById(R.id.my_order_item_date);
			syear = item.time.substring(0, 3);
			smonth = item.time.substring(5, 6);
			sday = item.time.substring(8, 9);
			year = Integer.getInteger(syear);
			month = Integer.getInteger(smonth);
			day = Integer.getInteger(sday);
			calendar.set(year, month, day);
			dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			str = sday + '.' + smonth + '.' + syear + "\n" + mDaysOfWeek[dayOfWeek-1];
			tv.setText(str);
			tv = (TextView) v.findViewById(R.id.my_order_item_time);
			shour = item.time.substring(11, 12);
			hour = Integer.getInteger(shour);	
			str = shour + "<sup>00<sup>-" + Integer.toString(hour+1) +  "<sup>00<sup>"; 
			tv.setText(str);
			tv = (TextView) v.findViewById(R.id.my_order_item_cost);
			str = String.format(Locale.US, "%.2f", item.cost);
			tv.setText(str);
			mViews[i] = v;
		}
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		return mViews[position];
    }
	
	@Override
	public int getCount() {
		return mViews.length; 
	}

	 
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
