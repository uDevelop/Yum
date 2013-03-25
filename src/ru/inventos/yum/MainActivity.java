package ru.inventos.yum;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity implements Updatable, SlidingMenu.OnOpenListener, 
		SlidingMenu.OnCloseListener, OnItemClickListener {
	private TextView mOrderCount;
	private ImageButton mOrderBtn;
	private FrameLayout mOrderFrame;
	private TextView mTitle;
	private ImageView mStatus;
	private SlidingMenu mMenu;
	private ListView mMenuList; 
	private Cart mCart;
	private MainListAdapter mLunchListAdapter;
	private NetStorage mNetStorage;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		findViewsByIds();
		mMenu.setOnOpenListener(this);
		mMenu.setOnCloseListener(this);
		createMenu();
		createMenu2();		
		mMenu.showMenu();
		mCart = new Cart();
		mNetStorage = new NetStorage(this);
		mLunchListAdapter = new MainListAdapter(this, mCart, mNetStorage);
		ListView lunchList = (ListView) findViewById(R.id.main_list);
		lunchList.setAdapter(mLunchListAdapter);
		selectFirstCategory();		
		registerListeners();
		update();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void update() {
		updateOrderBtn();
	}
	
	private void updateOrderBtn() {
		int count = mCart.getCount();
		if (count == 0) {
			mOrderCount.setText("0");
			mOrderBtn.setImageResource(R.drawable.ic_launcher);
			mOrderBtn.setClickable(false);
		}
		else {
			mOrderCount.setText(Integer.toString(count));
			mOrderBtn.setImageResource(R.drawable.pic);
			mOrderBtn.setClickable(true);
		}
	}
	
	private void findViewsByIds() {
		mOrderCount = (TextView) findViewById(R.id.main_actionbar_order_count);
		mOrderBtn = (ImageButton) findViewById(R.id.main_actionbar_order_btn); 
		mOrderFrame = (FrameLayout) findViewById(R.id.main_actionbar_order);
		mTitle = (TextView) findViewById(R.id.main_actionbar_title);
		mStatus = (ImageView) findViewById(R.id.main_actionbar_status);
		mMenu = (SlidingMenu) findViewById(R.id.slidingmenulayout);
		mMenuList = (ListView) findViewById(R.id.main_menu_menulist);
	}
	
	private void createMenu() {
		MainMenuAdapter adapter = new MainMenuAdapter(this, 
        		R.array.menu_item_names, R.array.menu_item_pic_names);
        mMenuList.setAdapter(adapter);
        mMenuList.setOnItemClickListener(this);
	}
	
	private void createMenu2() {
		ListView menuList2 = (ListView) findViewById(R.id.main_menu_menulist2);
		MainMenuAdapter adapter = new MainMenuAdapter(this, 
        		R.array.menu_item_names2, R.array.menu_item_pic_names2);
        menuList2.setAdapter(adapter);		
	}
	
	private void selectFirstCategory() {
		View view = (View) mMenuList.getAdapter().getItem(0);
		String category = (String) view.getTag();
		mLunchListAdapter.setCategory(category);
		mMenuList.setTag(Integer.valueOf(0));				
	}
	
	@Override 
	public void onOpen() {
		mOrderFrame.setVisibility(View.INVISIBLE);
		mStatus.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.lunch_in_office);
		
	}
	
	@Override 
	public void onClose() {
		mOrderFrame.setVisibility(View.VISIBLE);
		mStatus.setVisibility(View.INVISIBLE);
		mTitle.setText(R.string.choise_of_dishes);
	}
	
	public void onMenuBtnClick(View view) {
		mMenu.toggle();		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.main_menu_menulist) {
			String name = (String) view.getTag();
			mLunchListAdapter.setCategory(name);	
			parent.setTag(Integer.valueOf(position));
		}
		else {
			switch (position) {
			case 1:
				break;
			case 2: 
				break;
			}
		}
	}	
	
	
	
	
	private void registerListeners() {
		mCart.registerDataListener(mLunchListAdapter);
		mCart.registerDataListener(this);
	}
	
	private void unregisterListeners() {
		mCart.unregisterDataListener(mLunchListAdapter);
		mCart.unregisterDataListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterListeners();				
	}
	
	

}
