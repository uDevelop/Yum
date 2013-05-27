package ru.inventos.yum.activities;

import ru.inventos.yum.Consts;
import ru.inventos.yum.NetStorage;
import ru.inventos.yum.R;
import ru.inventos.yum.interfaces.LoginReceiver;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends Activity implements LoginReceiver, OnTouchListener {
	private final static String MASK = "******";
	private EditText mEmail;
	private EditText mPassword;
	private ProgressBar mProgressBar;
	private ImageButton mButton;
	private NetStorage mNetStorage;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViews();
		setNotLoggedIn();
		mNetStorage = new NetStorage(this);
		boolean isFirst = getIntent().getBooleanExtra(Consts.LOGIN_IS_FIRST_LOGIN, false);
		if (isFirst) {
			blockInput();
			mNetStorage.autoLogin(this);
			mEmail.setText(MASK);
			mPassword.setText(MASK);
		}
		else {
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);
		}
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
		mEmail.setOnTouchListener(this);
		mPassword.setOnTouchListener(this);
	}
	
	private void blockInput() {
		mEmail.setEnabled(false);
		mPassword.setEnabled(false);
		mEmail.setFocusable(false);
		mPassword.setFocusable(false);
		mButton.setEnabled(false);
		mProgressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	private void unBlockInput() {
		mEmail.setEnabled(true);
		mPassword.setEnabled(true);
		mEmail.setFocusable(true);
		mPassword.setFocusable(true);
		mEmail.setFocusableInTouchMode(true);
		mPassword.setFocusableInTouchMode(true);
		mButton.setEnabled(true);
		mProgressBar.setVisibility(ProgressBar.INVISIBLE);
	}
	
	public void onNextBtnClick(View v) {
		blockInput();
		if (mEmail.getText().toString().equals(MASK)) {
			mNetStorage.autoLogin(this);
		}
		else {
			String email = mEmail.getText().toString();
			String password = mPassword.getText().toString();
			mNetStorage.login(this, email, password);
		}		
	}
	
	public void showLoginError() {
		Toast toast = Toast.makeText(getApplicationContext(), 
					R.string.login_error, Consts.TOASTS_SHOW_DURATION);		
		toast.show();
		mEmail.setText("");
		mPassword.setText("");		
	}
	
	@Override
	public void receiveLoginStatus(byte status) {
		switch (status) {     
		case Consts.LOGIN_STATUS_EMPTY_DATA:
			unBlockInput();
			break;
		case Consts.LOGIN_STATUS_FAIL:
			showLoginError();
			unBlockInput();
			break;
		case Consts.LOGIN_STATUS_AUTOLOGIN_FAIL:
			mEmail.setText("");
			mPassword.setText("");
			unBlockInput();
			break;
		case Consts.LOGIN_STATUS_OK:
			setLoggedIn();
			finish();
			break;		
		}
	}
	
	@Override 
	public boolean onTouch(View v, MotionEvent even) {
		EditText edit = (EditText) v;
		if (edit.getText().toString().equals(MASK)) {			
			edit.setText("");
		}
		return false;
	}	
}
