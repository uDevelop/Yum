package ru.inventos.yum;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;;

public class OrderListAdapter extends BaseAdapter {
	private final static int MAX_LINE_LENGTH = 18;
	private final static String RU_SYMBOL = "\u0584"; 
	private ArrayList<View> mItems;
	private Context mContext;
	private LayoutInflater mInflater;
	private Cart mCart; 
		
	public OrderListAdapter(Context context, Cart cart) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCart = cart;
		mItems = new ArrayList<View>();
		createList();
	}
	
	private static String getTextWithTabs(String str, int charsInStr) {
		int len = str.length();
		int[] ind = new int[len];
		int chars = 0;
		int qty = 0;
		for(int i = 0; i < len; i++) {
			chars++;
			if ((str.charAt(i) == ' ') && 
					(str.indexOf(' ', i + 1) - i + chars - 1 > charsInStr)) {
				ind[qty] = i;
				chars = 0; 
				qty++; 
			}
			else if ((str.charAt(i) == '(') && 
					(str.indexOf(')', i + 1) - i + chars > charsInStr)) {
				ind[qty] = i - 1;
				chars = 0;
				qty++;
				
			}
			 
		}
		String res = ""; 
		int start = 0;
		for (int i = 0; i < qty; i++) {
			res = res + str.substring(start, ind[i]) +"\n\t";
			start = ind[i] + 1;
		}
		res = res + str.substring(start, len);
		return res;		
	}
	
	public void createList() {
		int count = mCart.getCount();
		View view;
		for (int i = 0; i < count; i++) {
			view = mInflater.inflate(R.layout.order_list_item, null);
			CartItem item = mCart.getItemByIndex(i);
			TextView name = (TextView) view.findViewById(R.id.order_item_name);
			String str = Integer.toString(i+1) + ". " + item.name + " (" 
					+ Integer.toString(item.weight) + " г.)";
			str = getTextWithTabs(str, MAX_LINE_LENGTH);
			name.setText(str);
			TextView  price = (TextView) view.findViewById(R.id.order_item_price);
			str = Integer.toString(item.count) + " x " 
					+ String.format(Locale.US, "%.2f", item.price) + " " + RU_SYMBOL;
			price.setText(str);
			mItems.add(view);
		}	
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		return mItems.get(position);
	}
	
	@Override
	public int getCount() {
		return mItems.size(); 
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
