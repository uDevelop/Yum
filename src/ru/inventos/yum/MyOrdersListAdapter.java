package ru.inventos.yum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyOrdersListAdapter extends BaseAdapter {
	private final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static short SORT_MODE_ASC_BY_DATE = 1;
	public final static short SORT_MODE_DESC_BY_DATE = -1;
	public final static short SORT_MODE_ASC_BY_TIME = 2;
	public final static short SORT_MODE_DESC_TIME = -2;
	public final static short SORT_MODE_ASC_BY_STATUS = 3;
	public final static short SORT_MODE_DESC_STATUS = -3;
	public final static short SORT_MODE_ASC_BY_PRICE = 4;
	public final static short SORT_MODE_DESC_PRICE = -4;
	private LayoutInflater mInflater;
	private OrderItem[] mOrders;
	private ArrayList<OrderItem> mCurrentOrders;
	private View[] mViews;
	private String mMinDate;
	private String mMaxDate;
	private String[] mDaysOfWeek;
	private OrderComparator mComparator;
	private short mSortMode;
	private SimpleDateFormat mFormatter;
			
	public MyOrdersListAdapter(Context context, OrderItem[] orders) {
		mCurrentOrders = new ArrayList<OrderItem>();
		mOrders = orders;
		mInflater = LayoutInflater.from(context);
		Resources res = context.getResources();
		mDaysOfWeek = res.getStringArray(R.array.days_of_week_full);
		mFormatter = new SimpleDateFormat(DATE_FORMAT);
		initCurrentOrders();
		mComparator = new OrderComparator();
		setSort(SORT_MODE_ASC_BY_DATE);	
	}
	
	private void initCurrentOrders() {
		mCurrentOrders.clear();
		mMinDate = "7953-12-04";
		mMaxDate = "1977-04-17";
		for(OrderItem item : mOrders) {
			mCurrentOrders.add(item);
			String curDate = parseDate(item.time);
			if (curDate.compareTo(mMinDate) < 0) {
				mMinDate = curDate;
			}
			if (curDate.compareTo(mMaxDate) > 0) {
				mMaxDate = curDate;
			}			
		}
	}
	
	private void fillCurrentOrders() {
		mCurrentOrders.clear();
		for(OrderItem item : mOrders) {
			mCurrentOrders.add(item);						
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
				status.setImageResource(R.drawable.inway);
				break;
			case Consts.ORDER_STATUS_COMPLETE:
				status.setImageResource(R.drawable.delivered);
				break;
			case Consts.ORDER_STATUS_CANCEL:
				status.setImageResource(R.drawable.cancel);
				break;
			}
			tv = (TextView) v.findViewById(R.id.my_order_item_date);
			syear = item.time.substring(0, 4);
			smonth = item.time.substring(5, 7);
			sday = item.time.substring(8, 10);
			year = Integer.parseInt(syear);
			month = Integer.parseInt(smonth);
			day = Integer.parseInt(sday);
			calendar.set(year, month-1, day); 
			dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			str = sday + '.' + smonth + '.' + syear + "\n" + mDaysOfWeek[dayOfWeek-1];
			tv.setText(str);
			tv = (TextView) v.findViewById(R.id.my_order_item_time);
			shour = item.time.substring(11, 13);
			hour = Integer.parseInt(shour);	
			str = shour + "<sup><small>00</small></sup>-" + Integer.toString(hour+1) 
						+  "<sup><small>00</small></sup>"; 
			tv.setText(Html.fromHtml(str));
			tv = (TextView) v.findViewById(R.id.my_order_item_cost);
			str = String.format(Locale.US, "%.2f", item.cost);
			tv.setText(str + ' ' + Consts.RU_SYMBOL);
			mViews[i] = v;
		}
		this.notifyDataSetChanged();
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
	
	public Date getMaxDate() {
		Date result = null;
		try {
			result = mFormatter.parse(mMaxDate);
		} catch (ParseException ex) {
			Log.e("MyOrdersListAdapter", ex.getMessage());
		}
		return result;
	}
	
	public Date getMinDate() {
		Date result = null;
		try {
			result = mFormatter.parse(mMinDate);
		} catch (ParseException ex) {
			Log.e("MyOrdersListAdapter", ex.getMessage());
		}
		return result;
	}
	
	public void  init() {
		initCurrentOrders();
		setSort(mSortMode);		
	}
	
	public void setPeriod(Date minDate, Date maxDate) {
		fillCurrentOrders();
		if (minDate != null) {
			mMinDate = mFormatter.format(minDate);
		}
		if (maxDate != null) {
			mMaxDate = mFormatter.format(maxDate);
		}		
		String date;
		ArrayList<OrderItem> filtered = new ArrayList<OrderItem>();
		for (OrderItem item : mCurrentOrders) {
			date = parseDate(item.time);
			if ((date.compareTo(mMinDate) > -1) && date.compareTo(mMaxDate) < 1)  {
				filtered.add(item);
			} 
		}
		mCurrentOrders = filtered;
		mComparator.setMode(mSortMode);
		Collections.sort(mCurrentOrders, mComparator);
		fillViews();
		
	}
	
	public void setSort(short sortMode) {
			mSortMode = sortMode;
			mComparator.setMode(sortMode);
			Collections.sort(mCurrentOrders, mComparator);	
			fillViews();
	}
	
	public short getSortMode() {
		return mSortMode;
	}
	
	private static String parseDate(String str) {
		return str.substring(0, 10);
	}
	
	private static String parseTime(String str) {
		return str.substring(11, 19);
	}
	
	private class OrderComparator implements Comparator<OrderItem> {
		private int mMode;  
		
		public void setMode(int mode) {
			mMode = mode;
		}
		
		public int compare(OrderItem order1, OrderItem order2) {
			switch (Math.abs(mMode)) { 
			case SORT_MODE_ASC_BY_DATE:
				final String mDate1 =  order1.time;
				final String mDate2 =  order2.time; 
				return mDate1.compareTo(mDate2) * mMode;
			case SORT_MODE_ASC_BY_TIME:
				final String mTime1 =  parseTime(order1.time);
				final String mTime2 =  parseTime(order2.time);
				return mTime1.compareTo(mTime2) * mMode;
			case SORT_MODE_ASC_BY_STATUS:
				return (order1.status - order2.status) * mMode;
			case SORT_MODE_ASC_BY_PRICE:
				return Float.compare(order1.cost, order2.cost) * mMode;			
			}	
			return 0;
		}	
	}	
}
