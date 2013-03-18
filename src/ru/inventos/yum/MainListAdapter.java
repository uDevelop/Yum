package ru.inventos.yum;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter {
	private ArrayList<View> mItems;
	private Context mContext;
	private LayoutInflater mInflater;
	private Cart mCart; 
	private NetStorage mNetStorage;
	private ArrayList<LunchItem> mLunchItems;
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
		for(LunchItem item: mLunchItems) {
			if (item.category.equals(mCurrentCategory) && (item.count != 0)) {
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
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

}
