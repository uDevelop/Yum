package ru.inventos.yum.activities;

import ru.inventos.yum.Cart;
import ru.inventos.yum.CartItem;
import ru.inventos.yum.Consts;
import ru.inventos.yum.NetStorage;
import ru.inventos.yum.R;
import ru.inventos.yum.interfaces.OrderStatusReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Report extends Activity implements OrderStatusReceiver {
	private Cart mCart;
	private NetStorage netStorage;
	private TextView mTitle;
	private TextView mText1;
	private TextView mText2;
	private TextView mText3;
	private ImageButton mButton;	
	private ImageView mReportBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		mCart = new Cart();
		netStorage = new NetStorage(this);
		findViews();
		setWait();		
		CartItem[] items = mCart.getArray();
		mCart.clear();
		netStorage.makeOrder(this, items);		
	}
	
	private void findViews() {
		mTitle = (TextView) findViewById(R.id.report_title);
		mText1 =  (TextView) findViewById(R.id.report_text1);
		mText2 =  (TextView) findViewById(R.id.report_text2);
		mText3 =  (TextView) findViewById(R.id.report_text3);
		mButton = (ImageButton) findViewById(R.id.report_btn);	
		mReportBtn = (ImageView) findViewById(R.id.report_image);
	}
	
	private void setWait() {		
		mTitle.setText(R.string.report_title_wait);
		mText1.setText(R.string.report_text1_wait);
		mText2.setVisibility(TextView.INVISIBLE);
		mText3.setVisibility(TextView.INVISIBLE);
		mButton.setVisibility(ImageButton.INVISIBLE);
		mReportBtn.setClickable(false);
	}
	
	private void setError() {		
		mTitle.setText(R.string.report_title_err);
		mText1.setText(R.string.report_text1_err);
		mText2.setVisibility(TextView.GONE);
		mText3.setVisibility(TextView.VISIBLE);
		mText3.setText(R.string.report_text3_err);
		mButton.setVisibility(ImageButton.VISIBLE);	
		mReportBtn.setClickable(true);
	}
	
	private void setOk() {		
		mTitle.setText(R.string.report_title_normal);
		mText1.setText(R.string.report_text1_normal);
		mText2.setVisibility(TextView.VISIBLE);
		mText2.setText(R.string.report_text2_normal);
		mText3.setVisibility(TextView.VISIBLE);
		mText3.setText(R.string.report_text3_normal);
		mButton.setVisibility(ImageButton.VISIBLE);
		mReportBtn.setClickable(true);
	}
	
	
	public void onBtnClick(View v) {		
		finish();
	}
	
	public void onMenuBtnClick(View v) {		
		finish();
	}
	
	@Override
	public void receiveStatus(byte status) {
		if (status == Consts.CHECKOUT_STATUS_OK) {
			setOk();
		}
		else if (status == Consts.CHECKOUT_STATUS_ERROR) {
			setError();
		}
		else if (status == Consts.CHECKOUT_STATUS_NETWORK_FAIL) {
			setError();
			mText1.setText(R.string.report_text1_net_err);			
		}
	}
}
