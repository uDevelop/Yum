package ru.inventos.yum.adapters;

import java.util.ArrayList;
import java.util.Locale;

import ru.inventos.yum.Cart;
import ru.inventos.yum.CartItem;
import ru.inventos.yum.Consts;
import ru.inventos.yum.LunchItem;
import ru.inventos.yum.NetStorage;
import ru.inventos.yum.R;
import ru.inventos.yum.Utils;
import ru.inventos.yum.R.drawable;
import ru.inventos.yum.R.id;
import ru.inventos.yum.R.layout;
import ru.inventos.yum.activities.LunchInfo;
import ru.inventos.yum.activities.Portion;
import ru.inventos.yum.interfaces.LunchListReceiver;
import ru.inventos.yum.interfaces.Updatable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter implements OnClickListener,  
		LunchListReceiver, Updatable {
	private final static int MAX_LINE_LENGTH = 18;
	private ArrayList<View> mViews;
	private Context mContext;
	private LayoutInflater mInflater;
	private Cart mCart; 
	private NetStorage mNetStorage;
	private ArrayList<LunchItem> mCategoryItems;
	private LunchItem[] mLunchItems;
	private String mCurrentCategory;
		
	public MainListAdapter(Context context, Cart cart, NetStorage netStorage) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCart = cart;
		mNetStorage = netStorage;
		mViews = new ArrayList<View>();
		mLunchItems = null;
		mCategoryItems = new ArrayList<LunchItem>();
	}
	
	@Override
	public void update() {
		this.notifyDataSetChanged();
	}	
	
	public void pullList() {
		mNetStorage.getLunchList(this);
	}
	
	public void setCategory(String name) {
		pullList();
		if (name != null) { 
			mCurrentCategory = name;
			updateCategory(mLunchItems, name);
		}
	}
	
	private void updateCategory(LunchItem[] lunchItems, String currentCategory) {
		mCategoryItems.clear();
		mViews.clear();
		View view = null; 
		int num = 0;
		if (lunchItems != null) {
			for(LunchItem item: lunchItems) {
				if (item.category.equalsIgnoreCase(currentCategory) && (item.count != 0)) {
					mCategoryItems.add(item);
					num++;
					view = mInflater.inflate(R.layout.main_list_item, null);
					TextView tv =  (TextView) view.findViewById(R.id.main_list_item_name);
					String str = Integer.toString(num) + ". " + item.name + " (" 
							+ Integer.toString(item.weight) + " г.)";
					str = Utils.getFormatText(str, MAX_LINE_LENGTH, true);
					tv.setText(str);
					tv =  (TextView) view.findViewById(R.id.main_list_item_price);
					str = String.format(Locale.US, "%.2f", item.price)  + " ";						
					tv.setText(str);
					ImageButton btn = (ImageButton) view.findViewById(R.id.main_list_item_add_btn); 
					btn.setTag(Integer.valueOf(num-1));
					btn.setOnClickListener(this); 
					view.setTag(Integer.valueOf(num-1));
					view.setOnClickListener(this);
					mViews.add(view);
				}
			}
		}
		this.notifyDataSetChanged();
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LunchItem lunchItem = mCategoryItems.get(position);
		CartItem item = mCart.getItem(lunchItem.id);
		View view = mViews.get(position);
		TextView count = (TextView) view.findViewById(R.id.main_list_item_count);
		ImageButton btn = (ImageButton) view.findViewById(R.id.main_list_item_add_btn);
		if (item == null && mCart.containsCategory(lunchItem.category)) {
			btn.setImageResource(R.drawable.button_add_inactive);
			btn.setClickable(false);
			count.setText("");
		}
		else {
			btn.setClickable(true);
			if (item == null) {
				count.setText("");
				btn.setImageResource(R.drawable.button_add);			
			}
			else {
				count.setText(Integer.toString(item.count));
				btn.setImageResource(R.drawable.button_more);			
			}
		}
		return view;
    }
	
	@Override
	public int getCount() {
		return mViews.size(); 
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
		if (v.getId() == R.id.main_list_item_add_btn) {			
			LunchItem item = mCategoryItems.get(ind);
			CartItem citem = mCart.getItem(item.id);		
			if (citem == null) {						
				mCart.add(item.id, item.name, item.price, item.weight, item.category);			
			}
			else {
				Intent intent = new Intent(mContext, Portion.class);
				intent.putExtra(Consts.PORTION_ELEMENT_ID, item.id);
				intent.putExtra(Consts.PORTION_MAX_COUNT, item.count);
				mContext.startActivity(intent);
			}
			this.notifyDataSetChanged();
		}
		else {
			v.setPressed(true);
			showLunchInfo(ind);
		}
	}
	
	private void showLunchInfo(int position) {
		LunchItem item = mCategoryItems.get(position);
		Intent intent = new Intent(mContext, LunchInfo.class);
		String str = item.name + " (" + Integer.toString(item.weight) + " г.)";
		intent.putExtra(Consts.LUNCH_INFO_TITLE, str);
		intent.putExtra(Consts.LUNCH_INFO_PRICE, item.price);
		intent.putExtra(Consts.LUNCH_INFO_DESCRIPTION, item.description);
		intent.putExtra(Consts.LUNCH_INFO_IMAGE, item.image);
		mContext.startActivity(intent);
	}
	
	@Override
	public void receiveLunchList(LunchItem[] items) {
		if (items != null) {
			mLunchItems = items;
			if (mCurrentCategory != null) {
				updateCategory(mLunchItems, mCurrentCategory);
			}
		}
	}
	
	public boolean findAndShow(String name) {
		for (LunchItem item: mLunchItems) {
			if (item.name.equalsIgnoreCase(name)) {
				LunchItem[] items = new LunchItem[1];
				items[0] = item;
				updateCategory(items, item.category);
				mCurrentCategory = null; //чтобы не обновлялась категория
				return true;
			}
		}
		return false;
	}
}
