package ru.inventos.yum;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		createMenu();
		
 
       
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

}
