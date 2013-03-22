package ru.inventos.yum;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
	private Cart mCart;
	private MainListAdapter mLunchListAdapter;
	private NetStorage mNetStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		createMenu();
		createMenu2();
		SlidingMenu menu = (SlidingMenu) findViewById(R.id.slidingmenulayout);
		menu.showMenu();
		mCart = new Cart();
		mNetStorage = new NetStorage(this);
		mLunchListAdapter = new MainListAdapter(this, mCart, mNetStorage);
		ListView lunchList = (ListView) findViewById(R.id.main_list);
		lunchList.setAdapter(mLunchListAdapter);
		mLunchListAdapter.setCategory("\u041f\u0435\u0440\u0432\u043e\u0435");
		mCart.registerDataListener(mLunchListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private void createMenu() {
		ListView menuList = (ListView) findViewById(R.id.main_menu_menulist);
		MainMenuAdapter adapter = new MainMenuAdapter(this, 
        		R.array.menu_item_names, R.array.menu_item_pic_names);
        menuList.setAdapter(adapter);		
	}
	
	private void createMenu2() {
		ListView menuList2 = (ListView) findViewById(R.id.main_menu_menulist2);
		MainMenuAdapter adapter = new MainMenuAdapter(this, 
        		R.array.menu_item_names2, R.array.menu_item_pic_names2);
        menuList2.setAdapter(adapter);		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCart.unregisterDataListener(mLunchListAdapter);				
	}

}
