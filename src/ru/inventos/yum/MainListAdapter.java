package ru.inventos.yum;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter implements OnClickListener, Updatable {
	private final static String ruSymbol = "\u0584";
	private ArrayList<View> mItems;
	private Context mContext;
	private LayoutInflater mInflater;
	private Cart mCart; 
	private NetStorage mNetStorage;
	private LunchItem[] mLunchItems;
	private int[] mLunchIds;
	private String mCurrentCategory;
	
	public MainListAdapter(Context context, Cart cart, NetStorage netStorage) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCart = cart;
		mNetStorage = netStorage;
		mItems = new ArrayList<View>();
	}
	
	@Override
	public void update() {
		this.notifyDataSetChanged();
	}
	
	
	
	public void setCategory(String name) {
		mItems.clear();
		mCurrentCategory = name;
		mNetStorage.getLunchList(this);		
	}
	
	public void UpdateList(LunchItem[] lunchItems) {
		mLunchItems = lunchItems;
		View view = null; 
		int num = 0;
		if ((mLunchItems != null) && (lunchItems.length > 0)) {
			mLunchIds = new int[lunchItems.length];
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
					str = String.format("%.2f", item.price)  + " "+ruSymbol;						
					tv.setText(str);
					ImageButton btn = (ImageButton) view.findViewById(R.id.main_list_item_add_btn); 
					btn.setTag(new Integer(num-1));
					btn.setOnClickListener(this);
					mItems.add(view);
				}
			}
		}
		this.notifyDataSetChanged();
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		CartItem item = mCart.getItem(mLunchIds[position]);
		View view = mItems.get(position);
		TextView count = (TextView) view.findViewById(R.id.main_list_item_count);
		ImageButton btn = (ImageButton) view.findViewById(R.id.main_list_item_add_btn); 
		if (item == null) {
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
	
	@Override 
	public void onClick(View v) {
		Integer index = (Integer) v.getTag();
		int ind = index.intValue();		
		CartItem citem = mCart.getItem(mLunchIds[ind]);
		LunchItem item = mLunchItems[ind];
		if (citem == null) {						
			mCart.add(mLunchIds[ind], item.name, item.price);			
		}
		else {
			Intent intent = new Intent(mContext, Portion.class);
			intent.putExtra(Consts.PORTION_ELEMENT_ID, mLunchIds[ind]);
			intent.putExtra(Consts.PORTION_MAX_COUNT, item.count);
			mContext.startActivity(intent);
		}
		this.notifyDataSetChanged();
	}
}
