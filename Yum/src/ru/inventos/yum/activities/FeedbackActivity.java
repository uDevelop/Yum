package ru.inventos.yum.activities;

import org.holoeverywhere.widget.Spinner;

import ru.inventos.yum.NetStorage;
import ru.inventos.yum.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class FeedbackActivity extends Activity implements OnClickListener {
	private Spinner mTheme;
	private ArrayAdapter<CharSequence> mAdapter;
	private EditText mBody;
	private NetStorage netStorage;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);	
		ImageView menuBtn = (ImageView) findViewById(R.id.feedback_image);
		menuBtn.setOnClickListener(this);
		fillThemes(R.array.feedback_themes);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mBody = (EditText) findViewById(R.id.feedback_message_edit);
		netStorage = new NetStorage(this);
		
	}
	
	private void fillThemes(int themes) {
		mTheme = (Spinner) findViewById(R.id.feedback_theme);
		mAdapter = ArrayAdapter.createFromResource(this,
					R.array.feedback_themes, R.layout.feedback_spinner_item);
		mAdapter.setDropDownViewResource(R.layout.dropdown_item);
		mTheme.setAdapter(mAdapter);		
	}
	
	public void onBtnClick(View v) {
		String theme = (String) mAdapter.getItem(mTheme.getSelectedItemPosition());
		String body = mBody.getText().toString();
		netStorage.sendFeedback(theme, body);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		finish();
	}
	

}
