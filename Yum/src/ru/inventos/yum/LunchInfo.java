package ru.inventos.yum;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LunchInfo extends Activity implements ImageReceiver {
	private final static int MAX_CHARS_IN_LINE = 33;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_info);
		Intent intent = this.getIntent();
		TextView title = (TextView) findViewById(R.id.lunch_info_title);
		String str = intent.getStringExtra(Consts.LUNCH_INFO_TITLE);
		str = Utils.getFormatText(str, MAX_CHARS_IN_LINE, false);
		title.setText(str);
		TextView description = (TextView) findViewById(R.id.lunch_info_description); 
		str = intent.getStringExtra(Consts.LUNCH_INFO_DESCRIPTION);
		description.setText(str);
		float cost = intent.getFloatExtra(Consts.LUNCH_INFO_PRICE, -1f);
		str = String.format(Locale.US, "%.2f ", cost);
		TextView price = (TextView) findViewById(R.id.lunch_info_price);
		price.setText(str);
		ImageStorage imageStorage = new ImageStorage(this);
		str = intent.getStringExtra(Consts.LUNCH_INFO_IMAGE);
		imageStorage.getImage(str, this);
	}
	
	public void onBtnClick(View v) {
		finish();
	}
	
	public void receiveImage(Bitmap bmp) {
		if (bmp != null) {
			ImageView img = (ImageView) findViewById(R.id.lunch_info_image);
			img.setImageBitmap(bmp);
		}	
	}
}