package ru.inventos.yum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends Activity implements LoginReceiver {
	private EditText mEmail;
	private EditText mPassword;
	private ProgressBar mProgressBar;
	private ImageButton mButton;
	private LoginSystem mLoginSystem;
	 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViews();
		setNotLoggedIn();
		mLoginSystem = new LoginSystem(this);
		blockInput();
		mLoginSystem.autoLogin(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
	
	private void findViews() {
		mEmail = (EditText) findViewById(R.id.login_email_edit);
		mPassword = (EditText) findViewById(R.id.login_password_edit);
		mButton = (ImageButton) findViewById(R.id.login_next_btn);
		mProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);
	}
	
	private void blockInput() {
		mEmail.setEnabled(false);
		mPassword.setEnabled(false);
		mButton.setEnabled(false);
		mProgressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	private void unBlockInput() {
		mEmail.setEnabled(true);
		mPassword.setEnabled(true);
		mButton.setEnabled(true);
		mProgressBar.setVisibility(ProgressBar.INVISIBLE);
	}
	
	public void onNextBtnClick(View v) {
		String email = mEmail.getText().toString();
		String password = mPassword.getText().toString(); 
		blockInput();
		mLoginSystem.login(this, email, password);
	}
	
	public void showLoginError() {
		Toast toast = Toast.makeText(getApplicationContext(), 
					R.string.login_error, Consts.TOASTS_SHOW_DURATION);		
		toast.show();
		mPassword.setText("");		
	}
	
	@Override
	public void receiveLoginStatus(byte status) {
		switch (status) {
		case LoginSystem.STATUS_EMPTY_DATA:
			unBlockInput();
			break;
		case LoginSystem.STATUS_FAIL:
			showLoginError();
			unBlockInput();
			break;
		case LoginSystem.STATUS_OK:
			setLoggedIn();
			finish();
			break;		
		}
	}
	
	
}
