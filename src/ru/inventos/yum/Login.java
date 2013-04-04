package ru.inventos.yum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Login extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setNotLoggedIn();
		
		
	}
	
	private void setNotLoggedIn() {
		Intent intent = new Intent();
		intent.putExtra(Consts.LOGIN_STATUS, false);
		setResult(RESULT_OK, intent);
	}
	
	private void setLoggedIn() {
		Intent intent = new Intent();
		intent.putExtra(Consts.LOGIN_STATUS, true);	
		setResult(RESULT_OK, intent);
	}
	
	
}
