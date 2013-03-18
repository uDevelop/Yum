package ru.inventos.yum;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter {
	private ArrayList<View> mItems;
	private Context mContext;
	private LayoutInflater mInflater;
	private Cart mCart; 
	private NetStorage mNetStorage;
	private ArrayList<LunchItem> mLunchItems;
	private int[] mLunchIds;
	private String mCurrentCategory;
	
	public MainListAdapter(Context context, Cart cart, NetStorage netStorage) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCart = cart;
		mNetStorage = netStorage;
		mLunchItems = new ArrayList<LunchItem>();				
	}
	
	public void setCategory(String name) {
		//TODO:
		mItems.clear();
		
	}
	
	public void UpdateList() {
		View view = null; 
		int num = 0;
		int count = 0;
		for(LunchItem item: mLunchItems) {
			if (item.category.equals(mCurrentCategory) && (item.count != 0)) {
				count++;
			}
		}
		mLunchIds = new int[count];		
		for(LunchItem item: mLunchItems) {
			if (item.category.equals(mCurrentCategory) && (item.count != 0)) {
				mLunchIds[num] = item.id;
				num++;
				view = mInflater.inflate(R.layout.main_list_item, null);
				TextView tv =  (TextView) view.findViewById(R.id.main_list_item_name);
				String str = Integer.toString(num) + ". " + item.name + " (" 
						+ Integer.toString(item.weight) + " г.)";
				tv.setText(str);
				tv =  (TextView) view.findViewById(R.id.main_list_item_price);
				str = Float.toString(item.price);				
			}
		}
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null; //mItems[position];
    }
	
	@Override
	  public int getCount() {
	    return mItems.size(); 
	  }

	 
	@Override
	public Object getItem(int position) {
		CartItem item = mCart.getItem(mLunchIds[position]);
		View view = mItems.get(position);
		TextView count = (TextView) view.findViewById(R.id.main_list_item_count);
		ImageButton btn = (ImageButton) view.findViewById(R.id.main_list_item_add_btn); 
		if (item.count == 0) {
			count.setText("");
			btn.setImageResource(R.drawable.ic_launcher);			
		}
		else {
			count.setText(Integer.toString(item.count));
			btn.setImageResource(R.drawable.pic);
			
		}
		return view;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

}
