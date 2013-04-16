package ru.inventos.yum;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import org.holoeverywhere.widget.Spinner;

public class FeedbackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);	 
		fillThemes(R.array.feedback_themes);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	private void fillThemes(int themes) {
		Spinner spinner = (Spinner) findViewById(R.id.feedback_theme);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.feedback_themes, R.layout.feedback_spinner_item);
		adapter.setDropDownViewResource(R.layout.feedback_spinner_item);
		spinner.setAdapter(adapter);		
	}
	

}
